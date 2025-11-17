package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.payload.request.SessionRequest;

import java.util.List;

public interface SessionService {
    List<SessionDTO> getAllSessions();
    SessionDTO createSession(SessionRequest request);
    SessionDTO updateSession(Integer id, SessionRequest request);
    void deleteSession(Integer id);
    Integer getTutorIdFromSession(Integer sessionId); // Get tutor ID to check ownership
}

