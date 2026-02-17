package com.traki.trakiapi.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
public class Waybill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waybill_id", nullable = false ,unique = true)
    public Long Id;
    public String WaybillNumber;
    public String Description;
    public String Address;
    public String Name;
    public String Organization;
    public String ZipCode;
    public String City;
    public String Phone;
    public String Date;
    public String Email;
    public Double Weight;
    public Double Width;
    public Double Height;
    public Double Depth;
    public Double Price;

    public Provider Provider;
    public Country Country;
    public Client Client;
}
