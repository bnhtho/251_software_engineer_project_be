package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.repo.TutorProfileRepository;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.AdminService;
import HCMUT.TutorSytem.service.StudentService;
import HCMUT.TutorSytem.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImp implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TutorService tutorService;

    @Override
    @Transactional
    public StudentDTO updateStudentProfileByAdmin(Long userId, StudentProfileUpdateRequest request) {
        // Admin can update any student profile
        return studentService.updateStudentProfile(userId, request);
    }

    @Override
    @Transactional
    public void deleteStudentProfile(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundExceptions("Student not found with id: " + userId);
        }
        // Delete user - cascading will handle related data
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public TutorDetailDTO updateTutorProfileByAdmin(Long userId, TutorProfileUpdateRequest request) {
        // Admin can update any tutor profile
        return tutorService.updateTutorProfile(userId, request);
    }

    @Override
    @Transactional
    public void deleteTutorProfile(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundExceptions("Tutor not found with id: " + userId);
        }

        // Check if tutor profile exists
        tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for user id: " + userId));

        // Delete user - cascading will handle tutor profile and related data
        userRepository.deleteById(userId);
    }
}

