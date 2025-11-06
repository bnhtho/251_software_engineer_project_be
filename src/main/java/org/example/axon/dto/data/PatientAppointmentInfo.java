package org.example.axon.dto.data;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class PatientAppointmentInfo {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
}
