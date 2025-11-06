package org.example.axon.repository;

import org.example.axon.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for accessing Appointment data.
 * Provides methods to retrieve appointments by doctor or patient.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    /**
     * Find all appointments by both doctor ID and patient ID.
     */
    List<Appointment> findAllByDoctorUserIdAndPatientUserId(String doctorId, String patientId);

    /**
     * Find all appointments of a specific doctor.
     */
    List<Appointment> findAllByDoctorUserId(String doctorId);

    /**
     * Find all appointments of a specific patient.
     */
    List<Appointment> findAllByPatientUserId(String patientId);
}

