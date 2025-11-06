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
@Table(name = "HospitalDepartments")
public class HospitalDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HospitalDepartmentId", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "HospitalId", nullable = false)
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "DepartmentId", nullable = false)
    private MedicalDepartment department;

    @Column(name = "Address")
    private String address;

    @OneToMany(mappedBy = "hospitalDepartment")
    private Set<Doctor> doctors = new LinkedHashSet<>();

    @OneToMany(mappedBy = "hospitalDepartment")
    private Set<DoctorAvailability> doctorAvailabilities = new LinkedHashSet<>();

}