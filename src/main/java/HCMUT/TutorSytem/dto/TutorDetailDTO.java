package HCMUT.TutorSytem.dto;

import HCMUT.TutorSytem.Enum.TutorStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class TutorDetailDTO {
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
    private Integer tutorProfileId;
    private Integer majorId;
    private String majorName;
    private String department; // From major.department.name
    private String bio;
    private List<SubjectDTO> subjects; // List of subjects the tutor teaches
    private BigDecimal rating;
    private Integer experienceYears;
    private Integer totalSessionsCompleted;
    private Boolean isAvailable;
    private TutorStatus status;

    // Teaching schedule
    private List<TutorScheduleDTO> schedules;
}
