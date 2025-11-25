package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.SessionDTO;
import HCMUT.TutorSytem.payload.request.SessionRequest;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.SessionService;
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
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    /**
     * Get all sessions (with pagination)
     * Mặc định: 10 items per page
     *
     * @param page Số trang (bắt đầu từ 0, mặc định = 0)
     */
    @GetMapping
    public ResponseEntity<BaseResponse> getAllSessions(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<SessionDTO> sessionsPage = sessionService.getAllSessions(pageable);
        Map<String, Object> paginatedData = PaginationUtil.createPaginationResponse(sessionsPage);

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Sessions retrieved successfully");
        response.setData(paginatedData);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new session
     */
    @PostMapping
    public ResponseEntity<BaseResponse> createSession(@RequestBody SessionRequest sessionRequest) {
        SessionDTO session = sessionService.createSession(sessionRequest);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Session created successfully");
        response.setData(session);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing session
     * Only the tutor who created the session can update it
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateSession(@PathVariable Integer id, @RequestBody SessionRequest sessionRequest) {
        // Check ownership: only the tutor who created the session can update it
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Integer currentUserId = getCurrentUserId(authentication);
            Integer sessionTutorId = sessionService.getTutorIdFromSession(id);

            if (!currentUserId.equals(sessionTutorId)) {
                BaseResponse response = new BaseResponse();
                response.setStatusCode(403);
                response.setMessage("Access denied: You can only update your own sessions");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        SessionDTO session = sessionService.updateSession(id, sessionRequest);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Session updated successfully");
        response.setData(session);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a session
     * Only the tutor who created the session can delete it
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteSession(@PathVariable Integer id) {
        // Check ownership: only the tutor who created the session can delete it
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Integer currentUserId = getCurrentUserId(authentication);
            Integer sessionTutorId = sessionService.getTutorIdFromSession(id);

            if (!currentUserId.equals(sessionTutorId)) {
                BaseResponse response = new BaseResponse();
                response.setStatusCode(403);
                response.setMessage("Access denied: You can only delete your own sessions");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        sessionService.deleteSession(id);
        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Session deleted successfully");
        response.setData(null);
        return ResponseEntity.ok(response);
    }


    private Integer getCurrentUserId(Authentication authentication) {
        return (Integer) authentication.getPrincipal();
    }
}

