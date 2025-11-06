package org.example.axon.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Doctors")
public class Doctor {
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
    @Column(name = "Bio", length = 200)
    private String bio;

    @Column(name = "LicenseNumber", length = 50)
    private String licenseNumber;

    @Column(name = "ConsultationFee", precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(name = "Specialization", length = 100)
    private String specialization;

    //Mới thêm vào, mô tả quan hệ giữa bác sĩ và hospitalDeparttment
    //Ban đầu là quan hệ giữa appoinment với hospitalDepartment => không chính xác.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HospitalDepartmentId")
    private HospitalDepartment hospitalDepartment;

    @OneToMany(mappedBy = "doctor")
    private Set<Appointment> appointments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "doctor")
    private Set<DoctorAvailability> doctorAvailabilities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "doctor")
    private Set<Review> reviews = new LinkedHashSet<>();

}