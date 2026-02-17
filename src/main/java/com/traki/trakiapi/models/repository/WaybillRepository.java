package com.traki.trakiapi.models.repository;

import com.traki.trakiapi.models.entities.Waybill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaybillRepository extends JpaRepository<Waybill, Long> {
}
