import {
    apiRequest,
    clearPendingJob,
    escapeHtml,
    formatDate,
    formatDateTime,
    getPendingJob,
    requireAuth,
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
    jobs: [],
    applications: [],
    selectedJob: null
};

const messageElement = document.getElementById("pageMessage");
const statsGrid = document.getElementById("statsGrid");
const applicationsSummary = document.getElementById("applicationsSummary");
const studentView = document.getElementById("studentView");
const managerView = document.getElementById("managerView");
const selectedJobCard = document.getElementById("selectedJobCard");
const applyForm = document.getElementById("applyForm");
const applyButton = document.getElementById("applyButton");
const studentApplicationsGrid = document.getElementById("studentApplicationsGrid");
const managerFilterForm = document.getElementById("managerFilterForm");
const managerJobFilter = document.getElementById("managerJobFilter");
const managerStatusFilter = document.getElementById("managerStatusFilter");
const applicationsTableBody = document.getElementById("applicationsTableBody");
const resetManagerFiltersButton = document.getElementById("resetManagerFiltersButton");

document.addEventListener("DOMContentLoaded", init);

async function init() {
    setPageBusy(true, "Loading applications...");
    const user = await requireAuth(["STUDENT", "ADMIN", "RECRUITER"]);
    if (!user) {
        setPageBusy(false);
        return;
    }

    state.user = user;
    initializeLayout("applications", user);
    consumeFlashInto(messageElement);
    renderLoadingSummary(applicationsSummary, 3);
    renderLoadingStats(statsGrid, 4);

    if (user.role === "STUDENT") {
        configureStudentView();
        bindStudentEvents();
        renderLoadingCards(selectedJobCard, 1);
        renderLoadingCards(studentApplicationsGrid, 3);
        await loadStudentView();
        setPageBusy(false);
        return;
    }

    configureManagerView();
    bindManagerEvents();
    await loadManagerView();
    setPageBusy(false);
}

function configureStudentView() {
    document.getElementById("applicationsBadge").textContent = "Student Applications";
    document.getElementById("applicationsTitle").textContent = "Submit and track your applications.";
    document.getElementById("applicationsSubtitle").textContent =
        "Selected jobs appear here for quick submission, and your recent applications stay visible below.";

    managerView.classList.add("hidden");
    studentView.classList.remove("hidden");
}

function configureManagerView() {
    const roleTitle = state.user.role === "ADMIN" ? "Admin Application Review" : "Recruiter Application Review";
    document.getElementById("applicationsBadge").textContent = roleTitle;
    document.getElementById("applicationsTitle").textContent = "Review candidate pipelines and update statuses.";
    document.getElementById("applicationsSubtitle").textContent =
        "Filter applications by job or status, then move candidates through the process from one page.";

    studentView.classList.add("hidden");
    managerView.classList.remove("hidden");
}

function bindStudentEvents() {
    applyForm.addEventListener("submit", submitApplication);
}

function bindManagerEvents() {
    managerFilterForm.addEventListener("submit", handleManagerFilter);
    resetManagerFiltersButton.addEventListener("click", resetManagerFilters);
    applicationsTableBody.addEventListener("click", updateApplicationStatus);
}

async function loadStudentView() {
    try {
        const [applicationsPayload, selectedJob] = await Promise.all([
            apiRequest("/api/applications/my"),
            loadSelectedJob()
        ]);

        state.applications = applicationsPayload.data || [];
        state.selectedJob = selectedJob;

        renderStudentSummary();
        renderSelectedJob();
        renderStudentStats();
        renderStudentApplications();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    }
}

async function loadSelectedJob() {
    const queryJobId = new URLSearchParams(window.location.search).get("jobId");
    const jobId = Number(queryJobId || getPendingJob());

    if (!jobId) {
        return null;
    }

    try {
        const payload = await apiRequest(`/api/jobs/${jobId}`);
        return payload.data;
    } catch (error) {
        clearPendingJob();
        return null;
    }
}

