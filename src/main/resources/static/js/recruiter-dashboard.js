import {
    apiRequest,
    escapeHtml,
    formatDateTime,
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
    companies: [],
    jobs: [],
    applications: []
};

const messageElement = document.getElementById("pageMessage");
const statsGrid = document.getElementById("statsGrid");
const managerSummary = document.getElementById("managerSummary");
const companiesList = document.getElementById("companiesList");
const companyForm = document.getElementById("companyForm");
const companyButton = document.getElementById("companyButton");
const jobForm = document.getElementById("jobForm");
const jobButton = document.getElementById("jobButton");
const cancelEditButton = document.getElementById("cancelEditButton");
const jobCompanyId = document.getElementById("jobCompanyId");
const managedJobsGrid = document.getElementById("managedJobsGrid");
const applicationPreviewGrid = document.getElementById("applicationPreviewGrid");

document.addEventListener("DOMContentLoaded", init);

async function init() {
    setPageBusy(true, "Loading management workspace...");
    const user = await requireAuth(["ADMIN", "RECRUITER"]);
    if (!user) {
        setPageBusy(false);
        return;
    }

    state.user = user;
    initializeLayout("dashboard", user);
    consumeFlashInto(messageElement);
    configureRoleView();
    bindEvents();
    renderLoadingSummary(managerSummary, 3);
    renderLoadingSummary(companiesList, 3);
    renderLoadingCards(managedJobsGrid, 3);
    renderLoadingCards(applicationPreviewGrid, 3);
    renderLoadingStats(statsGrid, 4);
    await loadDashboard();
    setPageBusy(false);
}

function configureRoleView() {
    if (state.user.role === "ADMIN") {
        document.getElementById("dashboardRoleBadge").textContent = "Admin Workspace";
        document.getElementById("dashboardHeroTitle").textContent = "Admin control over companies and job postings.";
        document.getElementById("dashboardHeroSubtitle").textContent =
            "Create companies, support recruiters, and monitor platform-wide application activity.";
        return;
    }

    document.getElementById("dashboardHeroTitle").textContent = "Manage recruitment opportunities with control.";
    document.getElementById("dashboardHeroSubtitle").textContent =
        "Add your company, publish openings, and keep candidates informed through one placement workspace.";
}

function bindEvents() {
    companyForm.addEventListener("submit", saveCompany);
    jobForm.addEventListener("submit", saveJob);
    managedJobsGrid.addEventListener("click", handleJobActions);
    cancelEditButton.addEventListener("click", resetJobForm);
}

async function loadDashboard() {
    try {
        const managementPath = state.user.role === "ADMIN" ? "" : "/mine";
        const [companiesPayload, jobsPayload, applicationsPayload] = await Promise.all([
            apiRequest(`/api/companies${managementPath}`),
            apiRequest(`/api/jobs${managementPath}`),
            apiRequest(`/api/applications${managementPath}`)
        ]);

        state.companies = companiesPayload.data || [];
        state.jobs = jobsPayload.data || [];
        state.applications = applicationsPayload.data || [];

        renderManagerSummary();
        populateCompanies();
        renderCompanies();
        renderJobs();
        renderApplicationsPreview();
        renderStatsBar();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    }
}

function renderManagerSummary() {
    managerSummary.innerHTML = `
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
    `;
}

function populateCompanies() {
    if (!state.companies.length) {
        jobCompanyId.innerHTML = '<option value="">Add a company first</option>';
        jobCompanyId.disabled = true;
        return;
    }

    jobCompanyId.disabled = false;
    jobCompanyId.innerHTML = state.companies.map((company) => `
        <option value="${company.id}">${escapeHtml(company.name)}</option>
    `).join("");
}

function renderCompanies() {
    if (!state.companies.length) {
        companiesList.innerHTML = emptyState("No companies yet. Add your company above to begin posting jobs.");
        return;
    }

    companiesList.innerHTML = state.companies.map((company) => `
        <div class="summary-item">
            <strong>${escapeHtml(company.name)}</strong>
            <p>${escapeHtml(truncate(company.description, 140))}</p>
        </div>
    `).join("");
}

function renderJobs() {
    if (!state.jobs.length) {
        managedJobsGrid.innerHTML = emptyState("No jobs posted yet.");
        return;
    }

    managedJobsGrid.innerHTML = state.jobs.slice(0, 8).map((job) => `
        <article class="card management-card">
            <div class="card-head">
                <div class="chip-row">
                    <span class="status-pill ${job.active ? "success" : "warning"}">
                        ${job.active ? "Active" : "Inactive"}
                    </span>
                    <span class="micro-pill">${escapeHtml(job.companyName)}</span>
                </div>
                <h3>${escapeHtml(job.title)}</h3>
                <p class="card-summary">${escapeHtml(truncate(job.description, 115))}</p>
            </div>
            <div class="management-meta">
                <span><strong>Eligibility</strong>${escapeHtml(truncate(job.eligibility, 78))}</span>
                <span><strong>Posted</strong>${escapeHtml(formatDateTime(job.createdAt))}</span>
            </div>
            <div class="panel-actions">
                <button class="button secondary" data-edit-job="${job.id}" type="button">Edit</button>
                <button class="button danger" data-delete-job="${job.id}" type="button">Delete</button>
                <a class="button ghost" href="/applications.html?jobId=${job.id}">Review Applicants</a>
            </div>
        </article>
    `).join("");
}

