package org.example.axon.mapper;

import org.example.axon.dto.request.AppointmentCreationRequest;
import org.example.axon.dto.response.AppointmentResponse;
import org.example.axon.model.Appointment;
import org.example.axon.model.Doctor;
import org.example.axon.model.Patient;
import org.example.axon.model.HospitalDepartment;

public class AppointmentMapper {

    /**
     * Map from AppointmentCreationRequest to Appointment entity.
     * Used when creating a new appointment.
     */
    public static Appointment toEntity(AppointmentCreationRequest request) {
        if (request == null) return null;

        Appointment appointment = new Appointment();
        appointment.setScheduleDate(request.getScheduleDate());
        appointment.setScheduleTime(request.getScheduleTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setNote(request.getNote());
        appointment.setStatus("pending"); // default status

        // Set relationship references by ID (assuming lazy loading)
        Patient patient = new Patient();
        patient.setUserId(request.getPatientId());
        appointment.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setUserId(request.getDoctorId());
        appointment.setDoctor(doctor);

        return appointment;
    }

    /**
     * Map from Appointment entity to AppointmentResponse DTO.
     * Used when returning appointment details to the client.
     */
    public static AppointmentResponse toResponse(Appointment appointment) {
        if (appointment == null) return null;

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient() != null ? appointment.getPatient().getUserId() : null)
                .doctorId(appointment.getDoctor() != null ? appointment.getDoctor().getUserId() : null)
                .scheduleDate(appointment.getScheduleDate())
                .scheduleTime(appointment.getScheduleTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .build();
    }
}
