package org.example.axon.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Patients")
public class Patient {
    @Id
    @Column(name = "UserId", nullable = false, length = 36)
    private String userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "UserId", nullable = false)
    private User users;

    @Column(name = "CitizenId", length = 50)
    private String citizenId;

    @Column(name = "Age", nullable = false)
    private Integer age;

    @Lob
    @Column(name = "Gender")
    private String gender;

    @Column(name = "EmergencyName", length = 100)
    private String emergencyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "AddressId")
    private Address address;

    @Column(name = "EmergencyPhone", length = 20)
    private String emergencyPhone;

    @OneToMany(mappedBy = "patient")
    private Set<Appointment> appointments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "patient")
    private Set<PatientCondition> patientConditions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "patient")
    private Set<PatientInsurance> patientInsurances = new LinkedHashSet<>();

    @OneToMany(mappedBy = "patient")
    private Set<Review> reviews = new LinkedHashSet<>();

}