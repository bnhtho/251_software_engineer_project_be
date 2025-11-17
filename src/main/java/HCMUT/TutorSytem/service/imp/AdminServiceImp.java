package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.dto.UserDTO;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.UserMapper;
import HCMUT.TutorSytem.model.Status;
import HCMUT.TutorSytem.model.User;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.repo.StatusRepository;
import HCMUT.TutorSytem.repo.TutorProfileRepository;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.AdminService;
import HCMUT.TutorSytem.service.StudentService;
import HCMUT.TutorSytem.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StatusRepository statusRepository;

    @Override
    @Transactional
    public StudentDTO updateStudentProfileByAdmin(Integer userId, StudentProfileUpdateRequest request) {
        // Admin can update any student profile
        return studentService.updateStudentProfile(userId, request);
    }

    @Override
    @Transactional
    public void deleteStudentProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student not found with id: " + userId));

        // Soft delete: set status to INACTIVE
        Status inactiveStatus = statusRepository.findByName("INACTIVE")
                .orElseThrow(() -> new DataNotFoundExceptions("Status INACTIVE not found"));

        user.setStatus(inactiveStatus);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public TutorDetailDTO updateTutorProfileByAdmin(Integer userId, TutorProfileUpdateRequest request) {
        // Admin can update any tutor profile
        return tutorService.updateTutorProfile(userId, request);
    }

    @Override
    @Transactional
    public void deleteTutorProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + userId));

        // Check if tutor profile exists
        tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for user id: " + userId));

        // Soft delete: set status to INACTIVE
        Status inactiveStatus = statusRepository.findByName("INACTIVE")
                .orElseThrow(() -> new DataNotFoundExceptions("Status INACTIVE not found"));

        user.setStatus(inactiveStatus);
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}

