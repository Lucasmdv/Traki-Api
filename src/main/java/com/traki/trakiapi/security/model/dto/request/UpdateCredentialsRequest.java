package com.traki.trakiapi.security.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCredentialsRequest {

    private String newUsername;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String newPassword;

    private String currentPassword;
}
