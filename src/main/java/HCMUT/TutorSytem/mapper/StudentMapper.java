package HCMUT.TutorSytem.mapper;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StudentMapper {

    /**
     * Chuyển đổi User entity sang StudentDTO
     */
    public static StudentDTO toStudentDTO(User user) {
        if (user == null) {
            return null;
        }

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

        // Thông tin Major
        if (user.getMajor() != null) {
            dto.setMajorId(user.getMajor().getId());
            dto.setMajorName(user.getMajor().getName());

            // Thông tin Department từ Major
            if (user.getMajor().getDepartment() != null) {
                dto.setDepartment(user.getMajor().getDepartment().getName());
            }
        }

        return dto;
    }

    /**
     * Chuyển đổi danh sách User sang danh sách StudentDTO
     */
    public static List<StudentDTO> toStudentDTOList(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(StudentMapper::toStudentDTO)
                .collect(Collectors.toList());
    }
}
