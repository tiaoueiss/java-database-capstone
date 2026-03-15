package com.project.back_end.repositories;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Fetch appointments for a doctor in a time range, eagerly loading doctor's available times
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH d.availableTimes " +
            "WHERE a.doctor.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Fetch appointments for a doctor filtered by patient name (case-insensitive) in a time range
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH a.patient p " +
            "WHERE a.doctor.id = :doctorId " +
            "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
            "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetweenAndPatientNameContainingIgnoreCase(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("patientName") String patientName);

    // Delete all appointments for a given doctor
    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.doctor.id = :doctorId")
    void deleteByDoctorId(@Param("doctorId") Long doctorId);

    // All appointments for a patient
    List<Appointment> findByPatientId(Long patientId);

    // Appointments for a patient filtered by status, sorted by time ascending
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    // Filter by doctor name (LIKE) and patient ID
    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
            "AND a.patient.id = :patientId")
    List<Appointment> findByPatientIdAndDoctorNameContainingIgnoreCase(
            @Param("patientId") Long patientId,
            @Param("doctorName") String doctorName);

    // Filter by doctor name (LIKE), patient ID, and status
    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
            "AND a.patient.id = :patientId AND a.status = :status")
    List<Appointment> findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(
            @Param("patientId") Long patientId,
            @Param("doctorName") String doctorName,
            @Param("status") int status);

    // Appointments by patient and status (used by filterByCondition)
    List<Appointment> findByPatientIdAndStatus(Long patientId, int status);

    // Check if a slot is taken for a given doctor
    boolean existsByDoctorIdAndAppointmentTime(Long doctorId, LocalDateTime appointmentTime);

    // Check if a slot is taken, excluding a specific appointment (for updates)
    boolean existsByDoctorIdAndAppointmentTimeAndIdNot(Long doctorId, LocalDateTime appointmentTime, Long id);

    // Update appointment status by ID
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(@Param("status") int status, @Param("id") long id);
}