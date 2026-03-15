package com.project.back_end.services;

import com.project.back_end.dto.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 4. Get Doctor Availability
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(Long doctorId, String date) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Doctor doctor = doctorOpt.get();
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime start = localDate.atStartOfDay();
            LocalDateTime end = localDate.plusDays(1).atStartOfDay();

            // All slots the doctor normally offers
            List<LocalTime> allSlots = doctor.getAvailableTimes();

            // Already booked times on this date
            List<LocalTime> bookedTimes = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end)
                    .stream()
                    .map(a -> a.getAppointmentTime().toLocalTime())
                    .collect(Collectors.toList());

            // Filter out booked slots
            List<String> availableSlots = allSlots.stream()
                    .filter(slot -> !bookedTimes.contains(slot))
                    .map(LocalTime::toString)
                    .collect(Collectors.toList());

            response.put("availableSlots", availableSlots);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching availability: {}", e.getMessage());
            response.put("message", "Failed to retrieve availability");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 5. Save Doctor
    public ResponseEntity<Map<String, Object>> saveDoctor(Doctor doctor) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                response.put("message", "Doctor with this email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            doctorRepository.save(doctor);
            response.put("message", "Doctor registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error saving doctor: {}", e.getMessage());
            response.put("message", "Failed to save doctor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 6. Update Doctor
    public ResponseEntity<Map<String, Object>> updateDoctor(Doctor doctor) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!doctorRepository.existsById(doctor.getId())) {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            doctorRepository.save(doctor);
            response.put("message", "Doctor updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating doctor: {}", e.getMessage());
            response.put("message", "Failed to update doctor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 7. Get All Doctors
    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        // Force-load available times if lazily loaded
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    // 8. Delete Doctor
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteDoctor(Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!doctorRepository.existsById(id)) {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            appointmentRepository.deleteByDoctorId(id);
            doctorRepository.deleteById(id);
            response.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting doctor: {}", e.getMessage());
            response.put("message", "Failed to delete doctor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 9. Validate Doctor Login
    public ResponseEntity<Map<String, Object>> doctorLogin(Login login) {
        Map<String, Object> response = new HashMap<>();
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getEmail());
            if (doctor == null) {
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            if (!doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(doctor.getEmail());
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Doctor login error: {}", e.getMessage());
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 10. Find Doctor By Name
    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCase(name);
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    // 11. Filter By Name, Speciality, and Time
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByNameSpecialityAndTime(String name, String speciality, String time) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(name, speciality);
        doctors.forEach(d -> d.getAvailableTimes().size());
        return filterDoctorByTime(doctors, time);
    }

    // 12. Filter Doctor List By Time (AM/PM)
    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String time) {
        return doctors.stream().filter(doctor -> {
            List<LocalTime> slots = doctor.getAvailableTimes();
            if (slots == null || slots.isEmpty()) return false;
            return slots.stream().anyMatch(slot -> {
                if ("AM".equalsIgnoreCase(time)) return slot.getHour() < 12;
                if ("PM".equalsIgnoreCase(time)) return slot.getHour() >= 12;
                return false;
            });
        }).collect(Collectors.toList());
    }

    // 13. Filter By Name and Time
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndTime(String name, String time) {
        List<Doctor> doctors = findDoctorByName(name);
        return filterDoctorByTime(doctors, time);
    }

    // 14. Filter By Name and Speciality
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndSpeciality(String name, String speciality) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(name, speciality);
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    // 15. Filter By Time and Speciality
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByTimeAndSpeciality(String speciality, String time) {
        List<Doctor> doctors = doctorRepository.findBySpecialityIgnoreCase(speciality);
        doctors.forEach(d -> d.getAvailableTimes().size());
        return filterDoctorByTime(doctors, time);
    }

    // 16. Filter By Speciality
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorBySpeciality(String speciality) {
        List<Doctor> doctors = doctorRepository.findBySpecialityIgnoreCase(speciality);
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    // 17. Filter All Doctors By Time
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByTime(String time) {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors.forEach(d -> d.getAvailableTimes().size());
        return filterDoctorByTime(doctors, time);
    }
}