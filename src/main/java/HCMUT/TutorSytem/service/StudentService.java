package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.StudentSessionHistoryDTO;
import HCMUT.TutorSytem.dto.StudentSessionDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    StudentDTO getStudentProfile(Integer userId);
    Page<StudentSessionHistoryDTO> getStudentSessionHistory(Integer userId, Pageable pageable); // Chỉ dùng pagination
    StudentDTO updateStudentProfile(Integer userId, StudentProfileUpdateRequest request);
    // API mới cho đăng ký session
    Page<SessionDTO> getAvailableSessions(Pageable pageable); // Chỉ dùng pagination
    StudentSessionDTO registerSession(Integer studentId, Integer sessionId); // Đăng ký session

    /**
     * Lấy lịch học trong tuần của student (các session đã được confirm)
     * @param studentId ID của student
     * @param weekOffset Offset tuần (0 = tuần hiện tại, 1 = tuần sau, -1 = tuần trước)
     * @return Danh sách StudentSessionDTO của các buổi học trong tuần
     */
    List<StudentSessionDTO> getWeekSchedule(Integer studentId, Integer weekOffset);
}



