import {
    apiRequest,
    buildQuery,
    clearPendingJob,
    dashboardPathForRole,
    escapeHtml,
    formatDate,
    resolveCurrentUser,
    setFlash,
    setPendingJob,
    truncate
} from "./api.js";
import {
    consumeFlashInto,
    emptyState,
    hideMessage,
    initializeLayout,
    renderLoadingCards,
    setPageBusy,
    showMessage
} from "./common.js";

const state = {
    user: null,
    companies: [],
    jobs: []
};

const messageElement = document.getElementById("pageMessage");
const jobsMeta = document.getElementById("jobsMeta");
const jobsGrid = document.getElementById("jobsGrid");
const filterForm = document.getElementById("filterForm");
const filterCompanyId = document.getElementById("filterCompanyId");
const resetFiltersButton = document.getElementById("resetFiltersButton");

document.addEventListener("DOMContentLoaded", init);

async function init() {
    setPageBusy(true, "Loading job board...");
    try {
        state.user = await resolveCurrentUser();
    } catch (error) {
        state.user = null;
    }

    initializeLayout("jobs", state.user);
    consumeFlashInto(messageElement);
    clearPendingJob();
    bindEvents();
    jobsMeta.textContent = "Loading roles...";
    renderLoadingCards(jobsGrid, 6);
    await Promise.all([loadCompanies(), loadJobs()]);
    setPageBusy(false);
}

function bindEvents() {
    filterForm.addEventListener("submit", handleFilterSubmit);
    resetFiltersButton.addEventListener("click", resetFilters);
    jobsGrid.addEventListener("click", handleJobActions);
}

async function loadCompanies() {
    try {
        const payload = await apiRequest("/api/companies", {skipAuth: true});
        state.companies = payload.data || [];
        renderCompanyOptions();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    }
}

async function loadJobs() {
    try {
        const params = {
            title: document.getElementById("filterTitle").value.trim(),
            companyId: filterCompanyId.value,
            eligibility: document.getElementById("filterEligibility").value.trim(),
            active: document.getElementById("filterActive").value
        };

        const payload = await apiRequest(`/api/jobs${buildQuery(params)}`, {skipAuth: true});
        state.jobs = payload.data || [];
        renderJobs();
    } catch (error) {
        showMessage(messageElement, error.message, "error");
    }
}

function renderCompanyOptions() {
    filterCompanyId.innerHTML = `
        <option value="">All companies</option>
        ${state.companies.map((company) => `
            <option value="${company.id}">${escapeHtml(company.name)}</option>
        `).join("")}
    `;
}

function renderJobs() {
    jobsMeta.textContent = `${state.jobs.length} role${state.jobs.length === 1 ? "" : "s"} found`;

    if (!state.jobs.length) {
        jobsGrid.innerHTML = emptyState("No jobs matched the selected filters.");
        return;
    }

    jobsGrid.innerHTML = state.jobs.map((job) => `
        <article class="card">
            <div class="card-head">
                <div class="chip-row">
                    <span class="status-pill ${job.active ? "success" : "warning"}">
                        ${job.active ? "Active" : "Inactive"}
                    </span>
                    <span class="micro-pill">${escapeHtml(job.companyName)}</span>
                </div>
                <h3>${escapeHtml(job.title)}</h3>
                <p class="card-summary">${escapeHtml(truncate(job.description, 150))}</p>
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
                    <span>Package</span>
                    <strong>${escapeHtml(job.salaryPackage || "Confidential")}</strong>
                </div>
            </div>
            <div class="panel-actions">
                ${renderActionButton(job)}
            </div>
        </article>
    `).join("");
}

function renderActionButton(job) {
    if (!job.active) {
        return '<button class="button ghost" type="button" disabled>Closed</button>';
    }

    if (!state.user) {
        return '<a class="button primary" href="/index.html">Login to Apply</a>';
    }

    if (state.user.role === "STUDENT") {
        return `<button class="button primary" data-apply-job="${job.id}" type="button">Apply for Job</button>`;
    }

    return `<a class="button secondary" href="${dashboardPathForRole(state.user.role)}">Open Dashboard</a>`;
}

async function handleFilterSubmit(event) {
    event.preventDefault();
    hideMessage(messageElement);
    await loadJobs();
}

async function resetFilters() {
    filterForm.reset();
    document.getElementById("filterActive").value = "true";
    hideMessage(messageElement);
    await loadJobs();
}

function handleJobActions(event) {
    const button = event.target.closest("[data-apply-job]");
    if (!button) {
        return;
    }

    if (!state.user) {
        setFlash("error", "Please login as a student to apply.");
        window.location.href = "/index.html";
        return;
    }

    if (state.user.role !== "STUDENT") {
        setFlash("error", "Only students can apply for jobs.");
        window.location.href = dashboardPathForRole(state.user.role);
        return;
    }

    const jobId = Number(button.dataset.applyJob);
    setPendingJob(jobId);
    window.location.href = `/applications.html?jobId=${jobId}`;
}
