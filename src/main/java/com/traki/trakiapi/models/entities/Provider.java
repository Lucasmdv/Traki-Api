package com.traki.trakiapi.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Set;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "business_name", nullable = false, unique = true)
    private String businessName;

    @Column(nullable = false, length = 100, unique = true)
    private String taxId;

    @Column(name = "tax_address", nullable = false)
    private String taxAddress;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

}