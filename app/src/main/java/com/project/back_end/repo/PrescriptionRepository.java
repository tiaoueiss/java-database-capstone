package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    // Find all prescriptions linked to an appointment
    List<Prescription> findByAppointmentId(Long appointmentId);

    // Existence check — used by PrescriptionService to prevent duplicates
    boolean existsByAppointmentId(Long appointmentId);

    // Single prescription lookup — used by PrescriptionService.getPrescription()
    Optional<Prescription> findFirstByAppointmentId(Long appointmentId);
}