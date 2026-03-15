// doctorCard.js

import { deleteDoctor } from "../services/doctorServices.js";
import { fetchPatientDetails } from "../services/patientServices.js";
import { showBookingOverlay } from "../loggedPatient.js";

export function createDoctorCard(doctor) {

    const card = document.createElement("div");
    card.className = "doctor-card";

    const role = localStorage.getItem("userRole");

    /* Doctor info container */
    const info = document.createElement("div");
    info.className = "doctor-info";

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialty = document.createElement("p");
    specialty.textContent = `Specialization: ${doctor.specialization}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    const times = document.createElement("p");
    times.textContent = `Available: ${doctor.availableTimes}`;

    info.append(name, specialty, email, times);

    /* Actions container */
    const actions = document.createElement("div");
    actions.className = "card-actions";

    // ======================
    // ADMIN ACTION
    // ======================

    if (role === "admin") {

        const deleteBtn = document.createElement("button");
        deleteBtn.textContent = "Delete";
        deleteBtn.className = "adminBtn";

        deleteBtn.onclick = async () => {

            const token = localStorage.getItem("token");

            try {

                const response = await deleteDoctor(doctor.id, token);

                if (response.success) {
                    alert("Doctor deleted successfully");
                    card.remove();
                } else {
                    alert("Failed to delete doctor");
                }

            } catch (error) {
                console.error(error);
                alert("Error deleting doctor");
            }

        };

        actions.appendChild(deleteBtn);
    }

        // ======================
        // PATIENT NOT LOGGED
    // ======================

    else if (role === "patient") {

        const bookBtn = document.createElement("button");
        bookBtn.textContent = "Book Now";

        bookBtn.onclick = () => {
            alert("Please login before booking an appointment");
        };

        actions.appendChild(bookBtn);
    }

        // ======================
        // LOGGED PATIENT
    // ======================

    else if (role === "loggedPatient") {

        const bookBtn = document.createElement("button");
        bookBtn.textContent = "Book Now";

        bookBtn.onclick = async () => {

            const token = localStorage.getItem("token");

            if (!token) {
                window.location.href = "/";
                return;
            }

            try {

                const patient = await fetchPatientDetails(token);

                showBookingOverlay({
                    doctor: doctor,
                    patient: patient
                });

            } catch (error) {
                console.error(error);
                alert("Could not load patient data");
            }

        };

        actions.appendChild(bookBtn);
    }

    card.append(info, actions);

    return card;
}