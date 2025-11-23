package HCMUT.TutorSytem.mapper;

import HCMUT.TutorSytem.dto.TutorDTO;
import HCMUT.TutorSytem.model.TutorProfile;
import HCMUT.TutorSytem.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TutorMapper {

    public static TutorDTO toDTO(TutorProfile tutorProfile) {
        if (tutorProfile == null) {
            return null;
        }

        TutorDTO dto = new TutorDTO();
        User user = tutorProfile.getUser();

        dto.setId(tutorProfile.getId());

        // From User (Datacore)
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

        // From TutorProfile
        dto.setTitle(user.getAcademicStatus() != null ? user.getAcademicStatus() : "Tutor");

        // Major information
        if (user.getMajor() != null) {
            dto.setMajorId(user.getMajor().getId());
            dto.setMajorName(user.getMajor().getName());

            // Department from major
            if (user.getMajor().getDepartment() != null) {
                dto.setDepartment(user.getMajor().getDepartment().getName());
            }
        }

        dto.setDescription(tutorProfile.getBio());

        // Subjects - convert to list of subject names for DTO
        if (tutorProfile.getSubjects() != null && !tutorProfile.getSubjects().isEmpty()) {
            dto.setSpecializations(
                tutorProfile.getSubjects().stream()
                    .map(subject -> subject.getName())
                    .collect(Collectors.toList())
            );
        } else {
            dto.setSpecializations(Collections.emptyList());
        }

        dto.setRating(tutorProfile.getRating() != null ? tutorProfile.getRating().doubleValue() : 0.0);
        dto.setReviewCount(0); // Can be calculated from feedback_student table
        dto.setStudentCount(tutorProfile.getTotalSessionsCompleted() != null ? tutorProfile.getTotalSessionsCompleted().intValue() : 0);
        dto.setExperienceYears(tutorProfile.getExperienceYears() != null ? tutorProfile.getExperienceYears().intValue() : 0);
        dto.setIsAvailable(tutorProfile.getIsAvailable());

        return dto;
    }

    public static List<TutorDTO> toDTOList(List<TutorProfile> tutorProfiles) {
        if (tutorProfiles == null) {
            return Collections.emptyList();
        }
        return tutorProfiles.stream()
                .map(TutorMapper::toDTO)
                .collect(Collectors.toList());
    }
}

