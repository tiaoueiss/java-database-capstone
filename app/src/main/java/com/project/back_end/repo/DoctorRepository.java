package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Find a doctor by exact email match
    Doctor findByEmail(String email);

    // Partial name match (case-sensitive LIKE)
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    // Partial name match (case-insensitive) + exact specialty (case-insensitive)
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(String name, String speciality);

    // Partial name match (case-insensitive) — used by findDoctorByName in DoctorService
    List<Doctor> findByNameContainingIgnoreCase(String name);

    // Exact specialty match (case-insensitive)
    List<Doctor> findBySpecialityIgnoreCase(String speciality);
}