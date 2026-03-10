package com.traki.trakiapi.models.repository;

import com.traki.trakiapi.models.entities.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {
    List<TrackingEvent> findByShipmentIdOrderByEventDateDesc(Long shipmentId);
}
