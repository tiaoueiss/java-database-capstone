package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find patient by email
    Patient findByEmail(String email);

    // Find patient by email or phone (for duplicate checks)
    Patient findByEmailOrPhone(String email, String phone);

    // Existence check used by Service.checkPatientExists()
    boolean existsByEmailOrPhone(String email, String phone);
}