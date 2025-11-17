package HCMUT.TutorSytem.payload.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TutorProfileUpdateRequest {
    // User fields
    private String firstName;
    private String lastName;
    private String profileImage;
    private String academicStatus;
    private LocalDate dob;
    private String phone;
    private String otherMethodContact;
    private Long majorId;

    // TutorProfile fields
    private String bio;
    private List<Long> subjectIds; // List of subject IDs
    private Integer experienceYears;
    private Boolean isAvailable;
}

