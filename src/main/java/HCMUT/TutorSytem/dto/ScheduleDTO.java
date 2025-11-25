package HCMUT.TutorSytem.dto;

import HCMUT.TutorSytem.Enum.DayOfWeek;
import lombok.Data;

import java.time.Instant;

@Data
public class ScheduleDTO {
    private Integer id;
    private Integer sessionId;
    private DayOfWeek dayOfWeek;

    // startTime và endTime được lấy từ session để hiển thị
    private Instant startTime;
    private Instant endTime;

    // Thông tin bổ sung từ session
    private String subjectName;
    private String tutorName;
    private String format;
    private String location;

    private Instant createdDate;
    private Instant updateDate;
}

