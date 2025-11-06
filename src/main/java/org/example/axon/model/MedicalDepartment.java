package org.example.axon.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "MedicalDepartments")
public class MedicalDepartment {
    @Id
    @Column(name = "DepartmentID", nullable = false)
    private Integer id;

    @Column(name = "Name", length = 150)
    private String name;

    @Lob
    @Column(name = "Description")
    private String description;

    @Column(name = "HeadOfDepartment", length = 100)
    private String headOfDepartment;

    @Column(name = "Phone", length = 20)
    private String phone;

    @Column(name = "Email", length = 100)
    private String email;

    @OneToMany(mappedBy = "department")
    private Set<HospitalDepartment> hospitalDepartments = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "medicalDepartments")
    private Set<Condition> conditions = new LinkedHashSet<>();

}