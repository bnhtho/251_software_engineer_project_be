package org.example.axon.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "Appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AppointmentID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DoctorId")
    private Doctor doctor;

    @Column(name = "ScheduleDate")
    private LocalDate scheduleDate;

    @Column(name = "ScheduleTime")
    private LocalTime scheduleTime;

    @Column(name = "EndTime")
    private LocalTime endTime;

    /**
     * Appointment status.
     * Valid values:
     * - "AVAILABLE" : The appointment slot is open and can be booked.
     * - "PENDING"   : Newly created appointment, waiting for confirmation.
     * - "CONFIRMED" : Appointment has been confirmed by the doctor or system.
     * - "COMPLETED" : Appointment has been cancelled by the patient or doctor.
     * - "CANCELED" : Appointment has been completed successfully.
     *
     * Default: "PENDING"
     */
    @ColumnDefault("'PENDING'")
    @Lob
    @Column(name = "Status", length = 20)
    private String status;

    @Lob
    @Column(name = "Note")
    private String note;

    @Column(name = "Created_at", updatable = false, nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

}