package com.traki.trakiapi.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="receiver_id",nullable = false, unique = true)
    public long Id;

    public String Name;

    public String Address;

    public String Organization;

    public String Email;

    public String City;

    public String Region;

    @Column(name="zip_code")
    public String ZipCode;

    public String Country;

    public String Phone;

}
