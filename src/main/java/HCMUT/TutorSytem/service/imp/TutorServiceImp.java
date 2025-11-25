package HCMUT.TutorSytem.service.imp;

import HCMUT.TutorSytem.dto.*;
import HCMUT.TutorSytem.exception.DataNotFoundExceptions;
import HCMUT.TutorSytem.mapper.StudentSessionMapper;
import HCMUT.TutorSytem.mapper.TutorDetailMapper;
import HCMUT.TutorSytem.mapper.TutorMapper;
import HCMUT.TutorSytem.model.*;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import HCMUT.TutorSytem.repo.MajorRepository;
import HCMUT.TutorSytem.repo.SubjectRepository;
import HCMUT.TutorSytem.repo.TutorProfileRepository;
import HCMUT.TutorSytem.repo.UserRepository;
import HCMUT.TutorSytem.service.TutorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorServiceImp implements TutorService {

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private StudentSessionRepository studentSessionRepository;

    @Autowired
    private StudentSessionStatusRepository studentSessionStatusRepository;


    @Override
    public Page<TutorDTO> getAllTutors(Pageable pageable) {
        Page<TutorProfile> tutorProfilesPage = tutorProfileRepository.findAll(pageable);
        return tutorProfilesPage.map(TutorMapper::toDTO);
    }



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
        tutorProfile.setPriority(0);
        tutorProfile.setTotalSessionsCompleted(0); // Auto set to 0
        tutorProfile.setIsAvailable(true); // Auto set to true
        tutorProfile.setStatus(TutorStatus.PENDING);

        tutorProfile = tutorProfileRepository.save(tutorProfile);
        return TutorMapper.toDTO(tutorProfile);
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
        return TutorMapper.toDTO(tutorProfile);
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

        // Lấy schedules của tutor
        List<Schedule> schedules = scheduleRepository.findByUserId(user.getId());

        return TutorDetailMapper.toDTO(user, tutorProfile, schedules);
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

        // Lấy schedules của tutor
        List<Schedule> schedules = scheduleRepository.findByUserId(user.getId());

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
        dto.setStatus(tutorProfile.getStatus());

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
                        scheduleDTO.setDayOfWeek(schedule.getDayOfWeek());
                        scheduleDTO.setStartTime(schedule.getStartTime());
                        scheduleDTO.setEndTime(schedule.getEndTime());
                        scheduleDTO.setCreatedDate(schedule.getCreatedDate());
                        scheduleDTO.setUpdateDate(schedule.getUpdateDate());
                        return scheduleDTO;
                    })
                    .collect(Collectors.toList());
            dto.setSchedules(scheduleDTOs);
        }

        return dto;
    }

    @Override
    public Page<StudentSessionDTO> getPendingStudentSessions(Integer tutorId, Pageable pageable) {
        // Lấy các yêu cầu đăng ký đang PENDING cho các session của tutor với pagination
        Page<StudentSession> pendingSessionsPage = studentSessionRepository
                .findPendingSessionsByTutorId(tutorId, (byte) StudentSessionStatus.PENDING, pageable);

        return pendingSessionsPage.map(StudentSessionMapper::toDTO);
    }

    @Override
    @Transactional
    public List<StudentSessionDTO> approveStudentSessions(Integer tutorId, List<Integer> studentSessionIds) {
        List<StudentSessionDTO> results = new ArrayList<>();

        for (Integer studentSessionId : studentSessionIds) {
            try {
                StudentSessionDTO approved = approve(tutorId, studentSessionId);
                results.add(approved);
            } catch (IllegalStateException e) {
                // Nếu session đã đầy, các yêu cầu còn lại sẽ bị reject tự động
                StudentSession studentSession = studentSessionRepository.findById(studentSessionId)
                        .orElseThrow(() -> new DataNotFoundExceptions("Student session not found with id: " + studentSessionId));

                // Reject yêu cầu này
                StudentSessionStatus rejectedStatus = studentSessionStatusRepository.findById(StudentSessionStatus.REJECTED)
                        .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));
                studentSession.setStudentSessionStatus(rejectedStatus);
                studentSession = studentSessionRepository.save(studentSession);

                // Xóa schedule đã thêm trước đó bằng userId và sessionId
                Session session = studentSession.getSession();
                scheduleRepository.deleteByUserIdAndSessionId(
                    studentSession.getStudent().getId(),
                    session.getId()
                );

                results.add(StudentSessionMapper.toDTO(studentSession));
            }
        }

        return results;
    }

    @Override
    @Transactional
    public List<StudentSessionDTO> rejectStudentSessions(Integer tutorId, List<Integer> studentSessionIds) {
        List<StudentSessionDTO> results = new ArrayList<>();

        // Kiểm tra tutor tồn tại
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + tutorId));

        if (!"TUTOR".equalsIgnoreCase(tutor.getRole())) {
            throw new IllegalArgumentException("User does not have tutor privileges");
        }

        for (Integer studentSessionId : studentSessionIds) {
            try {
                // Tìm student session
                StudentSession studentSession = studentSessionRepository.findById(studentSessionId)
                        .orElseThrow(() -> new DataNotFoundExceptions("Student session not found with id: " + studentSessionId));

                // Kiểm tra quyền: session phải thuộc về tutor này
                if (!studentSession.getSession().getTutor().getId().equals(tutorId)) {
                    throw new IllegalStateException("Bạn không có quyền từ chối yêu cầu này (session không thuộc về bạn)");
                }

                // Kiểm tra trạng thái phải là PENDING
                if (studentSession.getStudentSessionStatus().getId() != StudentSessionStatus.PENDING) {
                    throw new IllegalStateException("Yêu cầu đăng ký không ở trạng thái chờ duyệt. Trạng thái hiện tại: "
                            + studentSession.getStudentSessionStatus().getName());
                }

                // Từ chối: chỉ update status, không tăng currentQuantity
                StudentSessionStatus rejectedStatus = studentSessionStatusRepository.findById(StudentSessionStatus.REJECTED)
                        .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));
                studentSession.setStudentSessionStatus(rejectedStatus);

                // Save (updatedDate sẽ tự động update nhờ @UpdateTimestamp)
                studentSession = studentSessionRepository.save(studentSession);

                // Xóa schedule đã thêm trước đó khi đăng ký bằng userId và sessionId
                try {
                    Session session = studentSession.getSession();
                    scheduleRepository.deleteByUserIdAndSessionId(
                        studentSession.getStudent().getId(),
                        session.getId()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                results.add(StudentSessionMapper.toDTO(studentSession));
            } catch (DataNotFoundExceptions | IllegalStateException e) {
                throw e;
            }
        }

        return results;
    }

    @Override
    public List<SessionDTO> getWeekSchedule(Integer tutorId, Integer weekOffset) {
        // Kiểm tra tutor có tồn tại không
        if (!userRepository.existsById(tutorId)) {
            throw new DataNotFoundExceptions("Tutor not found with id: " + tutorId);
        }

        // Tính toán thời điểm bắt đầu và kết thúc của tuần
        Instant now = Instant.now();
        java.time.ZoneId zoneId = java.time.ZoneId.systemDefault();

        // Chuyển sang LocalDate để tính toán
        java.time.LocalDate currentDate = now.atZone(zoneId).toLocalDate();

        // Tìm ngày đầu tuần (Monday)
        java.time.LocalDate startOfCurrentWeek = currentDate.with(java.time.DayOfWeek.MONDAY);

        // Áp dụng offset
        java.time.LocalDate startOfTargetWeek = startOfCurrentWeek.plusWeeks(weekOffset);
        java.time.LocalDate endOfTargetWeek = startOfTargetWeek.plusDays(7);

        // Chuyển về Instant (lấy từ 00:00:00)
        Instant startOfWeek = startOfTargetWeek.atStartOfDay(zoneId).toInstant();
        Instant endOfWeek = endOfTargetWeek.atStartOfDay(zoneId).toInstant();

        // Query các Session đang SCHEDULED trong tuần của tutor
        List<Session> sessions = sessionRepository.findTutorScheduledSessionsInWeek(
                tutorId,
                SessionStatus.SCHEDULED,
                startOfWeek,
                endOfWeek
        );

        return sessions.stream()
                .map(session -> {
                    SessionDTO dto = new SessionDTO();
                    dto.setId(session.getId());
                    dto.setStartTime(session.getStartTime());
                    dto.setEndTime(session.getEndTime());
                    dto.setFormat(session.getFormat());
                    dto.setLocation(session.getLocation());
                    dto.setMaxQuantity(session.getMaxQuantity());
                    dto.setCurrentQuantity(session.getCurrentQuantity());
                    dto.setUpdatedDate(session.getUpdatedDate());

                    if (session.getTutor() != null) {
                        User tutor = session.getTutor();
                        dto.setTutorName((tutor.getFirstName() != null ? tutor.getFirstName() : "") + " " +
                                (tutor.getLastName() != null ? tutor.getLastName() : ""));
                    }

                    if (session.getSubject() != null) {
                        dto.setSubjectName(session.getSubject().getName());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    private StudentSessionDTO approve(Integer tutorId, Integer studentSessionId) {
        // Kiểm tra quyền tutor
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new DataNotFoundExceptions("Tutor not found with id: " + tutorId));

        if (!"TUTOR".equalsIgnoreCase(tutor.getRole())) {
            throw new IllegalArgumentException("User does not have tutor privileges");
        }

        // Tìm student session
        StudentSession studentSession = studentSessionRepository.findById(studentSessionId)
                .orElseThrow(() -> new DataNotFoundExceptions("Student session not found with id: " + studentSessionId));

        // Kiểm tra quyền: session phải thuộc về tutor này
        if (!studentSession.getSession().getTutor().getId().equals(tutorId)) {
            throw new IllegalStateException("Bạn không có quyền duyệt yêu cầu này (session không thuộc về bạn)");
        }

        // Kiểm tra trạng thái phải là PENDING (tương tự AdminService kiểm tra PENDING)
        if (studentSession.getStudentSessionStatus().getId() != StudentSessionStatus.PENDING) {
            throw new IllegalStateException("Yêu cầu đăng ký không ở trạng thái chờ duyệt. Trạng thái hiện tại: "
                    + studentSession.getStudentSessionStatus().getName());
        }

        // Lock session để tránh race condition khi update currentQuantity
        Session session = sessionRepository.findByIdWithLock(studentSession.getSession().getId())
                .orElseThrow(() -> new DataNotFoundExceptions("Session not found"));

        // Kiểm tra lần 2: session còn chỗ không
        if (session.getCurrentQuantity() >= session.getMaxQuantity()) {
            // Session đã đầy, từ chối yêu cầu này
            StudentSessionStatus rejectedStatus = studentSessionStatusRepository.findById(StudentSessionStatus.REJECTED)
                    .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));
            studentSession.setStudentSessionStatus(rejectedStatus);
            studentSessionRepository.save(studentSession);

            throw new IllegalStateException("Buổi học đã đủ số lượng, không thể duyệt thêm");
        }

        // Còn chỗ -> approve
        // 1. Tăng currentQuantity
        session.setCurrentQuantity(session.getCurrentQuantity() + 1);
        sessionRepository.save(session);

        // 2. Update status của studentSession thành CONFIRMED
        StudentSessionStatus confirmedStatus = studentSessionStatusRepository.findById(StudentSessionStatus.CONFIRMED)
                .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus CONFIRMED not found"));
        studentSession.setStudentSessionStatus(confirmedStatus);

        // 3. Set confirmedDate
        studentSession.setConfirmedDate(Instant.now());

        // 4. Save (updatedDate sẽ tự động update nhờ @UpdateTimestamp)
        studentSession = studentSessionRepository.save(studentSession);

        // 5. Nếu đây là người cuối cùng được approve (session đầy), auto reject các pending còn lại
        if (session.getCurrentQuantity().equals(session.getMaxQuantity())) {
            List<StudentSession> remainingPending = studentSessionRepository
                    .findBySessionId(session.getId())
                    .stream()
                    .filter(ss -> ss.getStudentSessionStatus().getId() == StudentSessionStatus.PENDING)
                    .toList();

            StudentSessionStatus rejectedStatus = studentSessionStatusRepository.findById(StudentSessionStatus.REJECTED)
                    .orElseThrow(() -> new DataNotFoundExceptions("StudentSessionStatus REJECTED not found"));

            for (StudentSession pending : remainingPending) {
                pending.setStudentSessionStatus(rejectedStatus);
                studentSessionRepository.save(pending);

                // Xóa schedule đã thêm trước đó bằng userId và sessionId
                scheduleRepository.deleteByUserIdAndSessionId(
                    pending.getStudent().getId(),
                    session.getId()
                );
            }
        }

        return StudentSessionMapper.toDTO(studentSession);
    }
}
