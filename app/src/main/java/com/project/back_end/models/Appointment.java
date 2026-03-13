package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Doctor doctor;

    @ManyToOne
    @NotNull
    private Patient patient;

    @NotNull
    private LocalDateTime appointmentTime;

    @NotNull
    private int status; // 0 = Scheduled, 1 = Completed

    public LocalDateTime getEndTime() {
        return appointmentTime.plusHours(1);
    }

    // Constructors, getters, setters
}