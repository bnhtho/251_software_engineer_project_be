package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.StudentSessionHistoryDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;

import java.util.List;

public interface StudentService {
    StudentDTO getStudentProfile(Long userId);
    List<StudentSessionHistoryDTO> getStudentSessionHistory(Long userId);
    StudentDTO updateStudentProfile(Long userId, StudentProfileUpdateRequest request);
}

