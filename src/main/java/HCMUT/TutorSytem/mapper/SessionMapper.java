package HCMUT.TutorSytem.mapper;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.model.Session;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SessionMapper {

    public static SessionDTO toDTO(Session session) {
        if (session == null) {
            return null;
        }

        SessionDTO dto = new SessionDTO();
        dto.setId(session.getId());

        // Map tutor name
        if (session.getTutor() != null) {
            dto.setTutorName(session.getTutor().getFirstName() + " " + session.getTutor().getLastName());
        }

        // Map student names from StudentSession
        if (session.getStudentSessions() != null && !session.getStudentSessions().isEmpty()) {
            dto.setStudentNames(
                session.getStudentSessions().stream()
                    .map(ss -> ss.getStudent().getFirstName() + " " + ss.getStudent().getLastName())
                    .collect(Collectors.toList())
            );
        } else {
            dto.setStudentNames(Collections.emptyList());
        }

        // Map subject name
        if (session.getSubject() != null) {
            dto.setSubjectName(session.getSubject().getName());
        }

        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setFormat(session.getFormat());
        dto.setLocation(session.getLocation());
        dto.setMaxQuantity(session.getMaxQuantity());
        dto.setCurrentQuantity(session.getCurrentQuantity());
        dto.setUpdatedDate(session.getUpdatedDate());
        dto.setStatus(session.getSessionStatus().getName());
        return dto;
    }

    public static List<SessionDTO> toDTOList(List<Session> sessions) {
        if (sessions == null) {
            return Collections.emptyList();
        }
        return sessions.stream()
                .map(SessionMapper::toDTO)
                .collect(Collectors.toList());
    }
}

