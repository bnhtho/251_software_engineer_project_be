package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.StudentSessionStatusDTO;
import HCMUT.TutorSytem.model.RegistrationStatus;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.repo.RegistrationStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student-session-statuses")
public class StudentSessionStatusController {

    @Autowired
    private RegistrationStatusRepository registrationStatusRepository;

    @GetMapping
    public ResponseEntity<BaseResponse> getAllStudentSessionStatuses() {
        List<RegistrationStatus> statuses = registrationStatusRepository.findAll();

        List<StudentSessionStatusDTO> statusDTOs = statuses.stream()
                .map(status -> {
                    StudentSessionStatusDTO dto = new StudentSessionStatusDTO();
                    dto.setId(status.getId());
                    dto.setName(status.getName());
                    dto.setDescription(status.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Student session statuses retrieved successfully");
        response.setData(statusDTOs);
        return ResponseEntity.ok(response);
    }
}

