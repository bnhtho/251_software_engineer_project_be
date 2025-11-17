package HCMUT.TutorSytem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class TutorDetailDTO {
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
    private Instant createdDate;
    private Instant updateDate;
    private Instant lastLogin;

    // From TutorProfile
    private Long tutorProfileId;
    private Long majorId;
    private String majorName;
    private String department; // From major.department.name
    private String bio;
    private List<SubjectDTO> subjects; // List of subjects the tutor teaches
    private BigDecimal rating;
    private Integer experienceYears;
    private Long totalSessionsCompleted;
    private Boolean isAvailable;

    // Teaching schedule
    private List<TutorScheduleDTO> schedules;
}

