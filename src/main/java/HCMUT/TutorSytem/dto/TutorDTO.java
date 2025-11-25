package HCMUT.TutorSytem.dto;

import HCMUT.TutorSytem.model.TutorStatus;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class TutorDTO {
    private Integer id;

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
    private Instant createdDate;
    private Instant updateDate;
    private Instant lastLogin;

    // From TutorProfile
    private String title;
    private Integer majorId;
    private String majorName;
    private String department; // From major.department.name
    private String description;
    private List<String> specializations; // List of subject names
    private Double rating;
    private Integer reviewCount;
    private Integer studentCount;
    private Integer experienceYears;
    private Boolean isAvailable;
    private TutorStatus status;
}
