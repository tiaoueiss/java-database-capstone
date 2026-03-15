package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // GET /appointments/{token}/{date}/{patientName}
    @GetMapping("/{token}/{date}/{patientName}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "doctor");
        if (tokenCheck != null) return tokenCheck;

        return appointmentService.getAppointments(date, patientName, token);
    }

    // POST /appointments/{token}
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(
            @Valid @RequestBody Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "patient");
        if (tokenCheck != null) return tokenCheck;

        return appointmentService.bookAppointment(appointment, token);
    }

    // PUT /appointments/{token}
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(
            @Valid @RequestBody Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "patient");
        if (tokenCheck != null) return tokenCheck;

        return appointmentService.updateAppointment(appointment);
    }

    // DELETE /appointments/{id}/{token}
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "patient");
        if (tokenCheck != null) return tokenCheck;

        return appointmentService.cancelAppointment(id);
    }
}