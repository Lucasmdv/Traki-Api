package com.traki.trakiapi.security.controller;

import com.traki.trakiapi.security.model.CredentialsEntity;
import com.traki.trakiapi.security.model.RoleEntity;
import com.traki.trakiapi.security.model.dto.request.RoleAssignRequest;
import com.traki.trakiapi.security.model.dto.request.RoleCreateRequest;
import com.traki.trakiapi.security.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles & Permits", description = "Endpoints to manage roles and assigns")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @Operation(summary = "Create a dynamic role (optional permits)")
    public ResponseEntity<RoleEntity> createRole(@RequestBody RoleCreateRequest request) {
        RoleEntity created = roleService.createRole(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/assign")
    @Operation(summary = "Assign a role to a user by role id (add-only)")
    public ResponseEntity<CredentialsEntity> assignRole(@RequestBody RoleAssignRequest request) {
        CredentialsEntity updated = roleService.assignRoleToUserById(request.getEmail(), request.getRoleId());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{email}/set")
    @Operation(summary = "Set role to user by role id (permissions handled separately)")
    public ResponseEntity<CredentialsEntity> setRoleToUser(
            @PathVariable String email,
            @RequestBody RoleAssignRequest body
    ) {
        CredentialsEntity updated = roleService.assignRoleToUserById(email, body.getRoleId());
        return ResponseEntity.ok(updated);
    }
}
