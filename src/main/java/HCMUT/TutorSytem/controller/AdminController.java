package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.dto.UserDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.AdminService;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Admin: Update student profile
     */
    @PutMapping("/students/{userId}")
    public ResponseEntity<BaseResponse> updateStudentProfile(
            @PathVariable Integer userId,
            @RequestBody StudentProfileUpdateRequest request) {
        StudentDTO studentDTO = adminService.updateStudentProfileByAdmin(userId, request);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student profile updated successfully by admin");
        response.setData(studentDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin: Soft delete student profile (set status to INACTIVE)
     */
    @DeleteMapping("/students/{userId}")
    public ResponseEntity<BaseResponse> deleteStudentProfile(@PathVariable Integer userId) {
        adminService.deleteStudentProfile(userId);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student profile deactivated successfully by admin");
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin: Update tutor profile
     */
    @PutMapping("/tutors/{userId}")
    public ResponseEntity<BaseResponse> updateTutorProfile(
            @PathVariable Integer userId,
            @RequestBody TutorProfileUpdateRequest request) {
        TutorDetailDTO tutorDetail = adminService.updateTutorProfileByAdmin(userId, request);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor profile updated successfully by admin");
        response.setData(tutorDetail);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin: Soft delete tutor profile (set status to INACTIVE)
     */
    @DeleteMapping("/tutors/{userId}")
    public ResponseEntity<BaseResponse> deleteTutorProfile(@PathVariable Integer userId) {
        adminService.deleteTutorProfile(userId);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor profile deactivated successfully by admin");
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin: Get All Users (without pagination)
     */
    @GetMapping("/users")
    public ResponseEntity<BaseResponse> getAllUsers() {
        List<UserDTO> users = adminService.getAllUsers();
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Users retrieved successfully");
        response.setData(users);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin: Approve hoặc Reject Session
     *
     * URL: /admin/sessions/{sessionId}?setStatus=SCHEDULED (approve)
     * URL: /admin/sessions/{sessionId}?setStatus=CANCELLED (reject)
     *
     * @param sessionId ID của session cần duyệt/từ chối
     * @param setStatus Trạng thái mới: "SCHEDULED" (approve) hoặc "CANCELLED" (reject)
     * @return SessionDTO với status đã được cập nhật
     */
    @PutMapping("/sessions/{sessionId}")
    public ResponseEntity<BaseResponse> updateSessionStatus(
            @PathVariable Integer sessionId,
            @RequestParam String setStatus) {

        // Lấy adminId từ authentication (token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer adminId = getCurrentUserId(authentication);

        // Gọi service để update session status
        SessionDTO sessionDTO = adminService.updateSessionStatus(sessionId, adminId, setStatus);

        // Tạo response message phù hợp
        String message;
        if ("SCHEDULED".equalsIgnoreCase(setStatus)) {
            message = "Session approved successfully";
        } else if ("CANCELLED".equalsIgnoreCase(setStatus)) {
            message = "Session rejected";
        } else {
            message = "Session status updated successfully";
        }

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage(message);
        response.setData(sessionDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method để lấy user ID từ authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        return (Integer) authentication.getPrincipal();
    }
}

