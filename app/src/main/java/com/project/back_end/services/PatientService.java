package com.project.back_end.services;

import com.project.back_end.dto.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 3. Create Patient
    public ResponseEntity<Map<String, Object>> createPatient(Patient patient) {
        Map<String, Object> response = new HashMap<>();
        try {
            patientRepository.save(patient);
            response.put("message", "Patient registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating patient: {}", e.getMessage());
            response.put("message", "Failed to register patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 4. Get Patient Appointments
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointments(Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            List<AppointmentDTO> dtos = appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching appointments: {}", e.getMessage());
            response.put("message", "Failed to retrieve appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 5. Filter By Condition (past / future)
    public ResponseEntity<Map<String, Object>> filterByCondition(Long patientId, String condition) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("message", "Invalid condition. Use 'past' or 'future'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            List<AppointmentDTO> dtos = appointmentRepository
                    .findByPatientIdAndStatus(patientId, status)
                    .stream().map(AppointmentDTO::new).collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error filtering by condition: {}", e.getMessage());
            response.put("message", "Failed to filter appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 6. Filter By Doctor Name
    public ResponseEntity<Map<String, Object>> filterByDoctor(Long patientId, String doctorName) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AppointmentDTO> dtos = appointmentRepository
                    .findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, doctorName)
                    .stream().map(AppointmentDTO::new).collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error filtering by doctor: {}", e.getMessage());
            response.put("message", "Failed to filter appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 7. Filter By Doctor and Condition
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(Long patientId,
                                                                          String doctorName,
                                                                          String condition) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("message", "Invalid condition. Use 'past' or 'future'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            List<AppointmentDTO> dtos = appointmentRepository
                    .findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(patientId, doctorName, status)
                    .stream().map(AppointmentDTO::new).collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error filtering by doctor and condition: {}", e.getMessage());
            response.put("message", "Failed to filter appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 8. Get Patient Details from Token
    public ResponseEntity<Map<String, Object>> getPatient(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("patient", patient);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching patient details: {}", e.getMessage());
            response.put("message", "Failed to retrieve patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}