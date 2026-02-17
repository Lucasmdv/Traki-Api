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
@Schema(name = "RegisterResponse", description = "Response with JWT token, user profile and permits")
public class RegisterResponse {
    @Schema(description = "JWT token for authenticated requests")
    private String token;

    @Schema(description = "Created user profile")
    private UserResponse user;

    @Schema(description = "Assigned roles (uppercase names, ordered, without duplicates)")
    private Set<String> roles;
}
