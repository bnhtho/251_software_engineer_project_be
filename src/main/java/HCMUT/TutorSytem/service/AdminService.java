package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;

public interface AdminService {
    // Student management
    StudentDTO updateStudentProfileByAdmin(Long userId, StudentProfileUpdateRequest request);
    void deleteStudentProfile(Long userId);

    // Tutor management
    TutorDetailDTO updateTutorProfileByAdmin(Long userId, TutorProfileUpdateRequest request);
    void deleteTutorProfile(Long userId);
}

