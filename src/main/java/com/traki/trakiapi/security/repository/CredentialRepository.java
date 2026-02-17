package com.traki.trakiapi.security.repository;
import com.traki.trakiapi.security.model.CredentialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialsEntity, Long> {

    Optional<CredentialsEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
}
