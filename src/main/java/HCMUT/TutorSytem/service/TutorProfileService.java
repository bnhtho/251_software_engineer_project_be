package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.TutorDTO;
import HCMUT.TutorSytem.dto.TutorProfileResponse;
import HCMUT.TutorSytem.model.TutorProfile;
import HCMUT.TutorSytem.payload.request.TutorProfileCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TutorProfileService {
    TutorDTO registerTutorProfile(Integer userId, TutorProfileCreateRequest request);
    TutorProfile approveTutorProfile(Integer id);
    TutorProfile rejectTutorProfile(Integer id);
    TutorProfile approveTutorProfileByUserId(Integer userId);
    TutorProfile rejectTutorProfileByUserId(Integer userId);
    Page<TutorProfileResponse> getPendingTutorProfiles(Pageable pageable);
}
