package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final Service service;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              Service service,
                              TokenService tokenService,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // 4. Book Appointment
    @Transactional
    public ResponseEntity<Map<String, Object>> bookAppointment(Appointment appointment, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validate doctor exists
            if (!doctorRepository.existsById(appointment.getDoctor().getId())) {
                response.put("message", "Invalid doctor ID");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Check for time conflict
            boolean slotTaken = appointmentRepository.existsByDoctorIdAndAppointmentTime(
                    appointment.getEndTime(
            if (slotTaken) {
                response.put("message", "This time slot is already booked");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Resolve patient from token
            Long patientId = tokenService.extractUserId(token);
            appointment.setPatient(patientRepository.findById(patientId).orElseThrow());

            appointmentRepository.save(appointment);
            response.put("message", "Appointment booked successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("message", "Failed to book appointment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 5. Update Appointment
    @Transactional
    public ResponseEntity<Map<String, Object>> updateAppointment(Appointment appointment) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
            if (existing.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment current = existing.get();

            // Validate patient ownership
            if (!current.getPatient().getId().equals(appointment.getPatient().getId())) {
                response.put("message", "Unauthorized: patient mismatch");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Check doctor availability at the new time (excluding this appointment)
            boolean slotTaken = appointmentRepository.existsByDoctorIdAndAppointmentTimeAndIdNot(
                    appointment.getDoctor().getId(),
                    appointment.getAppointmentTime(),
                    appointment.getId()
            );
            if (slotTaken) {
                response.put("message", "Doctor is not available at the requested time");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            current.setAppointmentTime(appointment.getAppointmentTime());
            current.setDoctor(appointment.getDoctor());
            appointmentRepository.save(current);

            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to update appointment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 6. Cancel Appointment
    @Transactional
    public ResponseEntity<Map<String, Object>> cancelAppointment(Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Appointment> existing = appointmentRepository.findById(id);
            if (existing.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            appointmentRepository.deleteById(id);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to cancel appointment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 7. Get Appointments
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getAppointments(String date, String patientName, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long doctorId = tokenService.extractUserId(token);
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime start = localDate.atStartOfDay();
            LocalDateTime end = localDate.plusDays(1).atStartOfDay();

            List<Appointment> appointments;
            if (patientName == null || patientName.isBlank() || patientName.equals("null")) {
                appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
            } else {
                appointments = appointmentRepository
                        .findByDoctorIdAndAppointmentTimeBetweenAndPatientNameContainingIgnoreCase(
                                doctorId, start, end, patientName);
            }

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to retrieve appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 8. Change Appointment Status
    @Transactional
    public void updateAppointmentStatus(Long appointmentId) {
        appointmentRepository.findById(appointmentId).ifPresent(appointment -> {
            appointment.setStatus(1); // 1 = prescription issued
            appointmentRepository.save(appointment);
        });
    }
}