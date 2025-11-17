package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.dto.UserDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
}

