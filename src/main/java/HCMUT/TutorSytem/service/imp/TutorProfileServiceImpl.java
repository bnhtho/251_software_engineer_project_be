package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.TutorDTO;
import HCMUT.TutorSytem.dto.TutorProfileResponse;
import HCMUT.TutorSytem.exception.BadRequestException;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.exception.MethodNotAllowExceptions;
import HCMUT.TutorSytem.mapper.TutorMapper;
import HCMUT.TutorSytem.mapper.TutorProfileResponseMapper;
import HCMUT.TutorSytem.model.Major;
import HCMUT.TutorSytem.model.Subject;
import HCMUT.TutorSytem.model.TutorProfile;
import HCMUT.TutorSytem.model.TutorStatus;
import HCMUT.TutorSytem.model.User;
import HCMUT.TutorSytem.payload.request.TutorProfileCreateRequest;
import HCMUT.TutorSytem.repo.MajorRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            user.setAcademicStatus(request.getTitle().trim());
        }
        userRepository.save(user);

        List<Integer> subjectIds = request.getSubjects() != null ? request.getSubjects() : Collections.emptyList();
        List<Subject> subjects = resolveSubjects(subjectIds);

        TutorProfile tutorProfile = new TutorProfile();
        tutorProfile.setUser(user);
        tutorProfile.setSubjects(new ArrayList<>(subjects));

        if (request.getExperienceYears() != null) {
            tutorProfile.setExperienceYears(request.getExperienceYears().shortValue());
        }

        tutorProfile.setBio(request.getDescription());
        tutorProfile.setRating(BigDecimal.ZERO);
        tutorProfile.setPriority(0);
        tutorProfile.setTotalSessionsCompleted(0);
        tutorProfile.setIsAvailable(true);
        tutorProfile.setStatus(TutorStatus.PENDING);

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
    public TutorProfile approveTutorProfileByUserId(Integer userId) {
        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for user id: " + userId));
        return approveTutorProfileInternal(tutorProfile);
    }

    @Override
    @Transactional
    public TutorProfile rejectTutorProfileByUserId(Integer userId) {
        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor profile not found for user id: " + userId));
        return rejectTutorProfileInternal(tutorProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TutorProfileResponse> getPendingTutorProfiles(Pageable pageable) {
        Page<TutorProfile> page = tutorProfileRepository.findByStatus(TutorStatus.PENDING, pageable);
        return page.map(TutorProfileResponseMapper::toResponse);
    }

    private TutorProfile approveTutorProfileInternal(TutorProfile tutorProfile) {
        if (tutorProfile.getStatus() != TutorStatus.PENDING) {
            throw new BadRequestException("Tutor profile is not in PENDING status");
        }

        tutorProfile.setStatus(TutorStatus.APPROVED);

        User user = tutorProfile.getUser();
        if (user != null) {
            String role = user.getRole();
            if (role == null || "STUDENT".equalsIgnoreCase(role)) {
                user.setRole("TUTOR");
                userRepository.save(user);
            }
        }

        return tutorProfileRepository.save(tutorProfile);
    }

    private TutorProfile rejectTutorProfileInternal(TutorProfile tutorProfile) {
        if (tutorProfile.getStatus() != TutorStatus.PENDING) {
            throw new BadRequestException("Tutor profile is not in PENDING status");
        }

        tutorProfile.setStatus(TutorStatus.REJECTED);
        return tutorProfileRepository.save(tutorProfile);
    }

    private List<Subject> resolveSubjects(List<Integer> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return Collections.emptyList();
        }

        boolean hasNullSubject = subjectIds.stream().anyMatch(Objects::isNull);
        if (hasNullSubject) {
            throw new MethodNotAllowExceptions("Subject id must not be null");
        }

        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        Set<Integer> foundIds = subjects.stream()
                .map(Subject::getId)
                .collect(Collectors.toSet());

        List<Integer> missingIds = subjectIds.stream()
                .filter(Objects::nonNull)
                .filter(id -> !foundIds.contains(id))
                .distinct()
                .collect(Collectors.toList());

        if (!missingIds.isEmpty()) {
            throw new DataNotFoundExceptions("Subject not found with id: " + missingIds.get(0));
        }

        return subjects;
    }
}
