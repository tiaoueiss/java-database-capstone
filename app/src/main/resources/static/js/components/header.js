import {openModal} from "./modals";

function renderHeader() {

    const headerDiv = document.getElementById("header");

    // If user is on root page
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");

        headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>
    `;
        return;
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>
  `;

    // Session validation
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Admin header
    if (role === "admin") {
        headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">
        Add Doctor
      </button>
      <a href="#" onclick="logout()">Logout</a>
    `;
    }

    // Doctor header
    else if (role === "doctor") {
        headerContent += `
      <button class="adminBtn" onclick="selectRole('doctor')">
        Home
      </button>
      <a href="#" onclick="logout()">Logout</a>
    `;
    }

    // Patient (not logged in)
    else if (role === "patient") {
        headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>
    `;
    }

    // Logged patient
    else if (role === "loggedPatient") {
        headerContent += `
      <button class="adminBtn"
        onclick="window.location.href='/pages/loggedPatientDashboard.html'">
        Home
      </button>

      <button class="adminBtn"
        onclick="window.location.href='/pages/patientAppointments.html'">
        Appointments
      </button>

      <a href="#" onclick="logoutPatient()">Logout</a>
    `;
    }

    headerContent += `
      </nav>
    </header>
  `;

    headerDiv.innerHTML = headerContent;

    attachHeaderButtonListeners();
}
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/pages/patientDashboard.html";
}

function attachHeaderButtonListeners() {

    const loginBtn = document.getElementById("patientLogin");
    const signupBtn = document.getElementById("patientSignup");

    if (loginBtn) {
        loginBtn.addEventListener("click", () => {
            openModal("patientLogin");
        });
    }

    if (signupBtn) {
        signupBtn.addEventListener("click", () => {
            openModal("patientSignup");
        });
    }

}
renderHeader();