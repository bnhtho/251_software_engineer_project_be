package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.UserDTO;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.SessionMapper;
import HCMUT.TutorSytem.mapper.UserMapper;
import HCMUT.TutorSytem.model.Session;
import HCMUT.TutorSytem.model.SessionStatus;
import HCMUT.TutorSytem.model.Status;
import HCMUT.TutorSytem.model.User;
import HCMUT.TutorSytem.repo.*;
import HCMUT.TutorSytem.service.AdminService;
import HCMUT.TutorSytem.service.StudentService;
import HCMUT.TutorSytem.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImp implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TutorService tutorService;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionStatusRepository sessionStatusRepository;


    @Override
    @Transactional
    public void deleteUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found with id: " + userId));

        // Soft delete: set status to INACTIVE
        Status inactiveStatus = statusRepository.findByName("INACTIVE")
                .orElseThrow(() -> new DataNotFoundExceptions("Status INACTIVE not found"));

        user.setStatus(inactiveStatus);
        userRepository.save(user);

        // Log để biết đã xóa user nào (student hay tutor)
        String userType = user.getRole() != null ? user.getRole() : "Unknown";
        System.out.println("Admin deleted user profile - UserId: " + userId + ", Role: " + userType);
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(UserMapper::toDTO);
    }

    @Override
    public UserDTO getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found with id: " + userId));
        return UserMapper.toDTO(user);
    }

    @Override
    @Transactional
    public SessionDTO updateSessionStatus(Integer sessionId, Integer adminId, String setStatus) {
        // Kiểm tra quyền admin
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new DataNotFoundExceptions("Admin not found with id: " + adminId));

        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new IllegalArgumentException("User does not have admin privileges");
        }

        // Tìm session theo sessionId
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new DataNotFoundExceptions("Session not found with id: " + sessionId));

        // Kiểm tra session đang ở trạng thái PENDING
        if (session.getSessionStatus().getId() != SessionStatus.PENDING) {
            throw new IllegalStateException("Session is not in PENDING status. Current status: " + session.getSessionStatus().getName());
        }

        // Xác định status mới dựa vào setStatus parameter
        SessionStatus newStatus;
        if ("SCHEDULED".equalsIgnoreCase(setStatus)) {
            // Admin approve -> đổi sang SCHEDULED
            newStatus = sessionStatusRepository.findById(SessionStatus.SCHEDULED)
                    .orElseThrow(() -> new DataNotFoundExceptions("SessionStatus SCHEDULED not found"));
        } else if ("CANCELLED".equalsIgnoreCase(setStatus)) {
            // Admin reject -> đổi sang CANCELLED
            newStatus = sessionStatusRepository.findById(SessionStatus.CANCELLED)
                    .orElseThrow(() -> new DataNotFoundExceptions("SessionStatus CANCELLED not found"));
        } else {
            throw new IllegalArgumentException("Invalid setStatus parameter. Must be 'SCHEDULED' or 'CANCELLED'");
        }

        // Đổi status
        session.setSessionStatus(newStatus);

        // Lưu session (updatedDate sẽ tự động update nhờ @UpdateTimestamp)
        session = sessionRepository.save(session);

        // Map sang SessionDTO và trả về
        return SessionMapper.toDTO(session);
    }

    @Override
    public Page<SessionDTO> getPendingSessions(Pageable pageable) {
        // Lấy các session có status = PENDING (id = 1)
        Page<Session> pendingSessionsPage = sessionRepository.findBySessionStatusId(SessionStatus.PENDING, pageable);

        // Map sang SessionDTO
        return pendingSessionsPage.map(SessionMapper::toDTO);
    }
}

