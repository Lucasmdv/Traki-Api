package com.traki.trakiapi.config;

import com.traki.trakiapi.models.entities.User;
import com.traki.trakiapi.models.repository.UserRepository;
import com.traki.trakiapi.security.model.CredentialsEntity;
import com.traki.trakiapi.security.model.RoleEntity;
import com.traki.trakiapi.security.repository.CredentialRepository;
import com.traki.trakiapi.security.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Loads seed data on startup if the database is empty.
 * Safe to run with both ddl-auto=create and ddl-auto=update.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RolRepository roleRepository;
    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedRoles();
        seedUsers();
    }

    private void seedRoles() {
        if (roleRepository.count() > 0) return;

        roleRepository.saveAll(List.of(
                RoleEntity.builder().name("USER")   .description("Usuario estándar con acceso básico").build(),
                RoleEntity.builder().name("ADMIN")  .description("Administrador con acceso completo").build(),
                RoleEntity.builder().name("MANAGER").description("Gestor con acceso intermedio").build()
        ));
        log.info("[DataInitializer] Roles creados: USER, ADMIN, MANAGER");
    }

    private void seedUsers() {
        if (credentialRepository.count() > 0) return;

        RoleEntity userRole  = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Rol USER no encontrado"));
        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rol ADMIN no encontrado"));

        // --- Admin (contraseña = su DNI: 00000000) ---
        User adminProfile = userRepository.save(User.builder()
                .firstName("Admin")
                .lastName("Sistema")
                .dni("00000000")
                .dateOfRegistration(LocalDate.now())
                .build());

        credentialRepository.save(CredentialsEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("00000000"))
                .user(adminProfile)
                .roles(Set.of(adminRole, userRole))
                .build());

        // --- User de prueba (contraseña = su DNI: 11111111) ---
        User userProfile = userRepository.save(User.builder()
                .firstName("Usuario")
                .lastName("Prueba")
                .dni("11111111")
                .dateOfRegistration(LocalDate.now())
                .build());

        credentialRepository.save(CredentialsEntity.builder()
                .username("user")
                .password(passwordEncoder.encode("11111111"))
                .user(userProfile)
                .roles(Set.of(userRole))
                .build());

        log.info("[DataInitializer] Usuarios creados: admin (ADMIN+USER) pass=00000000, user (USER) pass=11111111");
    }
}
