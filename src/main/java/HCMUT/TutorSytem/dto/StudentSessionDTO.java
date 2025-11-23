package HCMUT.TutorSytem.dto;

import HCMUT.TutorSytem.Enum.DayOfWeek;
import lombok.Data;
import java.time.Instant;

@Data
public class StudentSessionDTO {
    private Integer id;
    private Integer studentId;
    private String studentName;
    private Integer sessionId;
    private String sessionSubject;
    private Instant sessionStartTime;
    private Instant sessionEndTime;
    private String sessionFormat;
    private DayOfWeek sessionDayOfWeek; // Thứ trong tuần của session
    private String status; // studentSessionStatus.name (PENDING, CONFIRMED, REJECTED)
    private Instant registeredDate;
    private Instant confirmedDate; // Thời điểm được confirm bởi tutor
    private Instant updatedDate;
    private String sessionLocation;
}

