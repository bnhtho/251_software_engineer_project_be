package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.SessionMapper;
import HCMUT.TutorSytem.model.Session;
import HCMUT.TutorSytem.model.SessionStatus;
import HCMUT.TutorSytem.model.Subject;
import HCMUT.TutorSytem.model.User;
import HCMUT.TutorSytem.payload.request.SessionRequest;
import HCMUT.TutorSytem.repo.SessionRepository;
import HCMUT.TutorSytem.repo.SessionStatusRepository;
import HCMUT.TutorSytem.repo.SubjectRepository;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private SessionMapper sessionMapper;

    @Override
    public List<SessionDTO> getAllSessions() {
        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream()
                .map(sessionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SessionDTO createSession(SessionRequest request) {
        Session session = new Session();

        // Set tutor
        if (request.getTutorId() != null) {
            User tutor = userRepository.findById(request.getTutorId())
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

        // Set status - default to SCHEDULED (id=1) if not provided
        Byte statusId = request.getStatusId() != null ? request.getStatusId() : SessionStatus.SCHEDULED;
        SessionStatus sessionStatus = sessionStatusRepository.findById(statusId)
                .orElseThrow(() -> new DataNotFoundExceptions("SessionStatus not found with id: " + statusId));
        session.setSessionStatus(sessionStatus);

        session.setCreatedDate(Instant.now());

        session = sessionRepository.save(session);
        return sessionMapper.toDTO(session);
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
        if (request.getStatusId() != null) {
            SessionStatus sessionStatus = sessionStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new DataNotFoundExceptions("SessionStatus not found with id: " + request.getStatusId()));
            session.setSessionStatus(sessionStatus);
        }

        session.setUpdatedDate(Instant.now());

        session = sessionRepository.save(session);
        return sessionMapper.toDTO(session);
    }

    @Override
    public void deleteSession(Integer id) {
        if (!sessionRepository.existsById(id)) {
            throw new DataNotFoundExceptions("Session not found with id: " + id);
        }
        sessionRepository.deleteById(id);
    }

    @Override
    public Integer getTutorIdFromSession(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new DataNotFoundExceptions("Session not found with id: " + sessionId));
        return session.getTutor() != null ? session.getTutor().getId() : null;
    }
}

