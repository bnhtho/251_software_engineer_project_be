package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.dto.UserDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    // User management - Combined delete method
    void deleteUserProfile(Integer userId); // Xóa user profile (tự động xác định student hay tutor)

    // User management
    Page<UserDTO> getAllUsers(Pageable pageable); // Chỉ dùng pagination

    // Session management
    SessionDTO updateSessionStatus(Integer sessionId, Integer adminId, String setStatus);
}

