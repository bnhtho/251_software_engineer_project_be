package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.dto.UserDTO;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.SessionMapper;
import HCMUT.TutorSytem.mapper.UserMapper;
import HCMUT.TutorSytem.model.Session;
import HCMUT.TutorSytem.model.SessionStatus;
import HCMUT.TutorSytem.model.Status;
import HCMUT.TutorSytem.model.User;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.repo.SessionRepository;
import HCMUT.TutorSytem.repo.SessionStatusRepository;
import HCMUT.TutorSytem.repo.StatusRepository;
import HCMUT.TutorSytem.repo.TutorProfileRepository;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.AdminService;
import HCMUT.TutorSytem.service.StudentService;
import HCMUT.TutorSytem.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;

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
    public StudentDTO updateStudentProfileByAdmin(Integer userId, StudentProfileUpdateRequest request) {
        // Admin can update any student profile
        return studentService.updateStudentProfile(userId, request);
    }

    @Override
    @Transactional
    public void deleteStudentProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student not found with id: " + userId));

        // Soft delete: set status to INACTIVE
        Status inactiveStatus = statusRepository.findByName("INACTIVE")
                .orElseThrow(() -> new DataNotFoundExceptions("Status INACTIVE not found"));

        user.setStatus(inactiveStatus);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public TutorDetailDTO updateTutorProfileByAdmin(Integer userId, TutorProfileUpdateRequest request) {
        // Admin can update any tutor profile
        return tutorService.updateTutorProfile(userId, request);
    }

    @Override
    @Transactional
    public void deleteTutorProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + userId));

        // Check if tutor profile exists
        tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for user id: " + userId));

        // Soft delete: set status to INACTIVE
        Status inactiveStatus = statusRepository.findByName("INACTIVE")
                .orElseThrow(() -> new DataNotFoundExceptions("Status INACTIVE not found"));

        user.setStatus(inactiveStatus);
        userRepository.save(user);
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(UserMapper::toDTO);
    }

    @Override
    @Transactional
    public void deleteUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found with id: " + userId));

        String role = user.getRole();
        if (role == null) {
            // default to soft delete
            Status inactiveStatus = statusRepository.findByName("INACTIVE")
                    .orElseThrow(() -> new DataNotFoundExceptions("Status INACTIVE not found"));
            user.setStatus(inactiveStatus);
            userRepository.save(user);
            return;
        }

        if (role.equalsIgnoreCase("STUDENT")) {
            deleteStudentProfile(userId);
        } else if (role.equalsIgnoreCase("TUTOR")) {
            deleteTutorProfile(userId);
        } else {
            // For other roles (ADMIN etc.) still allow soft delete
            Status inactiveStatus = statusRepository.findByName("INACTIVE")
                    .orElseThrow(() -> new DataNotFoundExceptions("Status INACTIVE not found"));
            user.setStatus(inactiveStatus);
            userRepository.save(user);
        }
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
        Page<Session> page = sessionRepository.findBySessionStatusId(SessionStatus.PENDING, pageable);
        return page.map(SessionMapper::toDTO);
    }
}

