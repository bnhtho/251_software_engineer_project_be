package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentSessionDTO;
import HCMUT.TutorSytem.dto.TutorDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.StudentSessionMapper;
import HCMUT.TutorSytem.mapper.TutorDetailMapper;
import HCMUT.TutorSytem.mapper.TutorMapper;
import HCMUT.TutorSytem.model.*;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.repo.*;
import HCMUT.TutorSytem.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private StudentSessionRepository studentSessionRepository;

    @Autowired
    private RegistrationStatusRepository registrationStatusRepository;


    @Override
    public Page<TutorDTO> getAllTutors(Pageable pageable) {
        Page<TutorProfile> tutorProfilesPage = tutorProfileRepository.findAll(pageable);
        return tutorProfilesPage.map(TutorMapper::toDTO);
    }




    @Override
    public TutorDetailDTO getTutorDetail(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found with id: " + userId));

        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for user id: " + userId));

        // Lấy schedules của tutor
        List<Schedule> schedules = scheduleRepository.findByUserId(user.getId());

        return TutorDetailMapper.toDTO(user, tutorProfile, schedules);
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

        // Lấy schedules của tutor
        List<Schedule> schedules = scheduleRepository.findByUserId(user.getId());

        return TutorDetailMapper.toDTO(user, tutorProfile, schedules);
    }

    @Override
    public Page<StudentSessionDTO> getPendingStudentSessions(Integer tutorId, Pageable pageable) {
        // Lấy các yêu cầu đăng ký đang PENDING cho các session của tutor với pagination
        Page<StudentSession> pendingSessionsPage = studentSessionRepository
                .findPendingSessionsByTutorId(tutorId, (byte) RegistrationStatus.PENDING, pageable);

        return pendingSessionsPage.map(StudentSessionMapper::toDTO);
    }

    @Override
    @Transactional
    public List<StudentSessionDTO> approveStudentSessions(Integer tutorId, List<Integer> studentSessionIds) {
        List<StudentSessionDTO> results = new ArrayList<>();

        for (Integer studentSessionId : studentSessionIds) {
            try {
                StudentSessionDTO approved = approve(tutorId, studentSessionId);
                results.add(approved);
            } catch (IllegalStateException e) {
                // Nếu session đã đầy, các yêu cầu còn lại sẽ bị reject tự động
                StudentSession studentSession = studentSessionRepository.findById(studentSessionId)
                        .orElseThrow(() -> new DataNotFoundExceptions("Student session not found with id: " + studentSessionId));

                // Reject yêu cầu này
                RegistrationStatus rejectedStatus = registrationStatusRepository.findById(RegistrationStatus.REJECTED)
                        .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));
                studentSession.setRegistrationStatus(rejectedStatus);
                studentSession = studentSessionRepository.save(studentSession);

                // Xóa schedule đã thêm trước đó bằng userId và sessionId
                Session session = studentSession.getSession();
                scheduleRepository.deleteByUserIdAndSessionId(
                    studentSession.getStudent().getId(),
                    session.getId()
                );

                results.add(StudentSessionMapper.toDTO(studentSession));
            }
        }

        return results;
    }

    @Override
    @Transactional
    public List<StudentSessionDTO> rejectStudentSessions(Integer tutorId, List<Integer> studentSessionIds) {
        List<StudentSessionDTO> results = new ArrayList<>();

        // Kiểm tra tutor tồn tại
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + tutorId));

        if (!"TUTOR".equalsIgnoreCase(tutor.getRole().getName())) {
            throw new IllegalArgumentException("User does not have tutor privileges");
        }

        for (Integer studentSessionId : studentSessionIds) {
            try {
                // Tìm student session
                StudentSession studentSession = studentSessionRepository.findById(studentSessionId)
                        .orElseThrow(() -> new DataNotFoundExceptions("Student session not found with id: " + studentSessionId));

                // Kiểm tra quyền: session phải thuộc về tutor này
                if (!studentSession.getSession().getTutor().getId().equals(tutorId)) {
                    throw new IllegalStateException("Bạn không có quyền từ chối yêu cầu này (session không thuộc về bạn)");
                }

                // Kiểm tra trạng thái phải là PENDING
                if (studentSession.getRegistrationStatus().getId() != RegistrationStatus.PENDING) {
                    throw new IllegalStateException("Yêu cầu đăng ký không ở trạng thái chờ duyệt. Trạng thái hiện tại: "
                            + studentSession.getRegistrationStatus().getName());
                }

                // Từ chối: chỉ update status, không tăng currentQuantity
                RegistrationStatus rejectedStatus = registrationStatusRepository.findById(RegistrationStatus.REJECTED)
                        .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));
                studentSession.setRegistrationStatus(rejectedStatus);

                // Save (updatedDate sẽ tự động update nhờ @UpdateTimestamp)
                studentSession = studentSessionRepository.save(studentSession);

                // Xóa schedule đã thêm trước đó khi đăng ký bằng userId và sessionId
                try {
                    Session session = studentSession.getSession();
                    scheduleRepository.deleteByUserIdAndSessionId(
                        studentSession.getStudent().getId(),
                        session.getId()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                results.add(StudentSessionMapper.toDTO(studentSession));
            } catch (DataNotFoundExceptions | IllegalStateException e) {
                throw e;
            }
        }

        return results;
    }

    @Override
    public List<SessionDTO> getWeekSchedule(Integer tutorId, Integer weekOffset) {
        // Kiểm tra tutor có tồn tại không
        if (!userRepository.existsById(tutorId)) {
            throw new DataNotFoundExceptions("Tutor not found with id: " + tutorId);
        }

        // Tính toán thời điểm bắt đầu và kết thúc của tuần
        Instant now = Instant.now();
        java.time.ZoneId zoneId = java.time.ZoneId.systemDefault();

        // Chuyển sang LocalDate để tính toán
        java.time.LocalDate currentDate = now.atZone(zoneId).toLocalDate();

        // Tìm ngày đầu tuần (Monday)
        java.time.LocalDate startOfCurrentWeek = currentDate.with(java.time.DayOfWeek.MONDAY);

        // Áp dụng offset
        java.time.LocalDate startOfTargetWeek = startOfCurrentWeek.plusWeeks(weekOffset);
        java.time.LocalDate endOfTargetWeek = startOfTargetWeek.plusDays(7);

        // Chuyển về Instant (lấy từ 00:00:00)
        Instant startOfWeek = startOfTargetWeek.atStartOfDay(zoneId).toInstant();
        Instant endOfWeek = endOfTargetWeek.atStartOfDay(zoneId).toInstant();

        // Query các Session đang SCHEDULED trong tuần của tutor
        List<Session> sessions = sessionRepository.findTutorScheduledSessionsInWeek(
                tutorId,
                SessionStatus.SCHEDULED,
                startOfWeek,
                endOfWeek
        );

        return sessions.stream()
                .map(session -> {
                    SessionDTO dto = new SessionDTO();
                    dto.setId(session.getId());
                    dto.setStartTime(session.getStartTime());
                    dto.setEndTime(session.getEndTime());
                    dto.setFormat(session.getFormat());
                    dto.setLocation(session.getLocation());
                    dto.setMaxQuantity(session.getMaxQuantity());
                    dto.setCurrentQuantity(session.getCurrentQuantity());
                    dto.setUpdatedDate(session.getUpdatedDate());

                    if (session.getTutor() != null) {
                        User tutor = session.getTutor();
                        dto.setTutorName((tutor.getFirstName() != null ? tutor.getFirstName() : "") + " " +
                                (tutor.getLastName() != null ? tutor.getLastName() : ""));
                    }

                    if (session.getSubject() != null) {
                        dto.setSubjectName(session.getSubject().getName());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    private StudentSessionDTO approve(Integer tutorId, Integer studentSessionId) {
        // Kiểm tra quyền tutor
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + tutorId));

        if (!"TUTOR".equalsIgnoreCase(tutor.getRole().getName())) {
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
        if (studentSession.getRegistrationStatus().getId() != RegistrationStatus.PENDING) {
            throw new IllegalStateException("Yêu cầu đăng ký không ở trạng thái chờ duyệt. Trạng thái hiện tại: "
                    + studentSession.getRegistrationStatus().getName());
        }

        // Lock session để tránh race condition khi update currentQuantity
        Session session = sessionRepository.findByIdWithLock(studentSession.getSession().getId())
                .orElseThrow(() -> new DataNotFoundExceptions("Session not found"));

        // Kiểm tra lần 2: session còn chỗ không
        if (session.getCurrentQuantity() >= session.getMaxQuantity()) {
            // Session đã đầy, từ chối yêu cầu này
            RegistrationStatus rejectedStatus = registrationStatusRepository.findById(RegistrationStatus.REJECTED)
                    .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));
            studentSession.setRegistrationStatus(rejectedStatus);
            studentSessionRepository.save(studentSession);

            throw new IllegalStateException("Buổi học đã đủ số lượng, không thể duyệt thêm");
        }

        // Còn chỗ -> approve
        // 1. Tăng currentQuantity
        session.setCurrentQuantity(session.getCurrentQuantity() + 1);
        sessionRepository.save(session);

        // 2. Update status của studentSession thành CONFIRMED
        RegistrationStatus confirmedStatus = registrationStatusRepository.findById(RegistrationStatus.CONFIRMED)
                .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus CONFIRMED not found"));
        studentSession.setRegistrationStatus(confirmedStatus);

        // 3. Set confirmedDate
        studentSession.setConfirmedDate(Instant.now());

        // 4. Save (updatedDate sẽ tự động update nhờ @UpdateTimestamp)
        studentSession = studentSessionRepository.save(studentSession);

        // 5. Nếu đây là người cuối cùng được approve (session đầy), auto reject các pending còn lại
        if (session.getCurrentQuantity().equals(session.getMaxQuantity())) {
            List<StudentSession> remainingPending = studentSessionRepository
                    .findBySessionId(session.getId())
                    .stream()
                    .filter(ss -> ss.getRegistrationStatus().getId() == RegistrationStatus.PENDING)
                    .toList();

            RegistrationStatus rejectedStatus = registrationStatusRepository.findById(RegistrationStatus.REJECTED)
                    .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));

            for (StudentSession pending : remainingPending) {
                pending.setRegistrationStatus(rejectedStatus);
                studentSessionRepository.save(pending);

                // Xóa schedule đã thêm trước đó bằng userId và sessionId
                scheduleRepository.deleteByUserIdAndSessionId(
                    pending.getStudent().getId(),
                    session.getId()
                );
            }
        }

        return StudentSessionMapper.toDTO(studentSession);
    }
}
