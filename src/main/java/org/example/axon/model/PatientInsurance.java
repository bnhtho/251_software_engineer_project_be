package org.example.axon.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "PatientInsurances")
public class PatientInsurance {
    @Id
    @Column(name = "InsuranceId", nullable = false, length = 50)
    private String insuranceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "PatientId")
    private Patient patient;

    @Column(name = "ProviderName", length = 100)
    private String providerName;

    @Column(name = "ExpiryDate")
    private LocalDate expiryDate;

    @Column(name = "Type", length = 100)
    private String type;

}