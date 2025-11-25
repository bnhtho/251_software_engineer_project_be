package HCMUT.TutorSytem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import HCMUT.TutorSytem.dto.TutorProfileResponse;
import HCMUT.TutorSytem.mapper.TutorProfileResponseMapper;
import HCMUT.TutorSytem.model.TutorProfile;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.service.TutorProfileService;

@RestController
@RequestMapping("/api/admin/tutor_profiles")
public class AdminTutorProfileController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private TutorProfileService tutorProfileService;

    @GetMapping("/pending")

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
}
