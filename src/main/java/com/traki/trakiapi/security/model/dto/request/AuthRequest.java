package com.traki.trakiapi.security.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "AuthRequest", description = "DTO used for user authentication containing username and password.")
public record AuthRequest(

        @Schema(description = "Username used to authenticate.", example = "Carlos")
        String username,

        @Schema(description = "User password in plain text (must be encrypted before storage).", example = "P@ssw0rd")
        String password

) {
}
