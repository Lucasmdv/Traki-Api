package com.traki.trakiapi.models.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse extends RepresentationModel<UserResponse> {

    @Schema(description = "Unique identifier of the user", example = "123")
    private Long id;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's DNI (national identity document number)", example = "12345678")
    private String dni;

    @Schema(description = "Date of registration", example = "2021-01-01")
    private String dateOfRegistration;
}
