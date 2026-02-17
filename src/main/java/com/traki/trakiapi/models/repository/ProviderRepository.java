package com.traki.trakiapi.models.repository;

import com.traki.trakiapi.models.entities.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider,Long> {
}
