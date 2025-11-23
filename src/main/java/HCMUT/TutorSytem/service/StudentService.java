package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.StudentSessionHistoryDTO;
import HCMUT.TutorSytem.dto.StudentSessionDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;

import java.util.List;

public interface StudentService {
    StudentDTO getStudentProfile(Integer userId);
    List<StudentSessionHistoryDTO> getStudentSessionHistory(Integer userId);
    StudentDTO updateStudentProfile(Integer userId, StudentProfileUpdateRequest request);
    // API mới cho đăng ký session
    List<SessionDTO> getAvailableSessions(); // Lấy danh sách session khả dụng
    StudentSessionDTO registerSession(Integer studentId, Integer sessionId); // Đăng ký session
}



