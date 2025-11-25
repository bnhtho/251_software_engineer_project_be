package HCMUT.TutorSytem.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String role;
    private String hcmutId;
    private String academicStatus;
}

