package com.traki.trakiapi.security.services;

import com.traki.trakiapi.models.entities.User;
import com.traki.trakiapi.models.repository.UserRepository;
import com.traki.trakiapi.security.exceptions.AuthenticationException;
import com.traki.trakiapi.security.model.CredentialsEntity;
import com.traki.trakiapi.security.model.RoleEntity;
import com.traki.trakiapi.security.model.dto.request.*;
import com.traki.trakiapi.security.repository.CredentialRepository;
import com.traki.trakiapi.security.repository.RolRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Transactional
@Service
public class AuthService {

    private final CredentialRepository credentialsRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RolRepository rolRepository;
    private final UserRepository userRepository;


    public CredentialsEntity register(RegisterRequest request) {
        if (request == null || request.getCredentials() == null || request.getUser() == null) {
            throw new AuthenticationException("Los datos de registro están incompletos. Verificá la información enviada.", null);
        }

        String username = request.getCredentials().getUsername();
        if (credentialsRepository.existsByUsername(username)) {
            throw new AuthenticationException("Ya existe un usuario registrado con el usuario: " + username, null);
        }

        // Create user profile
        User user = User.builder()
                .firstName(request.getUser().getFirstName())
                .lastName(request.getUser().getLastName())
                .dni(request.getUser().getDni())
                .build();
        user = userRepository.save(user);

        // Create credentials linked to profile
        CredentialsEntity credentials = CredentialsEntity.builder()
                .username(request.getCredentials().getUsername())
                .password(passwordEncoder.encode(request.getCredentials().getPassword()))
                .user(user)
                .build();

        // Assign default USER role (must exist)
        RoleEntity USERRole = rolRepository.findByName("USER")
                .orElseThrow(() -> new AuthenticationException("No se encontró el rol predeterminado USER. Creá el rol antes de continuar.", null));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(USERRole);
        credentials.setRoles(roles);

        return credentialsRepository.save(credentials);
    }

    public CredentialsEntity registerCredentials(RegisterCredentialsRequest request) {
        if (credentialsRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("Ya existe un usuario registrado con el usuario: " + request.getUsername(), null);
        }

        CredentialsEntity credentials = CredentialsEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // Assign default USER role
        RoleEntity USERRole = rolRepository.findByName("USER")
                .orElseThrow(() -> new AuthenticationException("No se encontró el rol predeterminado USER. Creá el rol antes de continuar.", null));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(USERRole);
        credentials.setRoles(roles);

        // Optionally link to existing user profile
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new UsernameNotFoundException("User profile not found with id: " + request.getUserId()));
            credentials.setUser(user);
        }

        return credentialsRepository.save(credentials);
    }

    /**
     * Registers a new user profile along with credentials in a single transaction-like operation.
     */
    public CredentialsEntity registerUserWithCredentials(RegisterUserCredentialsRequest request) {
        if (credentialsRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("Ya existe un usuario registrado con el usuario: " + request.getUsername(), null);
        }

        // Create user profile first
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dni(request.getDni())
                .build();
        user = userRepository.save(user);

        // Create credentials linked to the user
        CredentialsEntity credentials = CredentialsEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .user(user)
                .build();

        // Assign default USER role (must exist)
        RoleEntity USERRole = rolRepository.findByName("USER")
                .orElseThrow(() -> new AuthenticationException("No se encontró el rol predeterminado USER. Creá el rol antes de continuar.", null));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(USERRole);
        credentials.setRoles(roles);

        return credentialsRepository.save(credentials);
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param input Authentication request containing email and password
     * @return UserDetails of the authenticated user
     * @throws AuthenticationException If authentication fails
     * @throws UsernameNotFoundException If no user is found with the provided email
     */
    public UserDetails authenticate(AuthRequest input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.username(),
                            input.password()
                    )
            );

            return credentialsRepository.findByUsername(input.username())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + input.username()));
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("El correo o la contraseña son incorrectos.", e);
        } catch (Exception e) {
            throw new AuthenticationException("Ocurrió un error inesperado al intentar autenticar al usuario.", e);
        }
    }

    /**
     * Logs out the currently authenticated user.
     * Invalidates the JWT token.
     *
     * @throws UsernameNotFoundException If no user is found with the extracted email
     */
    public void logout() {
        String token = jwtService.extractTokenFromSecurityContext();
        String username = jwtService.extractUsername(token);
        credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        jwtService.invalidateToken(token);
    }

    public CredentialsEntity getAuthenticatedUser() {
        String token = jwtService.extractTokenFromSecurityContext();
        String username = jwtService.extractUsername(token);
        return credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public CredentialsEntity updateCredentials(String username, UpdateCredentialsRequest request) {
        CredentialsEntity user = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (request.getCurrentPassword() != null &&
                !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return credentialsRepository.save(user);
    }
}
