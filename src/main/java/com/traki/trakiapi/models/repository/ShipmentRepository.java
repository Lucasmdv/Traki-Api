package com.traki.trakiapi.models.repository;

import com.traki.trakiapi.models.entities.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByUserId(Long userId);
    Shipment findByTrackingNumberAndProviderId(String trackingNumber, Long providerId);
}
