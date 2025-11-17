package HCMUT.TutorSytem.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class SessionDTO {
    private Integer id;
    private String tutorName;
    private List<String> studentNames; // List of registered students
    private String subjectName;
    private Instant startTime;
    private Instant endTime;
    private String format;
    private String location;
    private String status; // sessionStatus.name
    private Instant createdDate;
    private Instant updatedDate;
}