function renderStudentSummary() {
    applicationsSummary.innerHTML = `
        <div class="summary-item">
            <strong>Name</strong>
            <p>${escapeHtml(state.user.name)}</p>
        </div>
        <div class="summary-item">
            <strong>Email</strong>
            <p>${escapeHtml(state.user.email)}</p>
        </div>
        <div class="summary-item">
            <strong>Total Applications</strong>
            <p>${escapeHtml(state.applications.length)}</p>
        </div>
    `;
}

function renderStudentStats() {
    renderStats(statsGrid, [
        {label: "Applied Jobs", value: state.applications.length},
        {label: "Shortlisted", value: state.applications.filter((item) => item.status === "SHORTLISTED").length},
        {label: "Selected", value: state.applications.filter((item) => item.status === "SELECTED").length},
        {label: "Pending Target", value: state.selectedJob ? "Yes" : "No"}
    ]);
}

function renderSelectedJob() {
    if (!state.selectedJob) {
        selectedJobCard.innerHTML = emptyState("Choose a job from the listings page to start an application.");
        applyForm.classList.add("hidden");
        return;
    }

    if (!state.selectedJob.active) {
        state.selectedJob = null;
        selectedJobCard.innerHTML = emptyState("The selected job is no longer active.");
        applyForm.classList.add("hidden");
        clearPendingJob();
        return;
    }

    document.getElementById("selectedJobId").value = state.selectedJob.id;
    applyForm.classList.remove("hidden");
    selectedJobCard.innerHTML = `
        <article class="card">
            <div class="card-head">
                <div class="chip-row">
                    <span class="status-pill ${state.selectedJob.active ? "success" : "warning"}">
                        ${state.selectedJob.active ? "Active" : "Inactive"}
                    </span>
                    <span class="micro-pill">${escapeHtml(state.selectedJob.companyName)}</span>
                </div>
                <h3>${escapeHtml(state.selectedJob.title)}</h3>
                <p class="card-summary">${escapeHtml(truncate(state.selectedJob.description, 150))}</p>
            </div>
            <div class="detail-grid">
                <div class="detail-item">
                    <span>Eligibility</span>
                    <strong>${escapeHtml(state.selectedJob.eligibility)}</strong>
                </div>
                <div class="detail-item">
                    <span>Deadline</span>
                    <strong>${escapeHtml(formatDate(state.selectedJob.applicationDeadline))}</strong>
                </div>
                <div class="detail-item">
                    <span>Location</span>
                    <strong>${escapeHtml(state.selectedJob.location || "Remote")}</strong>
                </div>
                <div class="detail-item">
                    <span>Company</span>
                    <strong>${escapeHtml(state.selectedJob.companyName)}</strong>
                </div>
            </div>
        </article>
    `;
}

function renderStudentApplications() {
    if (!state.applications.length) {
        studentApplicationsGrid.innerHTML = emptyState("No applications submitted yet.");
        return;
    }

    studentApplicationsGrid.innerHTML = state.applications.map((application) => `
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
                    <span>Applicant</span>
                    <strong>${escapeHtml(application.studentName)}</strong>
                </div>
            </div>
        </article>
    `).join("");
}

async function submitApplication(event) {
    event.preventDefault();
    hideMessage(messageElement);
    setButtonBusy(applyButton, true, "Applying...");

    try {
        const jobId = Number(document.getElementById("selectedJobId").value);
        await apiRequest("/api/applications", {
            method: "POST",
            body: {jobId}
        });

        clearPendingJob();
        state.selectedJob = null;
        window.history.replaceState({}, "", "/applications.html");
        showMessage(messageElement, "Application submitted successfully.", "success");
        await loadStudentView();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    } finally {
        setButtonBusy(applyButton, false);
    }
}

async function loadManagerView() {
    try {
        const jobId = new URLSearchParams(window.location.search).get("jobId");
        const [jobsPayload, applicationsPayload] = await Promise.all([
            apiRequest("/api/jobs"),
            jobId ? apiRequest(`/api/applications/job/${jobId}`) : apiRequest("/api/applications")
        ]);

        state.jobs = jobsPayload.data || [];
        state.applications = applicationsPayload.data || [];

        renderManagerSummary();
        renderManagerStats();
        renderManagerFilters(jobId);
        renderManagerTable();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    }
}

