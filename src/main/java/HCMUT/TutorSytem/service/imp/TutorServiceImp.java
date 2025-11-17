package HCMUT.TutorSytem.service.imp;
import HCMUT.TutorSytem.dto.*;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.TutorMapper;
import HCMUT.TutorSytem.model.*;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorRequest;
import HCMUT.TutorSytem.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import HCMUT.TutorSytem.repo.MajorRepository;
import HCMUT.TutorSytem.repo.SubjectRepository;
import HCMUT.TutorSytem.repo.TutorProfileRepository;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.TutorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorServiceImp implements TutorService {

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TutorMapper tutorMapper;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private TutorScheduleRepository tutorScheduleRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Override
    public List<TutorDTO> getAllTutors() {
        List<TutorProfile> tutorProfiles = tutorProfileRepository.findAll();
        return tutorProfiles.stream()
                .map(tutorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TutorDTO createTutor(TutorRequest request) {
        // Assuming the current authenticated user is creating the tutor profile
        // TODO: Get current user from authentication context
        // For now, this will throw an error - need to get userId from JWT token
        // User user = getCurrentAuthenticatedUser();

        // Temporary: If you need to specify userId, add it to TutorRequest
        // For now, create a minimal user (this should be replaced with actual auth user)
        User user = new User();
        user.setRole("tutor");
        // Name comes from User datacore, not from request
        // user.setFirstName, user.setLastName should be already set

        // Set major if provided
        if (request.getMajorId() != null) {
            Major major = majorRepository.findById(request.getMajorId())
                    .orElseThrow(() -> new DataNotFoundExceptions("Major not found with id: " + request.getMajorId()));
            user.setMajor(major);
        }

        user = userRepository.save(user);

        // Create tutor profile
        TutorProfile tutorProfile = new TutorProfile();
        tutorProfile.setUser(user);

        // Handle subjects - link by subject IDs (ManyToMany)
        if (request.getSubjects() != null && !request.getSubjects().isEmpty()) {
            for (Integer subjectId : request.getSubjects()) {
                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new DataNotFoundExceptions("Subject not found with id: " + subjectId));
                tutorProfile.getSubjects().add(subject);
            }
        }

        tutorProfile.setExperienceYears(request.getExperienceYears() != null ? request.getExperienceYears().shortValue() : null);
        tutorProfile.setBio(request.getDescription());
        tutorProfile.setRating(BigDecimal.ZERO); // Auto set to 0, calculated from reviews
        tutorProfile.setTotalSessionsCompleted(0); // Auto set to 0
        tutorProfile.setIsAvailable(true); // Auto set to true

        tutorProfile = tutorProfileRepository.save(tutorProfile);
        return tutorMapper.toDTO(tutorProfile);
    }

    @Override
    public TutorDTO updateTutor(Integer id, TutorRequest request) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + id));

        // Cannot update User fields (from Datacore) - they are managed separately
        // Only update TutorProfile specific fields

        // Update major if provided (this might be allowed or not, depending on business logic)
        User user = tutorProfile.getUser();
        if (request.getMajorId() != null) {
            Major major = majorRepository.findById(request.getMajorId())
                    .orElseThrow(() -> new DataNotFoundExceptions("Major not found with id: " + request.getMajorId()));
            user.setMajor(major);
            userRepository.save(user);
        }

        // Handle subjects - only update if provided and not empty
        if (request.getSubjects() != null && !request.getSubjects().isEmpty()) {
            // Clear existing subjects
            tutorProfile.getSubjects().clear();

            // Add subjects by IDs
            for (Integer subjectId : request.getSubjects()) {
                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new DataNotFoundExceptions("Subject not found with id: " + subjectId));
                tutorProfile.getSubjects().add(subject);
            }
        }

        // Update TutorProfile fields - only if not null and not empty
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            user.setAcademicStatus(request.getTitle().trim());
            userRepository.save(user);
        }

        if (request.getExperienceYears() != null) {
            tutorProfile.setExperienceYears(request.getExperienceYears().shortValue());
        }

        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            tutorProfile.setBio(request.getDescription().trim());
        }

        // Rating is calculated from reviews, cannot be manually updated
        // isAvailable can be toggled by user (could add separate field in request if needed)
        // Do not update: name, phone, dob, hcmutId, etc. (from Datacore)

        tutorProfile = tutorProfileRepository.save(tutorProfile);
        return tutorMapper.toDTO(tutorProfile);
    }

    @Override
    @Transactional
    public void deleteTutor(Integer id) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + id));

        User user = tutorProfile.getUser();
        if (user == null) {
            throw new DataNotFoundExceptions("User not found for tutor profile id: " + id);
        }

        // Soft delete: set status to INACTIVE
        Status inactiveStatus = statusRepository.findByName("INACTIVE")
                .orElseThrow(() -> new DataNotFoundExceptions("Status INACTIVE not found"));

        user.setStatus(inactiveStatus);
        userRepository.save(user);
    }

    @Override
    public Integer getUserIdFromTutorProfile(Integer tutorProfileId) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(tutorProfileId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found with id: " + tutorProfileId));
        return tutorProfile.getUser() != null ? tutorProfile.getUser().getId() : null;
    }

    @Override
    public TutorDetailDTO getTutorDetail(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found with id: " + userId));

        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for user id: " + userId));

        return mapToTutorDetailDTO(user, tutorProfile);
    }

    @Override
    @Transactional
    public TutorDetailDTO updateTutorProfile(Integer userId, TutorProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found with id: " + userId));

        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for user id: " + userId));

        // ...existing code...

        // Update subjects if provided
        if (request.getSubjectIds() != null && !request.getSubjectIds().isEmpty()) {
            tutorProfile.getSubjects().clear();
            for (Integer subjectId : request.getSubjectIds()) {
                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new DataNotFoundExceptions("Subject not found with id: " + subjectId));
                tutorProfile.getSubjects().add(subject);
            }
        }

        user = userRepository.save(user);
        tutorProfile = tutorProfileRepository.save(tutorProfile);

        return mapToTutorDetailDTO(user, tutorProfile);
    }

    private TutorDetailDTO mapToTutorDetailDTO(User user, TutorProfile tutorProfile) {
        TutorDetailDTO dto = new TutorDetailDTO();

        // User fields
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

        // TutorProfile fields
        dto.setTutorProfileId(tutorProfile.getId());
        dto.setBio(tutorProfile.getBio());
        dto.setRating(tutorProfile.getRating());
        dto.setExperienceYears(tutorProfile.getExperienceYears() != null ? tutorProfile.getExperienceYears().intValue() : null);
        dto.setTotalSessionsCompleted(tutorProfile.getTotalSessionsCompleted());
        dto.setIsAvailable(tutorProfile.getIsAvailable());

        // Subjects
        if (tutorProfile.getSubjects() != null) {
            List<SubjectDTO> subjectDTOs = tutorProfile.getSubjects().stream()
                    .map(subject -> {
                        SubjectDTO subjectDTO = new SubjectDTO();
                        subjectDTO.setId(subject.getId());
                        subjectDTO.setName(subject.getName());
                        return subjectDTO;
                    })
                    .collect(Collectors.toList());
            dto.setSubjects(subjectDTOs);
        }

        // Schedules
        List<TutorSchedule> schedules = tutorScheduleRepository.findByTutorId(user.getId());
        if (schedules != null && !schedules.isEmpty()) {
            List<TutorScheduleDTO> scheduleDTOs = schedules.stream()
                    .map(schedule -> {
                        TutorScheduleDTO scheduleDTO = new TutorScheduleDTO();
                        scheduleDTO.setId(schedule.getId());
                        scheduleDTO.setDayOfWeek(schedule.getDayOfWeek() != null ? schedule.getDayOfWeek().intValue() : null);
                        scheduleDTO.setStartTime(schedule.getStartTime());
                        scheduleDTO.setEndTime(schedule.getEndTime());
                        scheduleDTO.setStatus(schedule.getStatus());
                        scheduleDTO.setCreatedDate(schedule.getCreatedDate());
                        scheduleDTO.setUpdateDate(schedule.getUpdateDate());
                        return scheduleDTO;
                    })
                    .collect(Collectors.toList());
            dto.setSchedules(scheduleDTOs);
        }

        return dto;
    }
}

