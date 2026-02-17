package com.traki.trakiapi.security.controller;

import com.traki.trakiapi.models.dtos.response.UserResponse;
import com.traki.trakiapi.models.entities.User;
import com.traki.trakiapi.security.model.CredentialsEntity;
import com.traki.trakiapi.security.model.RoleEntity;
import com.traki.trakiapi.security.model.dto.request.AuthRequest;
import com.traki.trakiapi.security.model.dto.request.RegisterRequest;
import com.traki.trakiapi.security.model.dto.request.UpdateCredentialsRequest;
import com.traki.trakiapi.security.model.dto.response.AuthResponse;
import com.traki.trakiapi.security.model.dto.response.LoginResponse;
import com.traki.trakiapi.security.model.dto.response.RegisterResponse;
import com.traki.trakiapi.security.services.AuthService;
import com.traki.trakiapi.security.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations for user authentication")
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout the current user",
            description = "Invalidates the current user's session and JWT token",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("Logout successful");
    }


    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user and get token",
            description = "Validates user credentials and returns a JWT token for authenticated requests")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid request format")
    })
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "Authentication credentials") @RequestBody AuthRequest authRequest) {
        UserDetails userDetails = authService.authenticate(authRequest);
        String token = jwtService.generateToken(userDetails);
        CredentialsEntity cred = (CredentialsEntity) userDetails;

        java.util.Set<String> roles = cred.getRoles().stream()
                .filter(java.util.Objects::nonNull)
                .map(RoleEntity::getName)
                .filter(java.util.Objects::nonNull)
                .map(String::toUpperCase)
                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));

        // Map associated User entity to UserResponse (nullable)
        UserResponse userResp = null;
        User userEntity = cred.getUser();
        if (userEntity != null) {
            userResp = new com.traki.trakiapi.models.dtos.response.UserResponse();
            try {
                userResp.setId(userEntity.getId());
            } catch (Exception ignored) {
                // Some older models may use different field names; ignore if absent
            }
            userResp.setFirstName(userEntity.getFirstName());
            userResp.setLastName(userEntity.getLastName());
            userResp.setDni(userEntity.getDni());
            if (userEntity.getDateOfRegistration() != null) {
                userResp.setDateOfRegistration(userEntity.getDateOfRegistration().toString());
            }
        }

        return ResponseEntity.ok(
                LoginResponse.builder()
                        .token(token)
                        .user(userResp)
                        .roles(roles)
                        .build());
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register user profile and credentials",
            description = "Registers a new user profile and credentials in one step and assigns default USER role")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        System.out.println(request);
        CredentialsEntity saved = authService.register(request);
        String token = jwtService.generateToken(saved);

        java.util.Set<String> roles = saved.getRoles().stream()
                .filter(java.util.Objects::nonNull)
                .map(RoleEntity::getName)
                .filter(java.util.Objects::nonNull)
                .map(String::toUpperCase)
                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));

        return ResponseEntity.status(201).body(
                RegisterResponse.builder()
                        .token(token)
                        .roles(roles)
                        .build());
    }

    @PatchMapping("/update")
    @Operation(
            summary = "Actualizar correo electrónico y/o contraseña",
            description = "Permite al usuario autenticado modificar su correo o contraseña.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Datos actualizados correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateCredentials(@Valid @RequestBody UpdateCredentialsRequest request) {
        CredentialsEntity currentUser = authService.getAuthenticatedUser();
        CredentialsEntity updated = authService.updateCredentials(currentUser.getUsername(), request);
        String newToken = jwtService.generateToken(updated);
        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(newToken)
                        .message("Credenciales actualizadas correctamente")
                        .build());
    }

}
