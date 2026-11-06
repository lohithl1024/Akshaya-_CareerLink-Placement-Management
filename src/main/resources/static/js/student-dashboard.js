import {
    apiRequest,
    clearPendingJob,
    escapeHtml,
    formatDate,
    formatDateTime,
    requireAuth,
    setPendingJob,
    titleCase,
    truncate
} from "./api.js";
import {
    consumeFlashInto,
    emptyState,
    hideMessage,
    initializeLayout,
    renderLoadingCards,
    renderLoadingStats,
    renderLoadingSummary,
    renderStats,
    setPageBusy,
    setButtonBusy,
    showMessage
} from "./common.js";

const state = {
    user: null,
    profile: null,
    jobs: [],
    applications: []
};

const messageElement = document.getElementById("pageMessage");
const accountSnapshot = document.getElementById("accountSnapshot");
const profileSummary = document.getElementById("profileSummary");
const availableJobsGrid = document.getElementById("availableJobsGrid");
const recentApplicationsGrid = document.getElementById("recentApplicationsGrid");
const statsGrid = document.getElementById("statsGrid");
const profileForm = document.getElementById("profileForm");
const profileButton = document.getElementById("profileButton");

document.addEventListener("DOMContentLoaded", init);

async function init() {
    setPageBusy(true, "Loading student workspace...");
    const user = await requireAuth(["STUDENT"]);
    if (!user) {
        setPageBusy(false);
        return;
    }

    state.user = user;
    initializeLayout("dashboard", user);
    consumeFlashInto(messageElement);
    clearPendingJob();
    bindEvents();
    renderLoadingSummary(accountSnapshot, 4);
    renderLoadingSummary(profileSummary, 4);
    renderLoadingCards(availableJobsGrid, 3);
    renderLoadingCards(recentApplicationsGrid, 3);
    renderLoadingStats(statsGrid, 4);

    document.getElementById("studentHeroTitle").textContent = `Welcome back, ${user.name}.`;
    document.getElementById("studentHeroSubtitle").textContent =
        "Keep your profile current, scan open roles, and monitor the status of your applications.";

    await loadDashboard();
    setPageBusy(false);
}

function bindEvents() {
    profileForm.addEventListener("submit", updateProfile);
    availableJobsGrid.addEventListener("click", handleJobActions);
}

async function loadDashboard() {
    try {
        const [profilePayload, jobsPayload, applicationsPayload] = await Promise.all([
            apiRequest("/api/students/me"),
            apiRequest("/api/students/me/jobs/available"),
            apiRequest("/api/applications/my")
        ]);

        state.profile = profilePayload.data;
        state.jobs = jobsPayload.data || [];
        state.applications = applicationsPayload.data || [];

        renderAccountSnapshot();
        renderProfile();
        renderJobs();
        renderApplications();
        renderDashboardStats();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    }
}

function renderAccountSnapshot() {
    accountSnapshot.innerHTML = `
        <div class="summary-item">
            <strong>Name</strong>
            <p>${escapeHtml(state.user.name)}</p>
        </div>
        <div class="summary-item">
            <strong>Email</strong>
            <p>${escapeHtml(state.user.email)}</p>
        </div>
        <div class="summary-item">
            <strong>Role</strong>
            <p>${escapeHtml(state.user.role)}</p>
        </div>
        <div class="summary-item">
            <strong>Student ID</strong>
            <p>${escapeHtml(state.user.studentId || "Not linked")}</p>
        </div>
    `;
}

function renderProfile() {
    if (!state.profile) {
        profileSummary.innerHTML = emptyState("Student profile not found.");
        return;
    }

    document.getElementById("profileBranch").value = state.profile.branch || "";
    document.getElementById("profileSkills").value = state.profile.skills || "";
    document.getElementById("profileResume").value = state.profile.resume || "";
    document.getElementById("profileGraduationYear").value = state.profile.graduationYear ?? "";
    document.getElementById("profileCgpa").value = state.profile.cgpa ?? "";
    document.getElementById("profileActiveBacklogs").value = state.profile.activeBacklogs ?? "";

    profileSummary.innerHTML = `
        <div class="summary-item">
            <strong>Name</strong>
            <p>${escapeHtml(state.profile.name)}</p>
        </div>
        <div class="summary-item">
            <strong>Branch</strong>
            <p>${escapeHtml(state.profile.branch)}</p>
        </div>
        <div class="summary-item">
            <strong>Skills</strong>
            <p>${escapeHtml(state.profile.skills)}</p>
        </div>
        <div class="summary-item">
            <strong>Resume</strong>
            <p>${escapeHtml(state.profile.resume)}</p>
        </div>
        <div class="summary-item"><strong>Graduation Year</strong><p>${escapeHtml(state.profile.graduationYear)}</p></div>
        <div class="summary-item"><strong>CGPA</strong><p>${escapeHtml(state.profile.cgpa)}</p></div>
        <div class="summary-item"><strong>Active Backlogs</strong><p>${escapeHtml(state.profile.activeBacklogs)}</p></div>
    `;
}

