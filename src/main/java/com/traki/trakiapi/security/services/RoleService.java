package com.traki.trakiapi.security.services;

import com.traki.trakiapi.security.model.CredentialsEntity;
import com.traki.trakiapi.security.model.RoleEntity;
import com.traki.trakiapi.security.model.dto.request.RoleCreateRequest;
import com.traki.trakiapi.security.repository.CredentialRepository;
import com.traki.trakiapi.security.repository.RolRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Transactional
@Service
public class RoleService {

    private final CredentialRepository credentialsRepository;
    private final RolRepository rolRepository;

    public RoleService(CredentialRepository credentialsRepository,
                       RolRepository rolRepository) {
        this.credentialsRepository = credentialsRepository;
        this.rolRepository = rolRepository;
    }

    // Removed legacy methods that mixed role and permit updates in one call.

    public RoleEntity createRole(RoleCreateRequest request) {
        String normalized = request.getName().toUpperCase(Locale.ROOT);
        RoleEntity role = rolRepository.findByName(normalized).orElse(null);
        if (role == null) {
            role = RoleEntity.builder()
                    .name(normalized)
                    .description(request.getDescription())
                    .build();
        } else {
            role.setDescription(request.getDescription());
        }

        return rolRepository.save(role);
    }

    public CredentialsEntity assignRoleToUserById(String username, Long roleId) {
        RoleEntity role = rolRepository.findById(roleId)
                .orElseThrow(() -> new UsernameNotFoundException("Role not found with id: " + roleId));
        CredentialsEntity credentials = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Set<RoleEntity> roles = credentials.getRoles();
        if (roles == null) roles = new HashSet<>(); else roles = new HashSet<>(roles);
        roles.add(role);
        credentials.setRoles(roles);
        return credentialsRepository.save(credentials);
    }
}
