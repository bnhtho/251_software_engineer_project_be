package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.StudentSessionDTO;
import HCMUT.TutorSytem.dto.StudentSessionHistoryDTO;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.SessionMapper;
import HCMUT.TutorSytem.mapper.StudentMapper;
import HCMUT.TutorSytem.mapper.StudentSessionMapper;
import HCMUT.TutorSytem.model.*;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.repo.*;
import HCMUT.TutorSytem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
public class StudentServiceImp implements StudentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentSessionRepository studentSessionRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RegistrationStatusRepository registrationStatusRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public StudentDTO getStudentProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student not found with id: " + userId));
        return StudentMapper.toStudentDTO(user);
    }

    @Override
    public Page<StudentSessionHistoryDTO> getStudentSessionHistory(Integer userId, Pageable pageable) {
        Page<StudentSession> studentSessionsPage = studentSessionRepository.findByStudentId(userId, pageable);

        return studentSessionsPage.map(StudentSessionMapper::toStudentSessionHistoryDTO);
    }

    @Override
    public StudentDTO updateStudentProfile(Integer userId, StudentProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student not found with id: " + userId));

        // Update user fields if provided
        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            user.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            user.setLastName(request.getLastName().trim());
        }

        if (request.getOtherMethodContact() != null) {
            user.setOtherMethodContact(request.getOtherMethodContact());
        }

        if (request.getMajorId() != null) {
            Major major = majorRepository.findById(request.getMajorId())
                    .orElseThrow(() -> new DataNotFoundExceptions("Major not found with id: " + request.getMajorId()));
            user.setMajor(major);
        }
        if(request.getDob() != null) {
            user.setDob(request.getDob());
        }
        if(request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if(request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
        if(request.getAcademicStatus() != null) {
            user.setAcademicStatus(request.getAcademicStatus());
        }

        user = userRepository.save(user);
        return StudentMapper.toStudentDTO(user);
    }

    @Override
    public Page<SessionDTO> getAvailableSessions(Pageable pageable) {
        Page<Session> availableSessionsPage = sessionRepository.findAvailableSessions(Instant.now(), pageable);
        return availableSessionsPage.map(SessionMapper::toDTO);
    }

    @Override
    @Transactional
    public StudentSessionDTO registerSession(Integer studentId, Integer sessionId) {
        // 1. Tìm student
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student not found with id: " + studentId));

        // 2. Tìm session
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new DataNotFoundExceptions("Session not found with id: " + sessionId));

        // 3. Kiểm tra session có available không (status = SCHEDULED, chưa bắt đầu, chưa đầy)
        if (session.getSessionStatus() == null || session.getSessionStatus().getId() != SessionStatus.SCHEDULED) {
            throw new IllegalStateException("Session is not available for registration");
        }

        Instant now = Instant.now();
        if (session.getStartTime().isBefore(now)) {
            throw new IllegalStateException("Session has already started or passed");
        }

        // Lưu ý: Không kiểm tra currentQuantity ở đây vì chỉ kiểm tra khi tutor approve
        // Cho phép nhiều student đăng ký (PENDING) hơn maxQuantity, tutor sẽ chọn ai được approve

        // 4. Kiểm tra student đã đăng ký session này chưa
        if (studentSessionRepository.findByStudentIdAndSessionId(studentId, sessionId).isPresent()) {
            throw new IllegalStateException("Student has already registered for this session");
        }

        if(Objects.equals(student.getId(), session.getTutor().getId())) {
            throw new IllegalStateException("Tutor cannot register for their own session");
        }

        // 5. Kiểm tra xung đột lịch trong Schedule
        // Sử dụng Instant từ session để kiểm tra conflict
        if (session.getDayOfWeek() != null) {
            boolean hasConflict = scheduleRepository.existsConflictingSchedule(
                    studentId,
                    session.getDayOfWeek(),
                    session.getStartTime(),
                    session.getEndTime()
            );

            if (hasConflict) {
                throw new IllegalStateException("Schedule conflict: Student already has a session at this time");
            }
        }

        // 6. Tạo StudentSession với status = PENDING
        StudentSession studentSession = new StudentSession();
        studentSession.setStudent(student);
        studentSession.setSession(session);

        RegistrationStatus pendingStatus = registrationStatusRepository.findById(RegistrationStatus.PENDING)
                .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus PENDING not found"));
        studentSession.setRegistrationStatus(pendingStatus);

        // registeredDate sẽ tự động set bởi @CreationTimestamp
        studentSession = studentSessionRepository.save(studentSession);

        // 7. Thêm vào Schedule để ngăn đăng ký session khác trùng giờ
        // Lưu sessionId thay vì startTime/endTime
        if (session.getDayOfWeek() != null) {
            Schedule schedule = new Schedule();
            schedule.setUser(student);
            schedule.setSession(session);
            schedule.setDayOfWeek(session.getDayOfWeek());
            scheduleRepository.save(schedule);
        }

        // 8. Return DTO
        return StudentSessionMapper.toDTO(studentSession);
    }

    @Override
    public List<StudentSessionDTO> getWeekSchedule(Integer studentId, Integer weekOffset) {
        // Kiểm tra student có tồn tại không
        if (!userRepository.existsById(studentId)) {
            throw new DataNotFoundExceptions("Student not found with id: " + studentId);
        }

        // Tính toán thời điểm bắt đầu và kết thúc của tuần
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault();

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

        // Query các StudentSession đã được CONFIRMED trong tuần
        List<StudentSession> studentSessions = studentSessionRepository.findStudentConfirmedSessionsInWeek(
                studentId,
                RegistrationStatus.CONFIRMED,
                startOfWeek,
                endOfWeek
        );

        return StudentSessionMapper.toDTOList(studentSessions);
    }
}

