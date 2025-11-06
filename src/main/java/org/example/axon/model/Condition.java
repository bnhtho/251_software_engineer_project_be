package org.example.axon.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Conditions")
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConditionID", nullable = false)
    private Integer id;

    @Column(name = "ConditionName", length = 150)
    private String conditionName;

    @Lob
    @Column(name = "Description")
    private String description;

    @Lob
    @Column(name = "Type")
    private String type;

    @OneToMany(mappedBy = "conditionID")
    private Set<PatientCondition> patientConditions = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "TreatedBy",
            joinColumns = @JoinColumn(name = "ConditionID"),
            inverseJoinColumns = @JoinColumn(name = "DepartmentID"))
    private Set<MedicalDepartment> medicalDepartments = new LinkedHashSet<>();

}