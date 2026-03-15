package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repos.PrescriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PrescriptionService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    // 3. Save Prescription
    public ResponseEntity<Map<String, Object>> savePrescription(Prescription prescription) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long appointmentId = prescription.getAppointmentId();
            boolean exists = prescriptionRepository.existsByAppointmentId(appointmentId);
            if (exists) {
                response.put("message", "A prescription for this appointment already exists");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error saving prescription: {}", e.getMessage());
            response.put("message", "Failed to save prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 4. Get Prescription
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Prescription> prescription = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescription.isPresent()) {
                response.put("prescription", prescription.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "No prescription found for this appointment");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error fetching prescription: {}", e.getMessage());
            response.put("message", "Failed to retrieve prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}