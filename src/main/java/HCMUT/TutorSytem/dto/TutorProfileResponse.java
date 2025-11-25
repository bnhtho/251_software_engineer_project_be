package HCMUT.TutorSytem.dto;

<<<<<<< HEAD
import HCMUT.TutorSytem.Enum.TutorStatus;
=======
import HCMUT.TutorSytem.model.TutorStatus;
>>>>>>> main
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TutorProfileResponse {
    private Integer id;
    private String bio;
    private Integer experienceYears;
    private BigDecimal rating;
    private Integer totalSessionsCompleted;
    private Boolean isAvailable;
    private TutorStatus status;
    private List<SubjectDTO> subjects;
    private UserResponse user;
}

