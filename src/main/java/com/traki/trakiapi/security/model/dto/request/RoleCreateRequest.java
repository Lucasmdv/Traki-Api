package com.traki.trakiapi.security.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RoleCreateRequest", description = "DTO to create a dynamic role with optional description and permits")
public class RoleCreateRequest {
    @Schema(description = "Unique role name", example = "ADMIN")
    private String name;

    @Schema(description = "Optional role description")
    private String description;
}