function renderJobs() {
    if (!state.jobs.length) {
        availableJobsGrid.innerHTML = emptyState("No active jobs are available right now.");
        return;
    }

    availableJobsGrid.innerHTML = state.jobs.slice(0, 6).map((job) => `
        <article class="card">
            <div class="card-head">
                <div class="chip-row">
                    <span class="status-pill success">Active</span>
                    <span class="micro-pill">${escapeHtml(job.companyName)}</span>
                </div>
                <h3>${escapeHtml(job.title)}</h3>
                <p class="card-summary">${escapeHtml(truncate(job.description, 145))}</p>
            </div>
            <div class="detail-grid">
                <div class="detail-item">
                    <span>Eligibility</span>
                    <strong>${escapeHtml(job.eligibility)}</strong>
                </div>
                <div class="detail-item">
                    <span>Deadline</span>
                    <strong>${escapeHtml(formatDate(job.applicationDeadline))}</strong>
                </div>
                <div class="detail-item">
                    <span>Location</span>
                    <strong>${escapeHtml(job.location || "Remote")}</strong>
                </div>
                <div class="detail-item">
                    <span>Company</span>
                    <strong>${escapeHtml(job.companyName)}</strong>
                </div>
            </div>
            <div class="panel-actions">
                <button class="button primary" data-apply-job="${job.id}" type="button">Apply Now</button>
                <a class="button secondary" href="/jobs.html">View Listing</a>
            </div>
        </article>
    `).join("");
}

function renderApplications() {
    if (!state.applications.length) {
        recentApplicationsGrid.innerHTML = emptyState("You have not applied for any jobs yet.");
        return;
    }

    recentApplicationsGrid.innerHTML = state.applications.slice(0, 6).map((application) => `
        <article class="card">
            <div class="card-head">
                <div class="chip-row">
                    <span class="status-pill ${statusTone(application.status)}">${escapeHtml(titleCase(application.status))}</span>
                    <span class="micro-pill">${escapeHtml(application.companyName)}</span>
                </div>
                <h3>${escapeHtml(application.jobTitle)}</h3>
            </div>
            <div class="detail-grid">
                <div class="detail-item">
                    <span>Applied</span>
                    <strong>${escapeHtml(formatDateTime(application.createdAt))}</strong>
                </div>
                <div class="detail-item">
                    <span>Candidate</span>
                    <strong>${escapeHtml(application.studentName)}</strong>
                </div>
            </div>
        </article>
    `).join("");
}

function renderDashboardStats() {
    renderStats(statsGrid, [
        {label: "Active Jobs", value: state.jobs.length},
        {label: "Applications", value: state.applications.length},
        {label: "Branch", value: state.profile?.branch || "Not set"},
        {label: "Resume Ready", value: state.profile?.resume ? "Yes" : "No"}
    ]);
}

async function updateProfile(event) {
    event.preventDefault();
    hideMessage(messageElement);
    setButtonBusy(profileButton, true, "Saving...");

    try {
        const payload = await apiRequest("/api/students/me", {
            method: "PUT",
            body: {
                branch: document.getElementById("profileBranch").value.trim(),
                skills: document.getElementById("profileSkills").value.trim(),
                resume: document.getElementById("profileResume").value.trim()
                , graduationYear: Number(document.getElementById("profileGraduationYear").value)
                , cgpa: Number(document.getElementById("profileCgpa").value)
                , activeBacklogs: Number(document.getElementById("profileActiveBacklogs").value)
            }
        });

        state.profile = payload.data;
        renderProfile();
        renderDashboardStats();
        showMessage(messageElement, "Profile updated successfully.", "success");
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    } finally {
        setButtonBusy(profileButton, false);
    }
}

function handleJobActions(event) {
    const button = event.target.closest("[data-apply-job]");
    if (!button) {
        return;
    }

    const jobId = Number(button.dataset.applyJob);
    setPendingJob(jobId);
    window.location.href = `/applications.html?jobId=${jobId}`;
}

function statusTone(status) {
    if (status === "SELECTED" || status === "SHORTLISTED") {
        return "success";
    }
    if (status === "REJECTED") {
        return "danger";
    }
    return "warning";
}
