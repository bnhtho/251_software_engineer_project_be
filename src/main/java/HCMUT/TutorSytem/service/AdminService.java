package HCMUT.TutorSytem.service;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.dto.UserDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;

import java.util.List;

public interface AdminService {
    // Student management
    StudentDTO updateStudentProfileByAdmin(Integer userId, StudentProfileUpdateRequest request);
    void deleteStudentProfile(Integer userId);

    // Tutor management
    TutorDetailDTO updateTutorProfileByAdmin(Integer userId, TutorProfileUpdateRequest request);
    void deleteTutorProfile(Integer userId);

    // User management
    List<UserDTO> getAllUsers();
}

