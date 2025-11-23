package HCMUT.TutorSytem.mapper;

import HCMUT.TutorSytem.dto.StudentSessionDTO;
import HCMUT.TutorSytem.dto.StudentSessionHistoryDTO;
import HCMUT.TutorSytem.model.StudentSession;
import HCMUT.TutorSytem.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StudentSessionMapper {

    public static StudentSessionDTO toDTO(StudentSession studentSession) {
        if (studentSession == null) {
            return null;
        }

        StudentSessionDTO dto = new StudentSessionDTO();
        dto.setId(studentSession.getId());
        dto.setRegisteredDate(studentSession.getRegisteredDate());
        dto.setConfirmedDate(studentSession.getConfirmedDate());
        dto.setUpdatedDate(studentSession.getUpdatedDate());

        if (studentSession.getStudentSessionStatus() != null) {
            dto.setStatus(studentSession.getStudentSessionStatus().getName());
        }

        if (studentSession.getStudent() != null) {
            dto.setStudentId(studentSession.getStudent().getId());
            User student = studentSession.getStudent();
            dto.setStudentName((student.getFirstName() != null ? student.getFirstName() : "") + " " +
                    (student.getLastName() != null ? student.getLastName() : ""));
        }

        if (studentSession.getSession() != null) {
            dto.setSessionId(studentSession.getSession().getId());
            dto.setSessionStartTime(studentSession.getSession().getStartTime());
            dto.setSessionEndTime(studentSession.getSession().getEndTime());
            dto.setSessionDayOfWeek(studentSession.getSession().getDayOfWeek());
            dto.setSessionFormat(studentSession.getSession().getFormat());
            dto.setSessionLocation(studentSession.getSession().getLocation());

            if (studentSession.getSession().getSubject() != null) {
                dto.setSessionSubject(studentSession.getSession().getSubject().getName());
            }
        }

        return dto;
    }

    public static List<StudentSessionDTO> toDTOList(List<StudentSession> studentSessions) {
        if (studentSessions == null) {
            return Collections.emptyList();
        }
        return studentSessions.stream()
                .map(StudentSessionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi StudentSession entity sang StudentSessionHistoryDTO
     */
    public static StudentSessionHistoryDTO toStudentSessionHistoryDTO(StudentSession studentSession) {
        if (studentSession == null) {
            return null;
        }

        StudentSessionHistoryDTO dto = new StudentSessionHistoryDTO();
        dto.setStudentSessionId(studentSession.getId());
        dto.setRegisteredDate(studentSession.getRegisteredDate());
        dto.setUpdatedDate(studentSession.getUpdatedDate());

        // Thông tin trạng thái đăng ký
        if (studentSession.getStudentSessionStatus() != null) {
            dto.setRegistrationStatus(studentSession.getStudentSessionStatus().getName());
        }

        // Thông tin Session
        if (studentSession.getSession() != null) {
            dto.setSessionId(studentSession.getSession().getId());
            dto.setStartTime(studentSession.getSession().getStartTime());
            dto.setEndTime(studentSession.getSession().getEndTime());
            dto.setFormat(studentSession.getSession().getFormat());
            dto.setLocation(studentSession.getSession().getLocation());
            dto.setDayOfWeek(studentSession.getSession().getDayOfWeek());
            dto.setSessionStatus(studentSession.getSession().getSessionStatus().getName());

            // Thông tin Tutor từ Session
            if (studentSession.getSession().getTutor() != null) {
                User tutor = studentSession.getSession().getTutor();
                dto.setTutorName((tutor.getFirstName() != null ? tutor.getFirstName() : "") + " " +
                        (tutor.getLastName() != null ? tutor.getLastName() : ""));
            }

            // Thông tin Subject từ Session
            if (studentSession.getSession().getSubject() != null) {
                dto.setSubjectName(studentSession.getSession().getSubject().getName());
            }
        }

        return dto;
    }
}

