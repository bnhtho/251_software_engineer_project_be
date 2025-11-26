package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.*;
import HCMUT.TutorSytem.mapper.TutorProfileResponseMapper;
import HCMUT.TutorSytem.model.TutorProfile;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.AdminService;
import HCMUT.TutorSytem.service.TutorProfileService;
import HCMUT.TutorSytem.util.PaginationUtil;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private TutorProfileService tutorProfileService;

    @Autowired
    private AdminService adminService;


    /**
     * Admin: Soft delete user profile (set status to INACTIVE)
     * Tự động xác định user là student hay tutor dựa trên role trong database
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<BaseResponse> deleteUserProfile(@PathVariable Integer userId) {
        // Lấy thông tin user để xác định role
        adminService.deleteUserProfile(userId);

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("User profile deactivated successfully by admin");
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin: Get All Users (with pagination)
     * Mặc định: 10 items per page
     *
     * @param page Số trang (bắt đầu từ 0, mặc định = 0)
     */
    @GetMapping("/users")
    public ResponseEntity<BaseResponse> getAllUsers(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<UserDTO> usersPage = adminService.getAllUsers(pageable);
        Map<String, Object> paginatedData = PaginationUtil.createPaginationResponse(usersPage);

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Users retrieved successfully");
        response.setData(paginatedData);
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

    @GetMapping("/tutor/pending")

    public ResponseEntity<BaseResponse> getPendingTutors(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<TutorProfileResponse> pendingProfiles = tutorProfileService.getPendingTutorProfiles(pageable);

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Fetched pending tutor profiles successfully");
        response.setData(pendingProfiles);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/approve")

    public ResponseEntity<BaseResponse> approveTutorByUserId(@PathVariable Integer userId) {
        TutorProfile updated = tutorProfileService.approveTutorProfile(userId);
        TutorProfileResponse data = TutorProfileResponseMapper.toResponse(updated);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor profile approved successfully");
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/reject")

    public ResponseEntity<BaseResponse> rejectTutorByUserId(@PathVariable Integer userId) {
        TutorProfile updated = tutorProfileService.rejectTutorProfile(userId);
        TutorProfileResponse data = TutorProfileResponseMapper.toResponse(updated);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor profile rejected successfully");
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method để lấy user ID từ authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        return (Integer) authentication.getPrincipal();
    }
}

