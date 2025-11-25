package HCMUT.TutorSytem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import HCMUT.TutorSytem.dto.TutorDTO;
import HCMUT.TutorSytem.payload.request.TutorProfileCreateRequest;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.TutorProfileService;

@RestController
@RequestMapping("/api/tutor-profiles")
public class TutorProfileController {

    @Autowired
    private TutorProfileService tutorProfileService;

    @PostMapping
    public ResponseEntity<BaseResponse> registerTutorProfile(
            @RequestBody TutorProfileCreateRequest request,
            Authentication authentication
    ) {
        Integer currentUserId = (Integer) authentication.getPrincipal();
        TutorDTO tutorProfile = tutorProfileService.registerTutorProfile(currentUserId, request);

        BaseResponse response = new BaseResponse();
        response.setStatusCode(201);
        response.setMessage("Tutor profile created successfully");
        response.setData(tutorProfile);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
