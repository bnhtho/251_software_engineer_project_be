package HCMUT.TutorSytem.dto;

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
    private String status;
    private List<SubjectDTO> subjects;
    private UserResponse user;
}

