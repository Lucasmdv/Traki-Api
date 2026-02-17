package com.traki.trakiapi.models.repository;

import com.traki.trakiapi.models.entities.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver,Long> {
}
