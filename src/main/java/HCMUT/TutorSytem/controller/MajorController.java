package HCMUT.TutorSytem.controller;

import HCMUT.TutorSytem.dto.MajorDTO;
import HCMUT.TutorSytem.model.Major;
import HCMUT.TutorSytem.payload.response.BaseResponse;
import HCMUT.TutorSytem.repo.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/majors")
public class MajorController {

    @Autowired
    private MajorRepository majorRepository;

    @GetMapping
    public ResponseEntity<BaseResponse> getAllMajors() {
        List<Major> majors = majorRepository.findAll();

        List<MajorDTO> majorDTOs = majors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Majors retrieved successfully");
        response.setData(majorDTOs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<BaseResponse> getMajorsByDepartment(@PathVariable Integer departmentId) {
        List<Major> majors = majorRepository.findByDepartmentId(departmentId);

        List<MajorDTO> majorDTOs = majors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        BaseResponse response = new BaseResponse();
        response.setStatusCode(200);
        response.setMessage("Majors retrieved successfully");
        response.setData(majorDTOs);
        return ResponseEntity.ok(response);
    }

    private MajorDTO convertToDTO(Major major) {
        MajorDTO dto = new MajorDTO();
        dto.setId(major.getId());
        dto.setName(major.getName());
        dto.setMajorCode(major.getMajorCode());
        dto.setProgramCode(major.getProgramCode());
        dto.setNote(major.getNote());

        if (major.getDepartment() != null) {
            dto.setDepartmentId(major.getDepartment().getId());
            dto.setDepartmentName(major.getDepartment().getName());
        }

        return dto;
    }
}

