package HCMUT.TutorSytem.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class StudentDTO {
    private Long id;

    // From User (Datacore)
    private String hcmutId;
    private String firstName;
    private String lastName;
    private String profileImage;
    private String academicStatus;
    private LocalDate dob;
    private String phone;
    private String otherMethodContact;
    private String role;
    private Long majorId;
    private String majorName;
    private String department; // From major.department.name
    private Instant createdDate;
    private Instant updateDate;
    private Instant lastLogin;
}

