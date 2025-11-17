package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.StudentSessionHistoryDTO;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.model.Major;
import HCMUT.TutorSytem.model.StudentSession;
import HCMUT.TutorSytem.model.User;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.repo.MajorRepository;
import HCMUT.TutorSytem.repo.StudentSessionRepository;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public StudentDTO getStudentProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student not found with id: " + userId));

        return mapToStudentDTO(user);
    }

    @Override
    public List<StudentSessionHistoryDTO> getStudentSessionHistory(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundExceptions("Student not found with id: " + userId);
        }

        List<StudentSession> studentSessions = studentSessionRepository.findByStudentId(userId);

        return studentSessions.stream()
                .map(this::mapToStudentSessionHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentDTO updateStudentProfile(Long userId, StudentProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student not found with id: " + userId));

        // Update user fields if provided
        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            user.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            user.setLastName(request.getLastName().trim());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
        if (request.getAcademicStatus() != null && !request.getAcademicStatus().trim().isEmpty()) {
            user.setAcademicStatus(request.getAcademicStatus().trim());
        }
        if (request.getDob() != null) {
            user.setDob(request.getDob());
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            user.setPhone(request.getPhone().trim());
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
        return mapToStudentDTO(user);
    }

    private StudentDTO mapToStudentDTO(User user) {
        StudentDTO dto = new StudentDTO();
        dto.setId(user.getId());
        dto.setHcmutId(user.getHcmutId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setProfileImage(user.getProfileImage());
        dto.setAcademicStatus(user.getAcademicStatus());
        dto.setDob(user.getDob());
        dto.setPhone(user.getPhone());
        dto.setOtherMethodContact(user.getOtherMethodContact());
        dto.setRole(user.getRole());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setUpdateDate(user.getUpdateDate());
        dto.setLastLogin(user.getLastLogin());

        if (user.getMajor() != null) {
            dto.setMajorId(user.getMajor().getId());
            dto.setMajorName(user.getMajor().getName());
            if (user.getMajor().getDepartment() != null) {
                dto.setDepartment(user.getMajor().getDepartment().getName());
            }
        }

        return dto;
    }

    private StudentSessionHistoryDTO mapToStudentSessionHistoryDTO(StudentSession studentSession) {
        StudentSessionHistoryDTO dto = new StudentSessionHistoryDTO();
        dto.setStudentSessionId(studentSession.getId());
        dto.setRegisteredDate(studentSession.getRegisteredDate());
        dto.setUpdatedDate(studentSession.getUpdatedDate());

        if (studentSession.getStudentSessionStatus() != null) {
            dto.setRegistrationStatus(studentSession.getStudentSessionStatus().getName());
        }

        if (studentSession.getSession() != null) {
            dto.setSessionId(studentSession.getSession().getId());
            dto.setStartTime(studentSession.getSession().getStartTime());
            dto.setEndTime(studentSession.getSession().getEndTime());
            dto.setFormat(studentSession.getSession().getFormat());
            dto.setLocation(studentSession.getSession().getLocation());

            if (studentSession.getSession().getSessionStatus() != null) {
                dto.setSessionStatus(studentSession.getSession().getSessionStatus().getName());
            }

            if (studentSession.getSession().getTutor() != null) {
                User tutor = studentSession.getSession().getTutor();
                dto.setTutorName((tutor.getFirstName() != null ? tutor.getFirstName() : "") + " " +
                        (tutor.getLastName() != null ? tutor.getLastName() : ""));
            }

            if (studentSession.getSession().getSubject() != null) {
                dto.setSubjectName(studentSession.getSession().getSubject().getName());
            }
        }

        return dto;
    }
}

