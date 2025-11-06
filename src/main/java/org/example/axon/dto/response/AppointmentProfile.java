package org.example.axon.dto.response;

import lombok.*;
import org.example.axon.dto.data.DoctorAppointmentInfo;
import org.example.axon.dto.data.PatientAppointmentInfo;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentProfile {
    private int id;
    private LocalDate scheduleDate;
    private LocalTime scheduleTime;
    private LocalTime endTime;
    private DoctorAppointmentInfo doctor;
    private PatientAppointmentInfo patient;
    private String status;
}
