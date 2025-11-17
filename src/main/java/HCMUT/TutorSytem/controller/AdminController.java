package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Admin: Update student profile
     * POST method as requested
     */
    @PostMapping("/students/{userId}")
    public ResponseEntity<BaseResponse> updateStudentProfile(
            @PathVariable Long userId,
            @RequestBody StudentProfileUpdateRequest request) {
        StudentDTO studentDTO = adminService.updateStudentProfileByAdmin(userId, request);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student profile updated successfully by admin");
        response.setData(studentDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin: Delete student profile
     */
    @DeleteMapping("/students/{userId}")
    public ResponseEntity<BaseResponse> deleteStudentProfile(@PathVariable Long userId) {
        adminService.deleteStudentProfile(userId);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student profile deleted successfully by admin");
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin: Update tutor profile
     */
    @PutMapping("/tutors/{userId}")
    public ResponseEntity<BaseResponse> updateTutorProfile(
            @PathVariable Long userId,
            @RequestBody TutorProfileUpdateRequest request) {
        TutorDetailDTO tutorDetail = adminService.updateTutorProfileByAdmin(userId, request);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor profile updated successfully by admin");
        response.setData(tutorDetail);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin: Delete tutor profile
     */
    @DeleteMapping("/tutors/{userId}")
    public ResponseEntity<BaseResponse> deleteTutorProfile(@PathVariable Long userId) {
        adminService.deleteTutorProfile(userId);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor profile deleted successfully by admin");
        response.setData(null);
        return ResponseEntity.ok(response);
    }
}

