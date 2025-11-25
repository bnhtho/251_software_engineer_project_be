package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.StudentSessionHistoryDTO;
import HCMUT.TutorSytem.dto.StudentSessionDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.StudentService;
import HCMUT.TutorSytem.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * Get student profile
     * Students can only view their own profile
     * userId lấy từ token authentication
     */
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse> getStudentProfile() {
        // Lấy studentId từ authentication (token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getCurrentUserId(authentication);

        StudentDTO studentDTO = studentService.getStudentProfile(userId);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student profile retrieved successfully");
        response.setData(studentDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Get student session history (with pagination)
     * Returns list of all sessions student has enrolled in
     * Students can only view their own history
     * userId lấy từ token authentication
     * Mặc định: 10 items per page
     *
     * @param page Số trang (bắt đầu từ 0, mặc định = 0)
     */
    @GetMapping("/history")
    public ResponseEntity<BaseResponse> getStudentSessionHistory(
            @RequestParam(defaultValue = "0") int page) {
        // Lấy studentId từ authentication (token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getCurrentUserId(authentication);

        Pageable pageable = PageRequest.of(page, 10);
        Page<StudentSessionHistoryDTO> historyPage = studentService.getStudentSessionHistory(userId, pageable);
        Map<String, Object> paginatedData = PaginationUtil.createPaginationResponse(historyPage);

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student session history retrieved successfully");
        response.setData(paginatedData);
        return ResponseEntity.ok(response);
    }

    /**
     * Update student profile
     * Students can only update their own profile
     * userId lấy từ token authentication
     */
    @PutMapping("/profile")
    public ResponseEntity<BaseResponse> updateStudentProfile(
            @RequestBody StudentProfileUpdateRequest request) {
        // Lấy studentId từ authentication (token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getCurrentUserId(authentication);

        StudentDTO studentDTO = studentService.updateStudentProfile(userId, request);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student profile updated successfully");
        response.setData(studentDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách session khả dụng để đăng ký (with pagination)
     * Public endpoint - tất cả student có thể xem
     * Mặc định: 10 items per page
     *
     * @param page Số trang (bắt đầu từ 0, mặc định = 0)
     */
    @GetMapping("/available-sessions")
    public ResponseEntity<BaseResponse> getAvailableSessions(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<SessionDTO> sessionsPage = studentService.getAvailableSessions(pageable);
        Map<String, Object> paginatedData = PaginationUtil.createPaginationResponse(sessionsPage);

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Available sessions retrieved successfully");
        response.setData(paginatedData);
        return ResponseEntity.ok(response);
    }

    /**
     * Đăng ký session
     * Student chỉ có thể đăng ký cho chính mình (userId lấy từ token)
     *
     * @param sessionId ID của session muốn đăng ký (dùng @RequestParam thay vì tạo DTO vì chỉ có 1 tham số)
     */
    @PostMapping("/register-session")
    public ResponseEntity<BaseResponse> registerSession(@RequestParam Integer sessionId) {
        // Lấy studentId từ authentication (token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer studentId = getCurrentUserId(authentication);

        StudentSessionDTO studentSession = studentService.registerSession(studentId, sessionId);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Yêu cầu đăng ký đã gửi, đang chờ tutor duyệt");
        response.setData(studentSession);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy lịch học trong tuần của student
     * Student chỉ có thể xem lịch của chính mình (studentId lấy từ token)
     *
     * @param weekOffset Offset tuần (0 = tuần hiện tại, 1 = tuần sau, -1 = tuần trước)
     */
    @GetMapping("/schedule/{weekOffset}")
    public ResponseEntity<BaseResponse> getWeekSchedule(@PathVariable Integer weekOffset) {
        // Lấy studentId từ authentication (token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer studentId = getCurrentUserId(authentication);

        List<StudentSessionDTO> schedule = studentService.getWeekSchedule(studentId, weekOffset);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Lịch học trong tuần lấy thành công");
        response.setData(schedule);
        return ResponseEntity.ok(response);
    }
    /**
     * Helper method để lấy user ID từ authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        return (Integer) authentication.getPrincipal();
    }
}