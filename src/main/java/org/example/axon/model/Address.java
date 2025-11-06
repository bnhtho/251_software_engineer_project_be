package org.example.axon.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AddressId", nullable = false)
    private Integer id;

    @Column(name = "AddressLine")
    private String addressLine;

    @Column(name = "City", length = 100)
    private String city;

    @Column(name = "ProvinceState", length = 100)
    private String provinceState;

    @Column(name = "Country", length = 100)
    private String country;

    @OneToMany(mappedBy = "address")
    private Set<Patient> patients = new LinkedHashSet<>();

}