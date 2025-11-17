package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.StudentDTO;
import HCMUT.TutorSytem.dto.StudentSessionHistoryDTO;
import HCMUT.TutorSytem.payload.request.StudentProfileUpdateRequest;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * Get student profile by user ID
     * Students can only view their own profile
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<BaseResponse> getStudentProfile(@PathVariable Long userId) {
        // Check ownership: only the student can view their own profile
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Long currentUserId = getCurrentUserId(authentication);
            if (!currentUserId.equals(userId)) {
                BaseResponse response = new BaseResponse();
                response.setStatusCode(403);
                response.setMessage("Access denied: You can only view your own profile");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        StudentDTO studentDTO = studentService.getStudentProfile(userId);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student profile retrieved successfully");
        response.setData(studentDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Get student's learning history (session history)
     * Students can only view their own history
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<BaseResponse> getStudentSessionHistory(@PathVariable Long userId) {
        // Check ownership: only the student can view their own history
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Long currentUserId = getCurrentUserId(authentication);
            if (!currentUserId.equals(userId)) {
                BaseResponse response = new BaseResponse();
                response.setStatusCode(403);
                response.setMessage("Access denied: You can only view your own learning history");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        List<StudentSessionHistoryDTO> history = studentService.getStudentSessionHistory(userId);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student session history retrieved successfully");
        response.setData(history);
        return ResponseEntity.ok(response);
    }

    /**
     * Update student profile
     * Students can only update their own profile
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<BaseResponse> updateStudentProfile(
            @PathVariable Long userId,
            @RequestBody StudentProfileUpdateRequest request) {
        // Check ownership: only the student can update their own profile
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Long currentUserId = getCurrentUserId(authentication);
            if (!currentUserId.equals(userId)) {
                BaseResponse response = new BaseResponse();
                response.setStatusCode(403);
                response.setMessage("Access denied: You can only update your own profile");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        StudentDTO studentDTO = studentService.updateStudentProfile(userId, request);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student profile updated successfully");
        response.setData(studentDTO);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}

