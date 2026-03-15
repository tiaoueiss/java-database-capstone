package com.project.back_end.controllers;

import com.project.back_end.dto.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    // GET /patient/{token}
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "patient");
        if (tokenCheck != null) return tokenCheck;

        return patientService.getPatient(token);
    }

    // POST /patient
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPatient(@Valid @RequestBody Patient patient) {
        ResponseEntity<Map<String, Object>> existsCheck = service.checkPatientExists(patient);
        if (existsCheck != null) return existsCheck;

        return patientService.createPatient(patient);
    }

    // POST /patient/login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // GET /patient/appointments/{id}/{token}/{user}
    @GetMapping("/appointments/{id}/{token}/{user}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token,
            @PathVariable String user) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, user);
        if (tokenCheck != null) return tokenCheck;

        return patientService.getPatientAppointments(id);
    }

    // GET /patient/appointments/filter/{condition}/{name}/{token}
    @GetMapping("/appointments/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "patient");
        if (tokenCheck != null) return tokenCheck;

        return service.filterPatientAppointments(condition, name, token);
    }
}