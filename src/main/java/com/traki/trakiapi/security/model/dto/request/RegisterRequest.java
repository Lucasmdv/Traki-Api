package com.traki.trakiapi.security.model.dto.request;

import com.traki.trakiapi.models.dtos.request.UserRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Schema(name = "RegisterRequest", description = "Composite request to register user profile and credentials together")
public class RegisterRequest {
    @Schema(description = "User profile data")
    @NotNull(message = "User profile data is required")
    @Valid
    private UserRequest user;

    @Schema(description = "Credentials data")
    @NotNull(message = "Credentials data is required")
    @Valid
    private CredentialRequest credentials;
}
