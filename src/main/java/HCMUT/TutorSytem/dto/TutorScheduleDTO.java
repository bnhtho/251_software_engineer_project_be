package HCMUT.TutorSytem.dto;

import HCMUT.TutorSytem.Enum.DayOfWeek;
import lombok.Data;

import java.time.Instant;
import java.time.LocalTime;

@Data
public class TutorScheduleDTO {
    private Integer id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Instant createdDate;
    private Instant updateDate;
}


