package org.example.axon.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Hospitals")
public class Hospital {
    @Id
    @Column(name = "HospitalID", nullable = false)
    private Integer id;

    @Column(name = "Name", length = 150)
    private String name;

    @Lob
    @Column(name = "Type")
    private String type;

    @Column(name = "Address")
    private String address;

    @Column(name = "Hotline", length = 20)
    private String hotline;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "OpenTime")
    private LocalTime openTime;

    @Column(name = "CloseTime")
    private LocalTime closeTime;

    @OneToMany(mappedBy = "hospital")
    private Set<HospitalDepartment> hospitalDepartments = new LinkedHashSet<>();

}