function renderApplicationsPreview() {
    if (!state.applications.length) {
        applicationPreviewGrid.innerHTML = emptyState("No applications submitted yet.");
        return;
    }

    applicationPreviewGrid.innerHTML = state.applications.slice(0, 6).map((application) => `
        <article class="card management-card application-management-card">
            <div class="card-head">
                <div class="chip-row">
                    <span class="status-pill ${statusTone(application.status)}">${escapeHtml(titleCase(application.status))}</span>
                    <span class="status-pill ${application.meetsEligibility ? "success" : "danger"}">${application.meetsEligibility ? "Eligible" : "Not eligible"}</span>
                    <span class="micro-pill">${escapeHtml(application.jobTitle)}</span>
                </div>
                <h3>${escapeHtml(application.studentName)}</h3>
            </div>
            <div class="management-meta">
                <span><strong>Company</strong>${escapeHtml(application.companyName)}</span>
                <span><strong>Applied</strong>${escapeHtml(formatDateTime(application.createdAt))}</span>
            </div>
        </article>
    `).join("");
}

function renderStatsBar() {
    renderStats(statsGrid, [
        {label: "Companies", value: state.companies.length},
        {label: "Jobs", value: state.jobs.length},
        {label: "Applications", value: state.applications.length},
        {label: "Role", value: state.user.role}
    ]);
}

async function saveCompany(event) {
    event.preventDefault();
    hideMessage(messageElement);

    setButtonBusy(companyButton, true, "Saving...");

    try {
        await apiRequest("/api/companies", {
            method: "POST",
            body: {
                name: document.getElementById("companyName").value.trim(),
                description: document.getElementById("companyDescription").value.trim()
            }
        });

        companyForm.reset();
        showMessage(messageElement, "Company added successfully.", "success");
        await loadDashboard();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    } finally {
        setButtonBusy(companyButton, false);
    }
}

async function saveJob(event) {
    event.preventDefault();
    hideMessage(messageElement);

    if (!document.getElementById("jobCompanyId").value) {
        showMessage(messageElement, "Add a company first before posting a job.", "error");
        return;
    }

    setButtonBusy(jobButton, true, document.getElementById("jobId").value ? "Updating..." : "Posting...");

    const jobId = document.getElementById("jobId").value;
    const path = jobId ? `/api/jobs/${jobId}` : "/api/jobs";
    const method = jobId ? "PUT" : "POST";

    try {
        await apiRequest(path, {
            method,
            body: {
                title: document.getElementById("jobTitle").value.trim(),
                description: document.getElementById("jobDescription").value.trim(),
                companyId: Number(document.getElementById("jobCompanyId").value),
                eligibility: document.getElementById("jobEligibility").value.trim(),
                eligibleBranches: document.getElementById("jobEligibleBranches").value.trim(),
                minimumCgpa: Number(document.getElementById("jobMinimumCgpa").value),
                graduationYear: Number(document.getElementById("jobGraduationYear").value),
                maximumActiveBacklogs: Number(document.getElementById("jobMaximumBacklogs").value)
            }
        });

        resetJobForm();
        showMessage(messageElement, jobId ? "Job updated successfully." : "Job posted successfully.", "success");
        await loadDashboard();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    } finally {
        setButtonBusy(jobButton, false);
    }
}

async function handleJobActions(event) {
    const editButton = event.target.closest("[data-edit-job]");
    const deleteButton = event.target.closest("[data-delete-job]");

    if (editButton) {
        const jobId = Number(editButton.dataset.editJob);
        populateJobForm(jobId);
        return;
    }

    if (!deleteButton) {
        return;
    }

    const jobId = Number(deleteButton.dataset.deleteJob);
    if (!window.confirm("Delete this job posting?")) {
        return;
    }

    try {
        await apiRequest(`/api/jobs/${jobId}`, {method: "DELETE"});
        showMessage(messageElement, "Job deleted successfully.", "success");
        await loadDashboard();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    }
}

function populateJobForm(jobId) {
    const job = state.jobs.find((item) => item.id === jobId);
    if (!job) {
        return;
    }

    document.getElementById("jobId").value = job.id;
    document.getElementById("jobTitle").value = job.title;
    document.getElementById("jobDescription").value = job.description;
    document.getElementById("jobEligibility").value = job.eligibility;
    document.getElementById("jobEligibleBranches").value = job.eligibleBranches || "All branches";
    document.getElementById("jobMinimumCgpa").value = job.minimumCgpa ?? "";
    document.getElementById("jobGraduationYear").value = job.graduationYear ?? "2026";
    document.getElementById("jobMaximumBacklogs").value = job.maximumActiveBacklogs ?? "0";
    document.getElementById("jobCompanyId").value = String(job.companyId);
    jobButton.textContent = "Update Job";
    cancelEditButton.classList.remove("hidden");
    window.scrollTo({top: 0, behavior: "smooth"});
}

function resetJobForm() {
    jobForm.reset();
    document.getElementById("jobId").value = "";
    if (state.companies.length) {
        document.getElementById("jobCompanyId").value = String(state.companies[0].id);
    }
    jobButton.textContent = "Post Job";
    cancelEditButton.classList.add("hidden");
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
