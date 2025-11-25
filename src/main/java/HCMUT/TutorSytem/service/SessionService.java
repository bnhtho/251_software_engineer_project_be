package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.payload.request.SessionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SessionService {
    Page<SessionDTO> getAllSessions(Pageable pageable); // Chỉ dùng pagination
    SessionDTO createSession(SessionRequest request);
    SessionDTO updateSession(Integer id, SessionRequest request);
    void deleteSession(Integer id);
    Integer getTutorIdFromSession(Integer sessionId); // Get tutor ID to check ownership
}