function renderManagerSummary() {
    applicationsSummary.innerHTML = `
        <div class="summary-item">
            <strong>Name</strong>
            <p>${escapeHtml(state.user.name)}</p>
        </div>
        <div class="summary-item">
            <strong>Role</strong>
            <p>${escapeHtml(state.user.role)}</p>
        </div>
        <div class="summary-item">
            <strong>Applications Loaded</strong>
            <p>${escapeHtml(state.applications.length)}</p>
        </div>
    `;
}

function renderManagerStats() {
    renderStats(statsGrid, [
        {label: "Applications", value: state.applications.length},
        {label: "Jobs", value: state.jobs.length},
        {label: "Shortlisted", value: state.applications.filter((item) => item.status === "SHORTLISTED").length},
        {label: "Selected", value: state.applications.filter((item) => item.status === "SELECTED").length}
    ]);
}

function renderManagerFilters(preselectedJobId) {
    managerJobFilter.innerHTML = `
        <option value="">All jobs</option>
        ${state.jobs.map((job) => `
            <option value="${job.id}" ${String(job.id) === String(preselectedJobId || "") ? "selected" : ""}>
                ${escapeHtml(job.title)} - ${escapeHtml(job.companyName)}
            </option>
        `).join("")}
    `;
}

function renderManagerTable() {
    const filteredApplications = state.applications.filter((application) => {
        const statusValue = managerStatusFilter.value;
        if (!statusValue) {
            return true;
        }
        return application.status === statusValue;
    });

    if (!filteredApplications.length) {
        applicationsTableBody.innerHTML = `
            <tr>
                <td colspan="6">${emptyState("No applications matched the selected filters.")}</td>
            </tr>
        `;
        return;
    }

    applicationsTableBody.innerHTML = filteredApplications.map((application) => `
        <tr>
            <td>${escapeHtml(application.studentName)}</td>
            <td>${escapeHtml(application.jobTitle)}</td>
            <td>${escapeHtml(application.companyName)}</td>
            <td>
                <select id="status-${application.id}">
                    ${["APPLIED", "IN_REVIEW", "SHORTLISTED", "REJECTED", "SELECTED"].map((status) => `
                        <option value="${status}" ${status === application.status ? "selected" : ""}>${escapeHtml(titleCase(status))}</option>
                    `).join("")}
                </select>
            </td>
            <td>${escapeHtml(formatDateTime(application.createdAt))}</td>
            <td class="table-actions">
                <button class="button secondary" data-update-status="${application.id}" type="button">Save</button>
            </td>
        </tr>
    `).join("");
}

async function handleManagerFilter(event) {
    event.preventDefault();
    hideMessage(messageElement);

    try {
        const jobId = managerJobFilter.value;
        const payload = await apiRequest(jobId ? `/api/applications/job/${jobId}` : "/api/applications");
        state.applications = payload.data || [];
        renderManagerStats();
        renderManagerTable();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    }
}

async function resetManagerFilters() {
    managerFilterForm.reset();
    window.history.replaceState({}, "", "/applications.html");
    await loadManagerView();
}

async function updateApplicationStatus(event) {
    const button = event.target.closest("[data-update-status]");
    if (!button) {
        return;
    }

    hideMessage(messageElement);
    const applicationId = Number(button.dataset.updateStatus);
    const select = document.getElementById(`status-${applicationId}`);
    setButtonBusy(button, true, "Saving...");

    try {
        await apiRequest(`/api/applications/${applicationId}/status`, {
            method: "PATCH",
            body: {status: select.value}
        });

        showMessage(messageElement, "Application status updated successfully.", "success");
        await handleManagerFilter(new Event("submit"));
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    } finally {
        setButtonBusy(button, false);
    }
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
