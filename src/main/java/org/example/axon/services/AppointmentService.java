package org.example.axon.services;

import org.example.axon.dto.data.ClinicInfo;
import org.example.axon.dto.data.DoctorAppointmentInfo;
import org.example.axon.dto.data.PatientAppointmentInfo;
import org.example.axon.dto.response.AppointmentProfile;
import org.example.axon.dto.response.AppointmentResponse;
import org.example.axon.mapper.AppointmentMapper;
import org.example.axon.model.Appointment;
import org.example.axon.model.Doctor;
import org.example.axon.model.Hospital;
import org.example.axon.model.Patient;
import org.example.axon.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<AppointmentResponse> getAllAppointmentResponse(String doctorId, String patientId){
        try {
            List<Appointment> appointments;
            Boolean hasDoctorId = !(doctorId != null && doctorId != "");
            Boolean hasPatientId = !(patientId != null && patientId != "");
            if(hasDoctorId && hasPatientId){
                appointments = appointmentRepository.findAllByDoctorUserIdAndPatientUserId(doctorId, patientId);
            } else if (hasDoctorId){
                appointments = appointmentRepository.findAllByDoctorUserId(doctorId);
            }else if(hasPatientId){
                appointments = appointmentRepository.findAllByPatientUserId(patientId);
            }else {
                appointments = appointmentRepository.findAll();
            }
            System.out.println(appointments);
            List<AppointmentResponse> appointmentResponses = new ArrayList<>();
            for(Appointment appointment: appointments){
                appointmentResponses.add(AppointmentMapper.toResponse(appointment));
            }
            return appointmentResponses;
        }catch (Exception e){
            throw e;
        }
    }

    public AppointmentProfile getAnAppointmentById(int id){
        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(()->new RuntimeException());
            DoctorAppointmentInfo doctorAppointmentInfo = null;
            if(appointment.getDoctor()!=null){
                Doctor doctor = appointment.getDoctor();
                ClinicInfo clinicInfo = null;
                if(doctor.getHospitalDepartment()!=null){
                        Hospital hospital = doctor.getHospitalDepartment().getHospital();
                    if(hospital != null){
                        clinicInfo = new ClinicInfo(hospital.getId().toString(),hospital.getName(),hospital.getAddress());
                    }
                }
                doctorAppointmentInfo = new DoctorAppointmentInfo(doctor.getUserId(), doctor.getUsers().getFirstName()
                        ,doctor.getUsers().getLastName(),doctor.getConsultationFee(),clinicInfo);
            }
            PatientAppointmentInfo patientAppointmentInfo = null;
            if(appointment.getPatient()!=null){
                Patient patient = appointment.getPatient();
                patientAppointmentInfo = new PatientAppointmentInfo(patient.getUserId().toString(),
                        patient.getUsers().getFirstName(), patient.getUsers().getLastName(), patient.getUsers().getEmail(),
                        patient.getUsers().getPhoneNumber(),patient.getGender());
            }
            return AppointmentProfile
                    .builder()
                    .id(appointment.getId())
                    .scheduleDate(appointment.getScheduleDate())
                    .scheduleTime(appointment.getScheduleTime())
                    .endTime(appointment.getEndTime())
                    .doctor(doctorAppointmentInfo)
                    .patient(patientAppointmentInfo)
                    .status(appointment.getStatus())
                    .build();

        } catch (Exception e){
            throw e;
        }
    }
}
