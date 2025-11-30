package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.TutorDTO;
import HCMUT.TutorSytem.dto.TutorProfileResponse;
import HCMUT.TutorSytem.exception.BadRequestException;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.exception.MethodNotAllowExceptions;
import HCMUT.TutorSytem.mapper.TutorMapper;
import HCMUT.TutorSytem.mapper.TutorProfileResponseMapper;
import HCMUT.TutorSytem.model.*;
import HCMUT.TutorSytem.payload.request.TutorProfileCreateRequest;
import HCMUT.TutorSytem.repo.MajorRepository;
import HCMUT.TutorSytem.repo.RegistrationStatusRepository;
import HCMUT.TutorSytem.repo.SubjectRepository;
import HCMUT.TutorSytem.repo.TutorProfileRepository;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.TutorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TutorProfileServiceImpl implements TutorProfileService {

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private RegistrationStatusRepository registrationStatusRepository;

    @Override
    @Transactional
    public TutorDTO registerTutorProfile(Integer userId, TutorProfileCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found with id: " + userId));

        tutorProfileRepository.findByUserId(userId).ifPresent(profile -> {
            throw new MethodNotAllowExceptions("Tutor profile already exists for this user");
        });

        if (request.getMajorId() != null) {
            Major major = majorRepository.findById(request.getMajorId())
                    .orElseThrow(() -> new DataNotFoundExceptions("Major not found with id: " + request.getMajorId()));
            user.setMajor(major);
        }

        if (request.getAcademicStatus() != null && !request.getAcademicStatus().trim().isEmpty()) {
            user.setAcademicStatus(request.getAcademicStatus().trim());
        }
        userRepository.save(user);

        List<Integer> subjectIds = request.getSubjects() != null ? request.getSubjects() : Collections.emptyList();
        List<Subject> subjects = resolveSubjects(subjectIds);

        TutorProfile tutorProfile = new TutorProfile();
        tutorProfile.setUser(user);
        tutorProfile.getSubjects().addAll(subjects);

        if (request.getExperienceYears() != null) {
            tutorProfile.setExperienceYears(request.getExperienceYears().shortValue());
        }

        tutorProfile.setBio(request.getDescription());
        tutorProfile.setRating(BigDecimal.ZERO);
        tutorProfile.setPriority(0);
        tutorProfile.setTotalSessionsCompleted(0);

        // Set registration status to PENDING by default
        RegistrationStatus pendingStatus = registrationStatusRepository.findById(RegistrationStatus.PENDING)
                .orElseThrow(() -> new DataNotFoundExceptions("Registration status PENDING not found"));
        tutorProfile.setRegistrationStatus(pendingStatus);

        TutorProfile savedProfile = tutorProfileRepository.save(tutorProfile);
        return TutorMapper.toDTO(savedProfile);
    }

    @Override
    @Transactional
    public TutorProfile approveTutorProfile(Integer id) {
        return approveTutorProfileByUserId(id);
    }

    @Override
    @Transactional
    public TutorProfile rejectTutorProfile(Integer id) {
        return rejectTutorProfileByUserId(id);
    }

    @Override
    @Transactional
    public TutorProfile approveTutorProfileByUserId(Integer Id) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(Id)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for id: " + Id));
        return approveTutorProfileInternal(tutorProfile);
    }

    @Override
    @Transactional
    public TutorProfile rejectTutorProfileByUserId(Integer Id) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(Id)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for id: " + Id));
        return rejectTutorProfileInternal(tutorProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TutorProfileResponse> getPendingTutorProfiles(Pageable pageable) {
        Page<TutorProfile> page = tutorProfileRepository.findByRegistrationStatusId(RegistrationStatus.PENDING, pageable);
        return page.map(TutorProfileResponseMapper::toResponse);
    }

    private TutorProfile approveTutorProfileInternal(TutorProfile tutorProfile) {
        // Ensure current status is PENDING
        ensurePendingStatus(tutorProfile);

        // Set registration status to CONFIRMED (approved)
        RegistrationStatus confirmedStatus = registrationStatusRepository.findById(RegistrationStatus.CONFIRMED)
                .orElseThrow(() -> new DataNotFoundExceptions("Registration status CONFIRMED not found"));
        tutorProfile.setRegistrationStatus(confirmedStatus);

        User user = tutorProfile.getUser();
        if (user != null) {
            String role = user.getRole().getName();
            if (role == null || "STUDENT".equalsIgnoreCase(role)) {
                user.setRole(new Role(2)); // Set role to TUTOR
                userRepository.save(user);
            }
        }

        return tutorProfileRepository.save(tutorProfile);
    }

    private TutorProfile rejectTutorProfileInternal(TutorProfile tutorProfile) {
        // Ensure current status is PENDING
        ensurePendingStatus(tutorProfile);

        // Set registration status to REJECTED
        RegistrationStatus rejectedStatus = registrationStatusRepository.findById(RegistrationStatus.REJECTED)
                .orElseThrow(() -> new DataNotFoundExceptions("Registration status REJECTED not found"));
        tutorProfile.setRegistrationStatus(rejectedStatus);

        return tutorProfileRepository.save(tutorProfile);
    }

    /**
     * Đảm bảo tutorProfile hiện đang ở trạng thái PENDING.
     */
    private void ensurePendingStatus(TutorProfile tutorProfile) {
        RegistrationStatus currentStatus = tutorProfile.getRegistrationStatus();
        if (currentStatus == null ||
                !Objects.equals(currentStatus.getId(), RegistrationStatus.PENDING)) {
            throw new BadRequestException("Tutor profile is not in PENDING status");
        }
    }

    /**
     * Resolve danh sách môn học từ danh sách id.
     * - Không cho phép id null
     * - Ném exception nếu có id nào không tồn tại.
     */
    private List<Subject> resolveSubjects(List<Integer> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Không cho phép null ngay từ đầu
        if (subjectIds.stream().anyMatch(Objects::isNull)) {
            throw new MethodNotAllowExceptions("Subject id must not be null");
        }

        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        Set<Integer> foundIds = subjects.stream()
                .map(Subject::getId)
                .collect(Collectors.toSet());

        List<Integer> missingIds = subjectIds.stream()
                .filter(id -> !foundIds.contains(id))
                .distinct()
                .toList();

        if (!missingIds.isEmpty()) {
            String missingIdsStr = missingIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new DataNotFoundExceptions("Subject not found with id(s): " + missingIdsStr);
        }

        return subjects;
    }
}
