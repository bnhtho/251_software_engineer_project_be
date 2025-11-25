package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentSessionDTO;
import HCMUT.TutorSytem.dto.TutorDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.payload.request.TutorProfileCreateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.TutorProfileService;
import HCMUT.TutorSytem.service.TutorService;
import HCMUT.TutorSytem.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tutors")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    @Autowired
    private TutorProfileService tutorProfileService;

    /**
     * Get all tutors (with pagination)
     * Public endpoint - anyone can view tutor list
     * Mặc định: 10 items per page
     *
     * @param page Số trang (bắt đầu từ 0, mặc định = 0)
     */
    @GetMapping
    public ResponseEntity<BaseResponse> getAllTutors(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<TutorDTO> tutorsPage = tutorService.getAllTutors(pageable);
        Map<String, Object> paginatedData = PaginationUtil.createPaginationResponse(tutorsPage);

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutors retrieved successfully");
        response.setData(paginatedData);
        return ResponseEntity.ok(response);
    }

    /**
     * Get detailed tutor profile
     * Includes: teaching schedule, certifications (academic status), experience years, subjects
     * Tutors can only view their own detailed profile
     * userId lấy từ token authentication
     */
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse> getTutorDetail() {
        // Lấy tutorId từ authentication (token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getCurrentUserId(authentication);

        TutorDetailDTO tutorDetail = tutorService.getTutorDetail(userId);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor detail retrieved successfully");
        response.setData(tutorDetail);
        return ResponseEntity.ok(response);
    }

    /**
     * Update tutor profile (detailed)
     * Tutors can only update their own profile
     * userId lấy từ token authentication
     */
    @PutMapping("/profile")
    public ResponseEntity<BaseResponse> updateTutorProfile(
            @RequestBody TutorProfileUpdateRequest request) {
        // Lấy tutorId từ authentication (token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getCurrentUserId(authentication);

        TutorDetailDTO tutorDetail = tutorService.updateTutorProfile(userId, request);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor profile updated successfully");
        response.setData(tutorDetail);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách yêu cầu đăng ký đang chờ duyệt (with pagination)
     * Tutor chỉ xem được yêu cầu của các session mình tạo
     * Mặc định: 10 items per page
     *
     * @param page Số trang (bắt đầu từ 0, mặc định = 0)
     */
    @GetMapping("/pending-registrations")
    public ResponseEntity<BaseResponse> getPendingStudentSessions(@RequestParam(defaultValue = "0") int page) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer tutorId = getCurrentUserId(authentication);

        Pageable pageable = PageRequest.of(page, 10);
        Page<StudentSessionDTO> pendingSessionsPage = tutorService.getPendingStudentSessions(tutorId, pageable);
        Map<String, Object> paginatedData = PaginationUtil.createPaginationResponse(pendingSessionsPage);

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Pending student sessions retrieved successfully");
        response.setData(paginatedData);
        return ResponseEntity.ok(response);
    }

    /**
     * Duyệt yêu cầu đăng ký (một hoặc nhiều)
     * Dùng @RequestBody với List<Integer> để nhận danh sách ID của các yêu cầu đăng ký
     * Nếu duyệt 1 người thì gửi list có 1 phần tử
     *
     * @param studentSessionIds Danh sách ID của các yêu cầu đăng ký (StudentSession)
     */
    @PutMapping("/student-sessions/approve")
    public ResponseEntity<BaseResponse> approveStudentSessions(@RequestBody List<Integer> studentSessionIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer tutorId = getCurrentUserId(authentication);

        List<StudentSessionDTO> results = tutorService.approveStudentSessions(tutorId, studentSessionIds);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Đã duyệt " + results.size() + " yêu cầu đăng ký");
        response.setData(results);
        return ResponseEntity.ok(response);
    }

    /**
     * Từ chối yêu cầu đăng ký (một hoặc nhiều)
     * Dùng @RequestBody với List<Integer> để nhận danh sách ID của các yêu cầu đăng ký
     * Nếu từ chối 1 người thì gửi list có 1 phần tử
     *
     * @param studentSessionIds Danh sách ID của các yêu cầu đăng ký (StudentSession)
     */
    @PutMapping("/student-sessions/reject")
    public ResponseEntity<BaseResponse> rejectStudentSessions(@RequestBody List<Integer> studentSessionIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer tutorId = getCurrentUserId(authentication);

        List<StudentSessionDTO> results = tutorService.rejectStudentSessions(tutorId, studentSessionIds);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Đã từ chối " + results.size() + " yêu cầu đăng ký");
        response.setData(results);
        return ResponseEntity.ok(response);
    }


    /**
     * Lấy lịch giảng dạy trong tuần của tutor
     * Tutor chỉ có thể xem lịch của chính mình (tutorId lấy từ token)
     *
     * @param weekOffset Offset tuần (0 = tuần hiện tại, 1 = tuần sau, -1 = tuần trước)
     */
    @GetMapping("/schedule/{weekOffset}")
    public ResponseEntity<BaseResponse> getWeekSchedule(@PathVariable Integer weekOffset) {
        // Lấy tutorId từ authentication (token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer tutorId = getCurrentUserId(authentication);

        List<SessionDTO> schedule = tutorService.getWeekSchedule(tutorId, weekOffset);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Lịch giảng dạy trong tuần lấy thành công");
        response.setData(schedule);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BaseResponse> registerTutorProfile(
            @RequestBody TutorProfileCreateRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer currentUserId = getCurrentUserId(authentication);
        TutorDTO tutorProfile = tutorProfileService.registerTutorProfile(currentUserId, request);

        BaseResponse response = new BaseResponse();
        response.setStatusCode(201);
        response.setMessage("Tutor profile created successfully");
        response.setData(tutorProfile);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /**
     * Helper method để lấy user ID từ authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        return (Integer) authentication.getPrincipal();
    }
}
