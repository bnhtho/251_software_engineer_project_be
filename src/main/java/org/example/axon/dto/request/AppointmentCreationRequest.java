package org.example.axon.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for creating a new appointment.
 * Contains patient, doctor, schedule time, and department details.
 */
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreationRequest {
    private String patientId;
    private String doctorId;
    private LocalDate scheduleDate;
    private LocalTime scheduleTime;
    private LocalTime endTime;
    private String note;
    private int hospitalDepartmentId;
}
