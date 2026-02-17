package com.traki.trakiapi.models.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(name = "UserDetailsRequest", description = "Filter parameters for searching Users.")
public class UserDetailsRequest {

    @Schema(description = "User's first name to filter by", example = "Lucas")
    private String firstName;

    @Schema(description = "User's last name to filter by", example = "Gómez")
    private String lastName;

    @Schema(description = "User's DNI (National ID number)", example = "40555444")
    private String dni;
}
