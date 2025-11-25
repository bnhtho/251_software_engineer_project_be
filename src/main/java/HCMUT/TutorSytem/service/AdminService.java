package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.dto.UserDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    // Student management
    StudentDTO updateStudentProfileByAdmin(Integer userId, StudentProfileUpdateRequest request);
    void deleteStudentProfile(Integer userId);

    // Tutor management
    TutorDetailDTO updateTutorProfileByAdmin(Integer userId, TutorProfileUpdateRequest request);
    void deleteTutorProfile(Integer userId);

    // User management
    Page<UserDTO> getAllUsers(Pageable pageable);
    void deleteUserProfile(Integer userId); // Admin delete either student or tutor profile

    // Session management
    SessionDTO updateSessionStatus(Integer sessionId, Integer adminId, String setStatus);

    Page<SessionDTO> getPendingSessions(Pageable pageable);
}

