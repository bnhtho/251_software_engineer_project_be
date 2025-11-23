package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.StudentSessionDTO;
import HCMUT.TutorSytem.dto.TutorDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorRequest;

import java.util.List;

public interface TutorService {
    List<TutorDTO> getAllTutors();
    TutorDTO createTutor(TutorRequest request);
    TutorDTO updateTutor(Integer id, TutorRequest request);
    void deleteTutor(Integer id);
    Integer getUserIdFromTutorProfile(Integer tutorProfileId); // Get user ID to check ownership

    // New methods for profile management
    TutorDetailDTO getTutorDetail(Integer userId);
    TutorDetailDTO updateTutorProfile(Integer userId, TutorProfileUpdateRequest request);
    // API mới cho tutor duyệt/từ chối student
    List<StudentSessionDTO> getPendingStudentSessions(Integer tutorId); // Lấy danh sách yêu cầu chờ duyệt
    StudentSessionDTO approveStudentSession(Integer tutorId, Integer studentSessionId); // Approve student
    StudentSessionDTO rejectStudentSession(Integer tutorId, Integer studentSessionId); // Reject student
    List<StudentSessionDTO> batchApproveStudentSessions(Integer tutorId, List<Integer> studentSessionIds); // Approve nhiều student cùng lúc

}

