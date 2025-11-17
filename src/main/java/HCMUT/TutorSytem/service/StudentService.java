package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.StudentSessionHistoryDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;

import java.util.List;

public interface StudentService {
    StudentDTO getStudentProfile(Integer userId);
    List<StudentSessionHistoryDTO> getStudentSessionHistory(Integer userId);
    StudentDTO updateStudentProfile(Integer userId, StudentProfileUpdateRequest request);
}

