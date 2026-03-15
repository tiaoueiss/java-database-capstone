import { openModal } from "./modal.js";
import { BASE_API_URL } from "./config.js";

const ADMIN_API = `${BASE_API_URL}/admin/login`;
const DOCTOR_API = `${BASE_API_URL}/doctor/login`;

window.onload = () => {
    const adminBtn = document.getElementById("adminLogin");
    const doctorBtn = document.getElementById("doctorLogin");

    if (adminBtn) {
        adminBtn.addEventListener("click", () => {
            openModal("adminLogin");
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => {
            openModal("doctorLogin");
        });
    }
};

/*
  Admin login handler
*/
window.adminLoginHandler = async function () {
    const username = document.getElementById("adminUsername").value;
    const password = document.getElementById("adminPassword").value;

    const admin = { username, password };

    try {
        const response = await fetch(ADMIN_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(admin),
        });

        if (!response.ok) {
            alert("Invalid admin credentials");
            return;
        }

        const data = await response.json();

        localStorage.setItem("token", data.token);
        selectRole("admin");
    } catch (error) {
        console.error(error);
        alert("Login failed. Try again.");
    }
};

/*
  Doctor login handler
*/
window.doctorLoginHandler = async function () {
    const email = document.getElementById("doctorEmail").value;
    const password = document.getElementById("doctorPassword").value;

    const doctor = { email, password };

    try {
        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(doctor),
        });

        if (!response.ok) {
            alert("Invalid doctor credentials");
            return;
        }

        const data = await response.json();

        localStorage.setItem("token", data.token);
        selectRole("doctor");
    } catch (error) {
        console.error(error);
        alert("Login failed.");
    }
};