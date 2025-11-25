package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.*;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.StudentSessionMapper;
import HCMUT.TutorSytem.mapper.TutorMapper;
import HCMUT.TutorSytem.model.*;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorRequest;
import HCMUT.TutorSytem.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import HCMUT.TutorSytem.repo.MajorRepository;
import HCMUT.TutorSytem.repo.SubjectRepository;
import HCMUT.TutorSytem.repo.TutorProfileRepository;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.TutorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorServiceImp implements TutorService {

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private TutorScheduleRepository tutorScheduleRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private StudentSessionRepository studentSessionRepository;

    @Autowired
    private StudentSessionStatusRepository studentSessionStatusRepository;


    @Autowired
    private StudentScheduleRepository studentScheduleRepository;

    @Override
    public List<TutorDTO> getAllTutors() {
        List<TutorProfile> tutorProfiles = tutorProfileRepository.findAll();
        return tutorProfiles.stream()
                .map(TutorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TutorDTO createTutor(TutorRequest request) {
        // Assuming the current authenticated user is creating the tutor profile
        // TODO: Get current user from authentication context
        // For now, this will throw an error - need to get userId from JWT token
        // User user = getCurrentAuthenticatedUser();

        // Temporary: If you need to specify userId, add it to TutorRequest
        // For now, create a minimal user (this should be replaced with actual auth user)
        User user = new User();
        user.setRole("tutor");
        // Name comes from User datacore, not from request
        // user.setFirstName, user.setLastName should be already set


    @Override
    public TutorDetailDTO getTutorDetail(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found with id: " + userId));

        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for user id: " + userId));

        return mapToTutorDetailDTO(user, tutorProfile);
    }

    @Override
    @Transactional
    public TutorDetailDTO updateTutorProfile(Integer userId, TutorProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found with id: " + userId));

        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for user id: " + userId));

        // ...existing code...

        // Update subjects if provided
        if (request.getSubjectIds() != null && !request.getSubjectIds().isEmpty()) {
            tutorProfile.getSubjects().clear();
            for (Integer subjectId : request.getSubjectIds()) {
                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new DataNotFoundExceptions("Subject not found with id: " + subjectId));
                tutorProfile.getSubjects().add(subject);
            }
        }

        user = userRepository.save(user);
        tutorProfile = tutorProfileRepository.save(tutorProfile);

        return mapToTutorDetailDTO(user, tutorProfile);
    }
    // sửa
    private TutorDetailDTO mapToTutorDetailDTO(User user, TutorProfile tutorProfile) {
        TutorDetailDTO dto = new TutorDetailDTO();

        return TutorDetailMapper.toDTO(user, tutorProfile, schedules);
    }

    @Override
    public List<StudentSessionDTO> getPendingStudentSessions(Integer tutorId) {
        // Lấy các yêu cầu đăng ký đang PENDING cho các session của tutor
        List<StudentSession> pendingSessions = studentSessionRepository
                .findPendingSessionsByTutorId(tutorId, StudentSessionStatus.PENDING);

        return StudentSessionMapper.toDTOList(pendingSessions);
    }

    @Override
    @Transactional
    public StudentSessionDTO approveStudentSession(Integer tutorId, Integer studentSessionId) {
        // Kiểm tra quyền tutor
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + tutorId));

        if (!"TUTOR".equalsIgnoreCase(tutor.getRole())) {
            throw new IllegalArgumentException("User does not have tutor privileges");
        }

        // Tìm student session
        StudentSession studentSession = studentSessionRepository.findById(studentSessionId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student session not found with id: " + studentSessionId));

        // Kiểm tra quyền: session phải thuộc về tutor này
        if (!studentSession.getSession().getTutor().getId().equals(tutorId)) {
            throw new IllegalStateException("Bạn không có quyền duyệt yêu cầu này (session không thuộc về bạn)");
        }

        // Kiểm tra trạng thái phải là PENDING (tương tự AdminService kiểm tra PENDING)
        if (studentSession.getStudentSessionStatus().getId() != StudentSessionStatus.PENDING) {
            throw new IllegalStateException("Yêu cầu đăng ký không ở trạng thái chờ duyệt. Trạng thái hiện tại: "
                    + studentSession.getStudentSessionStatus().getName());
        }

        // Lock session để tránh race condition khi update currentQuantity
        Session session = sessionRepository.findByIdWithLock(studentSession.getSession().getId())
                .orElseThrow(() -> new DataNotFoundExceptions("Session not found"));

        // Kiểm tra lần 2: session còn chỗ không
        if (session.getCurrentQuantity() >= session.getMaxQuantity()) {
            // Session đã đầy, từ chối yêu cầu này
            StudentSessionStatus rejectedStatus = studentSessionStatusRepository.findById(StudentSessionStatus.REJECTED)
                    .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));
            studentSession.setStudentSessionStatus(rejectedStatus);
            studentSessionRepository.save(studentSession);

            throw new IllegalStateException("Buổi học đã đủ số lượng, không thể duyệt thêm");
        }

        // Còn chỗ -> approve
        // 1. Tăng currentQuantity
        session.setCurrentQuantity(session.getCurrentQuantity() + 1);
        sessionRepository.save(session);

        // 2. Update status của studentSession thành CONFIRMED
        StudentSessionStatus confirmedStatus = studentSessionStatusRepository.findById(StudentSessionStatus.CONFIRMED)
                .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus CONFIRMED not found"));
        studentSession.setStudentSessionStatus(confirmedStatus);

        // 3. Set confirmedDate
        studentSession.setConfirmedDate(Instant.now());

        // 4. Save (updatedDate sẽ tự động update nhờ @UpdateTimestamp)
        studentSession = studentSessionRepository.save(studentSession);

        // 5. Nếu đây là người cuối cùng được approve (session đầy), auto reject các pending còn lại
        if (session.getCurrentQuantity().equals(session.getMaxQuantity())) {
            List<StudentSession> remainingPending = studentSessionRepository
                    .findBySessionId(session.getId())
                    .stream()
                    .filter(ss -> ss.getStudentSessionStatus().getId() == StudentSessionStatus.PENDING)
                    .toList();

            StudentSessionStatus rejectedStatus = studentSessionStatusRepository.findById(StudentSessionStatus.REJECTED)
                    .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));

            for (StudentSession pending : remainingPending) {
                pending.setStudentSessionStatus(rejectedStatus);
                studentSessionRepository.save(pending);

                // Xóa schedule đã thêm trước đó
                if (session.getDayOfWeek() != null) {
                    studentScheduleRepository.deleteByStudentAndSessionInfo(
                        pending.getStudent().getId(),
                        session.getDayOfWeek(),
                        session.getStartTime(),
                        session.getEndTime()
                    );
                }
            }
        }

        return StudentSessionMapper.toDTO(studentSession);
    }

    @Override
    @Transactional
    public StudentSessionDTO rejectStudentSession(Integer tutorId, Integer studentSessionId) {
        // Kiểm tra quyền tutor
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + tutorId));

        if (!"TUTOR".equalsIgnoreCase(tutor.getRole())) {
            throw new IllegalArgumentException("User does not have tutor privileges");
        }

        // Tìm student session
        StudentSession studentSession = studentSessionRepository.findById(studentSessionId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student session not found with id: " + studentSessionId));

        // Kiểm tra quyền: session phải thuộc về tutor này
        if (!studentSession.getSession().getTutor().getId().equals(tutorId)) {
            throw new IllegalStateException("Bạn không có quyền từ chối yêu cầu này (session không thuộc về bạn)");
        }

        // Kiểm tra trạng thái phải là PENDING (tương tự AdminService)
        if (studentSession.getStudentSessionStatus().getId() != StudentSessionStatus.PENDING) {
            throw new IllegalStateException("Yêu cầu đăng ký không ở trạng thái chờ duyệt. Trạng thái hiện tại: "
                    + studentSession.getStudentSessionStatus().getName());
        }

        // Từ chối: chỉ update status, không tăng currentQuantity
        StudentSessionStatus rejectedStatus = studentSessionStatusRepository.findById(StudentSessionStatus.REJECTED)
                .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));
        studentSession.setStudentSessionStatus(rejectedStatus);

        // Save (updatedDate sẽ tự động update nhờ @UpdateTimestamp)
        studentSession = studentSessionRepository.save(studentSession);

        // Xóa schedule đã thêm trước đó khi đăng ký
        Session session = studentSession.getSession();
        if (session.getDayOfWeek() != null) {
            studentScheduleRepository.deleteByStudentAndSessionInfo(
                studentSession.getStudent().getId(),
                session.getDayOfWeek(),
                session.getStartTime(),
                session.getEndTime()
            );
        }

        return StudentSessionMapper.toDTO(studentSession);
    }

    @Override
    @Transactional
    public List<StudentSessionDTO> batchApproveStudentSessions(Integer tutorId, List<Integer> studentSessionIds) {
        List<StudentSessionDTO> results = new ArrayList<>();

        for (Integer studentSessionId : studentSessionIds) {
            try {
                StudentSessionDTO approved = approveStudentSession(tutorId, studentSessionId);
                results.add(approved);
            } catch (IllegalStateException e) {
                // Nếu session đã đầy, các yêu cầu còn lại sẽ bị reject tự động
                StudentSession studentSession = studentSessionRepository.findById(studentSessionId)
                        .orElseThrow(() -> new DataNotFoundExceptions("Student session not found with id: " + studentSessionId));

                // Reject yêu cầu này
                StudentSessionStatus rejectedStatus = studentSessionStatusRepository.findById(StudentSessionStatus.REJECTED)
                        .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));
                studentSession.setStudentSessionStatus(rejectedStatus);
                studentSession = studentSessionRepository.save(studentSession);

                // Xóa schedule đã thêm trước đó
                Session session = studentSession.getSession();
                if (session.getDayOfWeek() != null) {
                    studentScheduleRepository.deleteByStudentAndSessionInfo(
                        studentSession.getStudent().getId(),
                        session.getDayOfWeek(),
                        session.getStartTime(),
                        session.getEndTime()
                    );
                }

                results.add(StudentSessionMapper.toDTO(studentSession));
            }
        }

        return results;
    }
}
