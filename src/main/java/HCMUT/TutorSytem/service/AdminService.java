package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    // User management - Combined delete method
    void deleteUserProfile(Integer userId); // Xóa user profile (tự động xác định student hay tutor)

    // User management
    Page<UserDTO> getAllUsers(Pageable pageable); // Chỉ dùng pagination
    UserDTO getUserProfile(Integer userId); // Lấy thông tin profile của user theo userId

    // Session management
    SessionDTO updateSessionStatus(Integer sessionId, Integer adminId, String setStatus);
    Page<SessionDTO> getPendingSessions(Pageable pageable); // Lấy danh sách session đang chờ duyệt
}

