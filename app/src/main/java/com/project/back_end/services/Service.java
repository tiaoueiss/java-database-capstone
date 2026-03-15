package com.project.back_end.services;

import com.project.back_end.dto.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repositories.AdminRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Service {

    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 3. Validate Token — returns null on success, ResponseEntity on failure
    public ResponseEntity<Map<String, Object>> validateToken(String token, String role) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean valid = tokenService.validateToken(token, role);
            if (!valid) {
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            return null; // valid
        } catch (Exception e) {
            response.put("message", "Token validation error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // 4. Validate Admin Login
    public ResponseEntity<Map<String, Object>> validateAdmin(Admin admin) {
        Map<String, Object> response = new HashMap<>();
        try {
            Admin found = adminRepository.findByUsername(admin.getUsername());
            if (found == null) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            if (!found.getPassword().equals(admin.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(found.getUsername());
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Admin login error: {}", e.getMessage());
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 5. Filter Doctors
    public ResponseEntity<Map<String, Object>> filterDoctors(String name, String time, String speciality) {
        Map<String, Object> response = new HashMap<>();

        boolean hasName      = name != null      && !name.isBlank()       && !name.equals("null");
        boolean hasTime      = time != null      && !time.isBlank()       && !time.equals("null");
        boolean hasSpec      = speciality != null && !speciality.isBlank() && !speciality.equals("null");

        List<Doctor> doctors;

        if (hasName && hasTime && hasSpec) {
            doctors = doctorService.filterDoctorsByNameSpecialityAndTime(name, speciality, time);
        } else if (hasName && hasTime) {
            doctors = doctorService.filterDoctorByNameAndTime(name, time);
        } else if (hasName && hasSpec) {
            doctors = doctorService.filterDoctorByNameAndSpeciality(name, speciality);
        } else if (hasTime && hasSpec) {
            doctors = doctorService.filterDoctorByTimeAndSpeciality(speciality, time);
        } else if (hasName) {
            doctors = doctorService.findDoctorByName(name);
        } else if (hasTime) {
            doctors = doctorService.filterDoctorsByTime(time);
        } else if (hasSpec) {
            doctors = doctorService.filterDoctorBySpeciality(speciality);
        } else {
            doctors = doctorService.getAllDoctors();
        }

        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }

    // 6. Validate Appointment Time
    public int validateAppointment(Long doctorId, LocalDateTime requestedTime, String date) {
        if (!doctorRepository.existsById(doctorId)) return -1;

        ResponseEntity<Map<String, Object>> availResp = doctorService.getDoctorAvailability(doctorId, date);
        if (availResp.getBody() == null) return 0;

        @SuppressWarnings("unchecked")
        List<String> slots = (List<String>) availResp.getBody().get("availableSlots");
        if (slots == null) return 0;

        String requested = requestedTime.toLocalTime().toString();
        return slots.contains(requested) ? 1 : 0;
    }

    // 7. Validate Patient (check for duplicates)
    public ResponseEntity<Map<String, Object>> checkPatientExists(Patient patient) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = patientRepository.existsByEmailOrPhone(patient.getEmail(), patient.getPhone());
        if (exists) {
            response.put("message", "A patient with this email or phone already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return null; // no conflict
    }

    // 8. Validate Patient Login
    public ResponseEntity<Map<String, Object>> validatePatientLogin(Login login) {
        Map<String, Object> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient == null) {
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Patient login error: {}", e.getMessage());
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 9. Filter Patient Appointments
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(String condition,
                                                                         String name,
                                                                         String token) {
        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);
        Long patientId = patient.getId();

        boolean hasCondition = condition != null && !condition.isBlank() && !condition.equals("null");
        boolean hasName      = name != null      && !name.isBlank()      && !name.equals("null");

        if (hasCondition && hasName) {
            return patientService.filterByDoctorAndCondition(patientId, name, condition);
        } else if (hasCondition) {
            return patientService.filterByCondition(patientId, condition);
        } else if (hasName) {
            return patientService.filterByDoctor(patientId, name);
        } else {
            return patientService.getPatientAppointments(patientId);
        }
    }
}