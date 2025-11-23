package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.Enum.DayOfWeek;
import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.SessionMapper;
import HCMUT.TutorSytem.model.*;
import HCMUT.TutorSytem.payload.request.SessionRequest;
import HCMUT.TutorSytem.repo.*;
import HCMUT.TutorSytem.service.SessionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionServiceImp implements SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SessionStatusRepository sessionStatusRepository;


    @Autowired
    private TutorScheduleRepository tutorScheduleRepository;

    @Override
    public List<SessionDTO> getAllSessions() {
        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream()
                .map(SessionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SessionDTO createSession(SessionRequest request) {
        // Validation 1: Kiểm tra startTime và endTime
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }

        // Validation 2: Kiểm tra startTime < endTime
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Validation 3: Kiểm tra date phải trong tương lai
        Instant now = Instant.now();
        if (request.getStartTime().isBefore(now)) {
            throw new IllegalArgumentException("Session start time must be in the future");
        }

        Session session = new Session();

        User tutor = null;
        if (request.getTutorId() != null) {
            tutor = userRepository.findById(request.getTutorId())
                    .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + request.getTutorId()));
            session.setTutor(tutor);
        }

        // Students will register later through StudentSession table (N-N relationship)

        // Set subject
        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new DataNotFoundExceptions("Subject not found with id: " + request.getSubjectId()));
            session.setSubject(subject);
        }

        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setFormat(request.getFormat());
        session.setLocation(request.getLocation());

        // Set maxQuantity (bắt buộc, nếu không có thì dùng default 50)
        session.setMaxQuantity(request.getMaxQuantity() != null ? request.getMaxQuantity() : 50);

        // Set currentQuantity = 0 khi mới tạo
        session.setCurrentQuantity(0);

        // Set sessionStatus mặc định là PENDING(id = 1)
        SessionStatus scheduledStatus = sessionStatusRepository.findById(SessionStatus.PENDING)
                .orElseThrow(() -> new DataNotFoundExceptions("SessionStatus not found with id: " + SessionStatus.PENDING));
        session.setSessionStatus(scheduledStatus);

        session = sessionRepository.save(session);
        return SessionMapper.toDTO(session);
    }

    @Override
    public SessionDTO updateSession(Integer id, SessionRequest request) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundExceptions("Session not found with id: " + id));

        // Update tutor if provided
        if (request.getTutorId() != null) {
            User tutor = userRepository.findById(request.getTutorId())
                    .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + request.getTutorId()));
            session.setTutor(tutor);
        }

        // Student list is managed through StudentSession table, not updated here

        // Update subject if provided
        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new DataNotFoundExceptions("Subject not found with id: " + request.getSubjectId()));
            session.setSubject(subject);
        }

        // Only update non-null fields
        if (request.getStartTime() != null) {
            session.setStartTime(request.getStartTime());
        }

        if (request.getEndTime() != null) {
            session.setEndTime(request.getEndTime());
        }

        // Only update non-null and non-empty strings
        if (request.getFormat() != null && !request.getFormat().trim().isEmpty()) {
            session.setFormat(request.getFormat().trim());
        }

        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            session.setLocation(request.getLocation().trim());
        }

        // Only update status if provided
        if (request.getSessionStatusId() != null) {
            SessionStatus sessionStatus = sessionStatusRepository.findById(request.getSessionStatusId())
                    .orElseThrow(() -> new DataNotFoundExceptions("SessionStatus not found with id: " + request.getSessionStatusId()));
            session.setSessionStatus(sessionStatus);

            // Update maxQuantity if provided
            if (request.getMaxQuantity() != null) {
                session.setMaxQuantity(request.getMaxQuantity());
            }

        }
        return SessionMapper.toDTO(session);
    }
    @Override
    public void deleteSession(Integer id) {
        if (!sessionRepository.existsById(id)) {
            throw new DataNotFoundExceptions("Session not found with id: " + id);
        }
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundExceptions("Session not found with id: " + id));
        SessionStatus cancelledStatus = sessionStatusRepository.findById(SessionStatus.CANCELLED)
                .orElseThrow(() -> new DataNotFoundExceptions("SessionStatus not found with id: " + SessionStatus.CANCELLED));
        session.setSessionStatus(cancelledStatus);
        sessionRepository.save(session);
    }

    @Override
    public Integer getTutorIdFromSession(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new DataNotFoundExceptions("Session not found with id: " + sessionId));
        return session.getTutor() != null ? session.getTutor().getId() : null;
    }
}

