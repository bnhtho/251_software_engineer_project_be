package HCMUT.TutorSytem.mapper;

import HCMUT.TutorSytem.dto.SubjectDTO;
import HCMUT.TutorSytem.dto.TutorProfileResponse;
import HCMUT.TutorSytem.dto.UserResponse;
import HCMUT.TutorSytem.model.Subject;
import HCMUT.TutorSytem.model.TutorProfile;
import HCMUT.TutorSytem.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class TutorProfileResponseMapper {

    private TutorProfileResponseMapper() {
    }

    public static TutorProfileResponse toResponse(TutorProfile tutorProfile) {
        if (tutorProfile == null) {
            return null;
        }

        TutorProfileResponse response = new TutorProfileResponse();
        response.setId(tutorProfile.getId());
        response.setBio(tutorProfile.getBio());
        response.setExperienceYears(tutorProfile.getExperienceYears() != null ? tutorProfile.getExperienceYears().intValue() : null);
        response.setRating(tutorProfile.getRating());
        response.setTotalSessionsCompleted(tutorProfile.getTotalSessionsCompleted());
        response.setSubjects(mapSubjects(tutorProfile.getSubjects()));
        response.setUser(mapUser(tutorProfile.getUser()));
        return response;
    }

    private static List<SubjectDTO> mapSubjects(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return Collections.emptyList();
        }
        return subjects.stream()
                .map(subject -> {
                    SubjectDTO subjectDTO = new SubjectDTO();
                    subjectDTO.setId(subject.getId());
                    subjectDTO.setName(subject.getName());
                    return subjectDTO;
                })
                .collect(Collectors.toList());
    }

    private static UserResponse mapUser(User user) {
        if (user == null) {
            return null;
        }
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setRole(user.getRole().getName());
        userResponse.setHcmutId(user.getHcmutId());
        userResponse.setAcademicStatus(user.getAcademicStatus());
        return userResponse;
    }
}

