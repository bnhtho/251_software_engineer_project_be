package HCMUT.TutorSytem.payload.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String profileImage;
    private String academicStatus;
    private LocalDate dob;
    private String phone;
    private String otherMethodContact;
    private Integer majorId;
}

