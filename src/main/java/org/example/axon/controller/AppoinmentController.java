package org.example.axon.controller;

import org.example.axon.dto.response.AppointmentProfile;
import org.example.axon.dto.response.AppointmentResponse;
import org.example.axon.model.Appointment;
import org.example.axon.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppoinmentController {
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppoinments(@RequestParam String doctorId, @RequestParam String patientId){
        try {
            return ResponseEntity.ok(appointmentService.getAllAppointmentResponse(doctorId,patientId));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentProfile> getAppointmentById(@PathVariable int id){
        try {
            return ResponseEntity.ok(appointmentService.getAnAppointmentById(id));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

}
