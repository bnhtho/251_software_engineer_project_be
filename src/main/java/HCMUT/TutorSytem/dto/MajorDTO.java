package HCMUT.TutorSytem.dto;

import lombok.Data;

@Data
public class MajorDTO {
    private Integer id;
    private String name;
    private String majorCode;
    private String programCode;
    private String note;
    private Integer departmentId;
    private String departmentName;
}

