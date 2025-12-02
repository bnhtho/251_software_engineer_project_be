package HCMUT.TutorSytem.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class TutorProfileCreateRequest {
    private String description;
    private List<Integer> subjects;
    private Integer experienceYears;
}

