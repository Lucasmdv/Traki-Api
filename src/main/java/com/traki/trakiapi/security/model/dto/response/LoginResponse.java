package com.traki.trakiapi.security.model.dto.response;

import com.traki.trakiapi.models.dtos.response.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LoginResponse", description = "Response with JWT token, sanitized user profile, and permits")
public class LoginResponse {
    @Schema(description = "JWT token for authenticated requests")
    private String token;

    @Schema(description = "Authenticated user profile (sanitized)")
    private UserResponse user;

    @Schema(description = "Assigned roles (uppercase names, ordered, without duplicates)")
    private Set<String> roles;
}
