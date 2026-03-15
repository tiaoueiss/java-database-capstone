package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  Service service,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    // POST ${api.path}prescription/{token}
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> savePrescription(
            @Valid @RequestBody Prescription prescription,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "doctor");
        if (tokenCheck != null) return tokenCheck;

        appointmentService.updateAppointmentStatus(prescription.getAppointment().getId());
        return prescriptionService.savePrescription(prescription);
    }

    // GET ${api.path}prescription/{appointmentId}/{token}
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "doctor");
        if (tokenCheck != null) return tokenCheck;

        return prescriptionService.getPrescription(appointmentId);
    }
}