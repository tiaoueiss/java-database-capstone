import { BASE_API_URL } from "./config.js";

const DOCTOR_API = `${BASE_API_URL}/doctors`;

/*
  Fetch all doctors
*/
export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API);
        const data = await response.json();
        return data.doctors || [];
    } catch (error) {
        console.error("Error fetching doctors:", error);
        return [];
    }
}

/*
  Delete doctor (admin only)
*/
export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
            method: "DELETE",
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message,
        };
    } catch (error) {
        console.error("Delete doctor error:", error);
        return { success: false, message: "Delete failed" };
    }
}

/*
  Save new doctor
*/
export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${token}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(doctor),
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message,
        };
    } catch (error) {
        console.error("Save doctor error:", error);
        return { success: false, message: "Save failed" };
    }
}

/*
  Filter doctors
*/
export async function filterDoctors(name, time, specialty) {
    try {
        const response = await fetch(
            `${DOCTOR_API}/filter/${name}/${time}/${specialty}`
        );

        if (!response.ok) {
            console.error("Filter request failed");
            return { doctors: [] };
        }

        return await response.json();
    } catch (error) {
        console.error("Filter doctors error:", error);
        return { doctors: [] };
    }
}