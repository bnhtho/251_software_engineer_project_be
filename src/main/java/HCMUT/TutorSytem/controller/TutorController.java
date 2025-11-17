package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.TutorDTO;
import HCMUT.TutorSytem.dto.TutorDetailDTO;
import HCMUT.TutorSytem.payload.request.TutorProfileUpdateRequest;
import HCMUT.TutorSytem.payload.request.TutorRequest;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutors")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    /**
     * Get all tutors (without pagination)
     * Public endpoint - anyone can view tutor list
     */
    @GetMapping
    public ResponseEntity<BaseResponse> getAllTutors() {
        List<TutorDTO> tutors = tutorService.getAllTutors();
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutors retrieved successfully");
        response.setData(tutors);
        return ResponseEntity.ok(response);
    }

    /**
     * Get detailed tutor profile by user ID
     * Includes: teaching schedule, certifications (academic status), experience years, subjects
     * Tutors can only view their own detailed profile
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<BaseResponse> getTutorDetail(@PathVariable Integer userId) {
        // Check ownership: only the tutor can view their own detailed profile
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Integer currentUserId = getCurrentUserId(authentication);
            if (!currentUserId.equals(userId)) {
                BaseResponse response = new BaseResponse();
                response.setStatusCode(403);
                response.setMessage("Access denied: You can only view your own detailed profile");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

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
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<BaseResponse> updateTutorProfile(
            @PathVariable Integer userId,
            @RequestBody TutorProfileUpdateRequest request) {
        // Check ownership: only the tutor can update their own profile
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Integer currentUserId = getCurrentUserId(authentication);
            if (!currentUserId.equals(userId)) {
                BaseResponse response = new BaseResponse();
                response.setStatusCode(403);
                response.setMessage("Access denied: You can only update your own profile");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        TutorDetailDTO tutorDetail = tutorService.updateTutorProfile(userId, request);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor profile updated successfully");
        response.setData(tutorDetail);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new tutor profile
     * This endpoint can be used by authenticated users to become a tutor
     */
    @PostMapping
    public ResponseEntity<BaseResponse> createTutor(@RequestBody TutorRequest tutorRequest) {
        TutorDTO tutor = tutorService.createTutor(tutorRequest);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor created successfully");
        response.setData(tutor);
        return ResponseEntity.ok(response);
    }

    /**
     * Update tutor profile by tutor profile ID
     * Tutors can only update their own profile
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateTutor(@PathVariable Integer id, @RequestBody TutorRequest tutorRequest) {
        // Check ownership: only the tutor owner can update their profile
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Integer currentUserId = getCurrentUserId(authentication);
            Integer profileUserId = tutorService.getUserIdFromTutorProfile(id);

            if (!currentUserId.equals(profileUserId)) {
                BaseResponse response = new BaseResponse();
                response.setStatusCode(403);
                response.setMessage("Access denied: You can only update your own tutor profile");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        TutorDTO tutor = tutorService.updateTutor(id, tutorRequest);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor updated successfully");
        response.setData(tutor);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft delete tutor profile by tutor profile ID (set status to INACTIVE)
     * Tutors can only delete their own profile
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteTutor(@PathVariable Integer id) {
        // Check ownership: only the tutor owner can delete their profile
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Integer currentUserId = getCurrentUserId(authentication);
            Integer profileUserId = tutorService.getUserIdFromTutorProfile(id);

            if (!currentUserId.equals(profileUserId)) {
                BaseResponse response = new BaseResponse();
                response.setStatusCode(403);
                response.setMessage("Access denied: You can only delete your own tutor profile");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        tutorService.deleteTutor(id);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Tutor profile deactivated successfully");
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to get current user ID from authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        return (Integer) authentication.getPrincipal();
    }
}

