import {
    apiRequest,
    dashboardPathForRole,
    resolveCurrentUser,
    saveSession,
    setFlash
} from "./api.js";
import {
    consumeFlashInto,
    hideMessage,
    initializeLayout,
    setPageBusy,
    setButtonBusy,
    showMessage
} from "./common.js";

const messageElement = document.getElementById("pageMessage");
const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");
const loginButton = document.getElementById("loginButton");
const registerButton = document.getElementById("registerButton");
const registerRole = document.getElementById("registerRole");
const studentFields = document.getElementById("studentFields");
const createAccountLink = document.getElementById("createAccountLink");

document.addEventListener("DOMContentLoaded", init);

async function init() {
    initializeLayout("auth", null);
    consumeFlashInto(messageElement);
    toggleStudentFields();
    registerRole.addEventListener("change", toggleStudentFields);
    createAccountLink.addEventListener("click", focusRegistration);
    loginForm.addEventListener("submit", handleLogin);
    registerForm.addEventListener("submit", handleRegister);
    setPageBusy(true, "Checking session...");

    try {
        const user = await resolveCurrentUser();
        if (user) {
            window.location.replace(dashboardPathForRole(user.role));
        }
    } catch (error) {
        initializeLayout("auth", null);
    } finally {
        setPageBusy(false);
    }
}

function focusRegistration() {
    document.body.classList.add("registration-focus");
}

function toggleStudentFields() {
    const isStudent = registerRole.value === "STUDENT";
    studentFields.classList.toggle("hidden", !isStudent);

    ["registerBranch", "registerResume", "registerSkills", "registerGraduationYear", "registerCgpa", "registerActiveBacklogs"].forEach((fieldId) => {
        document.getElementById(fieldId).required = isStudent;
    });
}

async function handleLogin(event) {
    event.preventDefault();
    hideMessage(messageElement);
    setButtonBusy(loginButton, true, "Logging in...");

    try {
        const payload = await apiRequest("/api/auth/login", {
            method: "POST",
            skipAuth: true,
            body: {
                email: document.getElementById("loginEmail").value.trim(),
                password: document.getElementById("loginPassword").value
            }
        });

        saveSession(payload.data);
        setFlash("success", `Welcome back, ${payload.data.user.name}.`);
        window.location.replace(dashboardPathForRole(payload.data.user.role));
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    } finally {
        setButtonBusy(loginButton, false);
    }
}

async function handleRegister(event) {
    event.preventDefault();
    hideMessage(messageElement);
    setButtonBusy(registerButton, true, "Creating account...");

    const role = registerRole.value;
    const body = {
        name: document.getElementById("registerName").value.trim(),
        email: document.getElementById("registerEmail").value.trim(),
        password: document.getElementById("registerPassword").value,
        role
    };

    if (role === "STUDENT") {
        body.branch = document.getElementById("registerBranch").value.trim();
        body.resume = document.getElementById("registerResume").value.trim();
        body.skills = document.getElementById("registerSkills").value.trim();
        body.graduationYear = Number(document.getElementById("registerGraduationYear").value);
        body.cgpa = Number(document.getElementById("registerCgpa").value);
        body.activeBacklogs = Number(document.getElementById("registerActiveBacklogs").value);
    }

    try {
        const payload = await apiRequest("/api/auth/register", {
            method: "POST",
            skipAuth: true,
            body
        });

        saveSession(payload.data);
        setFlash("success", `Registration successful. Welcome, ${payload.data.user.name}.`);
        window.location.replace(dashboardPathForRole(payload.data.user.role));
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    } finally {
        setButtonBusy(registerButton, false);
    }
}
