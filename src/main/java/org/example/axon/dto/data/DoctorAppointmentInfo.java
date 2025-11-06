package org.example.axon.dto.data;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAppointmentInfo {
    private String id;
    private String firstName;
    private String lastName;
    private BigDecimal consultationFee;
    private ClinicInfo clinicInfo;
}
