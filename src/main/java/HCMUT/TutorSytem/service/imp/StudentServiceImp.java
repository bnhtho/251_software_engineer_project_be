package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.StudentSessionDTO;
import HCMUT.TutorSytem.dto.StudentSessionHistoryDTO;
import HCMUT.TutorSytem.model.Major;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.SessionMapper;
import HCMUT.TutorSytem.mapper.StudentMapper;
import HCMUT.TutorSytem.mapper.StudentSessionMapper;
import HCMUT.TutorSytem.model.*;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.repo.*;
import HCMUT.TutorSytem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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
    private StudentSessionStatusRepository studentSessionStatusRepository;


    @Autowired
    private StudentScheduleRepository studentScheduleRepository;

    @Override
    public StudentDTO getStudentProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student not found with id: " + userId));
        return StudentMapper.toStudentDTO(user);
    }

    @Override
    public List<StudentSessionHistoryDTO> getStudentSessionHistory(Integer userId) {
        List<StudentSession> studentSessions = studentSessionRepository.findByStudentId(userId);

        return studentSessions.stream()
                .map(StudentSessionMapper::toStudentSessionHistoryDTO)
                .collect(Collectors.toList());
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

        user = userRepository.save(user);
        return StudentMapper.toStudentDTO(user);
    }

    @Override
    public List<SessionDTO> getAvailableSessions() {
        List<Session> availableSessions = sessionRepository.findAvailableSessions(Instant.now());
        return availableSessions.stream()
                .map(SessionMapper::toDTO)
                .collect(Collectors.toList());
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

        // 5. Kiểm tra xung đột lịch trong StudentSchedule
        if (session.getDayOfWeek() != null) {
            boolean hasConflict = studentScheduleRepository.existsConflictingSchedule(
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

        StudentSessionStatus pendingStatus = studentSessionStatusRepository.findById(StudentSessionStatus.PENDING)
                .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus PENDING not found"));
        studentSession.setStudentSessionStatus(pendingStatus);

        // registeredDate sẽ tự động set bởi @CreationTimestamp
        studentSession = studentSessionRepository.save(studentSession);

        // 7. Thêm vào StudentSchedule để ngăn đăng ký session khác trùng giờ
        if (session.getDayOfWeek() != null) {
            StudentSchedule studentSchedule = new StudentSchedule();
            studentSchedule.setStudent(student);
            studentSchedule.setDayOfWeek(session.getDayOfWeek());
            studentSchedule.setStartTime(session.getStartTime().atZone(ZoneId.systemDefault()).toLocalTime());
            studentSchedule.setEndTime(session.getEndTime().atZone(ZoneId.systemDefault()).toLocalTime());
            studentScheduleRepository.save(studentSchedule);
        }

        // 8. Return DTO
        return StudentSessionMapper.toDTO(studentSession);
    }
}