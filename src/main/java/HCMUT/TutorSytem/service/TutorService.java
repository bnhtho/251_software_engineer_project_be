package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentSessionDTO;
import HCMUT.TutorSytem.dto.TutorDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TutorService {
    Page<TutorDTO> getAllTutors(Pageable pageable); // Chỉ dùng pagination
    // New methods for profile management
    TutorDetailDTO getTutorDetail(Integer userId);
    TutorDetailDTO updateTutorProfile(Integer userId, TutorProfileUpdateRequest request);
    // API mới cho tutor duyệt/từ chối student
    Page<StudentSessionDTO> getPendingStudentSessions(Integer tutorId, Pageable pageable); // Chỉ dùng pagination
    List<StudentSessionDTO> approveStudentSessions(Integer tutorId, List<Integer> studentSessionIds); // Approve nhiều student cùng lúc (hoặc 1 student)
    List<StudentSessionDTO> rejectStudentSessions(Integer tutorId, List<Integer> studentSessionIds); // Reject nhiều student cùng lúc (hoặc 1 student)

    /**
     * Lấy lịch giảng dạy trong tuần của tutor (các session đang SCHEDULED)
     * @param tutorId ID của tutor
     * @param weekOffset Offset tuần (0 = tuần hiện tại, 1 = tuần sau, -1 = tuần trước)
     * @return Danh sách SessionDTO của các buổi học trong tuần
     */
    List<SessionDTO> getWeekSchedule(Integer tutorId, Integer weekOffset);
}

