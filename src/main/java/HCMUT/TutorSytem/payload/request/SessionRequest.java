package HCMUT.TutorSytem.payload.request;

import lombok.Data;

import java.time.Instant;

@Data
public class SessionRequest {
    private Integer tutorId;
    private Integer subjectId;
    private Instant startTime;
    private Instant endTime;
    private String format;
    private String location;
    private Byte statusId; // sessionStatus.id (1=SCHEDULED, 2=IN_PROGRESS, 3=COMPLETED, 4=CANCELLED)
}

