package HCMUT.TutorSytem.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalTime;

@Data
public class TutorScheduleDTO {
    private Integer id;
    private Integer dayOfWeek; // 0=Sunday, 1=Monday...6=Saturday
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private Instant createdDate;
    private Instant updateDate;
}

