package HCMUT.TutorSytem.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class StudentSessionHistoryDTO {
    private Long studentSessionId;
    private Long sessionId;
    private String tutorName;
    private String subjectName;
    private String subjectCode;
    private Instant startTime;
    private Instant endTime;
    private String format;
    private String location;
    private String sessionStatus; // Session status
    private String registrationStatus; // StudentSession status
    private Instant registeredDate;
    private Instant updatedDate;
}

