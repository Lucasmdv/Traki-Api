package com.traki.trakiapi.security.services;

import com.traki.trakiapi.security.model.CredentialsEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


@Transactional
@Service
@RequiredArgsConstructor
public class JwtService {


    @Value("${jwt.secret:}")
    private String jwtSecretKey;

    @Value("${jwt.expiration:0}")
    private Long jwtExpiration;


    private final Set<String> invalidatedToken  = ConcurrentHashMap.newKeySet();

    @PostConstruct
    private void initFromEnvIfNeeded() {
        if (jwtSecretKey == null || jwtSecretKey.isBlank()) {
            // Try common env var names
            jwtSecretKey = System.getenv("JWT_SECRET");
            if (jwtSecretKey == null || jwtSecretKey.isBlank()) {
                jwtSecretKey = System.getenv("JWT.SECRET");
            }
        }

        if ((jwtExpiration == null || jwtExpiration == 0L)) {
            String exp = System.getenv("JWT_EXPIRATION");
            if (exp == null || exp.isBlank()) {
                exp = System.getenv("JWT_EXPIRATION_MS");
            }
            if (exp != null && !exp.isBlank()) {
                try {
                    jwtExpiration = Long.parseLong(exp);
                } catch (NumberFormatException ignored) {
                    // ignore and keep default 0
                }
            }
        }

        // If still not set, provide a safe default: 24 hours
        if (jwtExpiration == null || jwtExpiration == 0L) {
            jwtExpiration = 24 * 60 * 60 * 1000L; // 24 hours in ms
        }

        if (jwtSecretKey == null || jwtSecretKey.isBlank()) {
            // This is a risky state but avoid null pointer later by throwing a clear exception
            throw new IllegalStateException("JWT secret key is not configured. Set 'jwt.secret' property or JWT_SECRET env var.");
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Build roles claim: uppercase enum names, ordered, no duplicates
        Set<String> roles = new TreeSet<>();

        if (userDetails instanceof CredentialsEntity cred) {
            claims.put("username", cred.getUsername());
            cred.getRoles().stream()
                    .filter(Objects::nonNull)
                    .map(r -> r.getName())
                    .filter(Objects::nonNull)
                    .map(String::toUpperCase)
                    .forEach(roles::add);
        } else {
            // Fallback: derive from GrantedAuthorities excluding ROLE_*
            for (org.springframework.security.core.GrantedAuthority a : userDetails.getAuthorities()) {
                String auth = a.getAuthority();
                if (auth != null && !auth.startsWith("ROLE_")) {
                    roles.add(auth);
                }
            }
        }

        if (!roles.isEmpty()) {
            claims.put("roles", roles);
        }

        return buildToken(claims, userDetails, jwtExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Use reflection to support different jjwt versions at runtime/compile time in analysis environment
        try {
            // Prefer parserBuilder() -> build() -> parseClaimsJws(...)
            Method parserBuilderMethod = Jwts.class.getMethod("parserBuilder");
            Object builder = parserBuilderMethod.invoke(null);
            Method setKey = builder.getClass().getMethod("setSigningKey", Key.class);
            Object afterSet = setKey.invoke(builder, getSignInKey());
            Method build = afterSet.getClass().getMethod("build");
            Object parser = build.invoke(afterSet);
            Method parse = parser.getClass().getMethod("parseClaimsJws", String.class);
            Object jws = parse.invoke(parser, token);
            Method getBody = jws.getClass().getMethod("getBody");
            return (Claims) getBody.invoke(jws);
        } catch (NoSuchMethodException e) {
            try {
                // Fallback: reflection-based call to parser() -> setSigningKey(...) -> parseClaimsJws(...)
                Method parserMethod = Jwts.class.getMethod("parser");
                Object parser = parserMethod.invoke(null);
                Method setKey = parser.getClass().getMethod("setSigningKey", Key.class);
                Object afterSet = setKey.invoke(parser, getSignInKey());
                Method parse = afterSet.getClass().getMethod("parseClaimsJws", String.class);
                Object jws = parse.invoke(afterSet, token);
                Method getBody = jws.getClass().getMethod("getBody");
                return (Claims) getBody.invoke(jws);
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException("Error al parsear el token JWT (fallback)", ex);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error al parsear el token JWT", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()))
                && !isTokenExpired(token)
                && userDetails.isAccountNonLocked()
                && userDetails.isEnabled();
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {
        Instant now = Instant.now();
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiration)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public void invalidateToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        invalidatedToken.add(token);
    }

    public String extractTokenFromSecurityContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se pudo obtener la request actual");
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no válido o no presente");
        }

        return authHeader.substring(7);
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedToken.contains(token);
    }
}
