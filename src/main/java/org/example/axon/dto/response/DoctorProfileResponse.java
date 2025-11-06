package org.example.axon.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.axon.dto.data.*;
@Data
@Setter
@Getter
@Builder

public class DoctorProfileResponse {

    private String id;
    private String image;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private LocalDate dateOfBirth;   // thay vì java.util.Date
    private String role;             // hoặc enum nếu muốn

    private String bio;
    private String licenseNumber;

    private CitizenDTO citizen;
    private HospitalDepartmentDTO department;

    private BigDecimal consultationFee;

    private List<ShiftDTO> shifts;
    private ClinicInfo clinicInfo;
}
