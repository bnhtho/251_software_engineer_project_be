package org.example.axon.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Response DTO representing appointment details returned to the client.
 */
@Data
@Getter
@Setter
@Builder
public class AppointmentResponse {
    private int id;
    private LocalDate scheduleDate;
    private LocalTime scheduleTime;
    private LocalTime endTime;
    private String doctorId;
    private String patientId;
    private String status;
}
