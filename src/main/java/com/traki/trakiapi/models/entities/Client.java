package com.traki.trakiapi.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id", nullable = false, unique = true)
    private long Id;

    private String Name;

    private String Organization;

    private String Email;

    private String Address;

    private String City;

    @Column(name="zip_code")
    private String ZipCode;

    private String Country;
}
