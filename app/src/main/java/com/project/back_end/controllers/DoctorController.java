package com.project.back_end.controllers;

import com.project.back_end.dto.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // GET ${api.path}doctor/availability/{user}/{doctorId}/{date}/{token}
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, user);
        if (tokenCheck != null) return tokenCheck;

        return doctorService.getDoctorAvailability(doctorId, date);
    }

    // GET ${api.path}doctor
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        return ResponseEntity.ok(Map.of("doctors", doctorService.getAllDoctors()));
    }

    // POST ${api.path}doctor/{token}
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> saveDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "admin");
        if (tokenCheck != null) return tokenCheck;

        return doctorService.saveDoctor(doctor);
    }

    // POST ${api.path}doctor/login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(@Valid @RequestBody Login login) {
        return doctorService.doctorLogin(login);
    }

    // PUT ${api.path}doctor/{token}
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "admin");
        if (tokenCheck != null) return tokenCheck;

        return doctorService.updateDoctor(doctor);
    }

    // DELETE ${api.path}doctor/{id}/{token}
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> tokenCheck = service.validateToken(token, "admin");
        if (tokenCheck != null) return tokenCheck;

        return doctorService.deleteDoctor(id);
    }

    // GET ${api.path}doctor/filter/{name}/{time}/{speciality}
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        return service.filterDoctors(name, time, speciality);
    }
}