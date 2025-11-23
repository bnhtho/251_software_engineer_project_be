package HCMUT.TutorSytem.dto;

import HCMUT.TutorSytem.Enum.DayOfWeek;
import lombok.Data;

import java.time.Instant;

@Data
public class StudentSessionHistoryDTO {
    private Integer studentSessionId;
    private Integer sessionId;
    private String tutorName;
    private String subjectName;
    private Instant startTime;
    private Instant endTime;
    private String format;
    private String location;
    private DayOfWeek dayOfWeek; // Thứ trong tuần
    private String sessionStatus; // Session status
    private String registrationStatus; // StudentSession status
    private Instant registeredDate;
    private Instant updatedDate;
}

