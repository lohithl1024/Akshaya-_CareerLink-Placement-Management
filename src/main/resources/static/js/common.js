import {
    clearSession,
    consumeFlash,
    dashboardPathForRole,
    escapeHtml,
    roleLabel,
    setFlash
} from "./api.js";

let loaderElement;

export function initializeLayout(activePage, user) {
    document.body.dataset.page = activePage;
    renderNavigation(activePage, user);
    renderHeaderActions(user);
    ensurePageLoader();
}

function renderNavigation(activePage, user) {
    const nav = document.getElementById("siteNav");
    if (!nav) {
        return;
    }

    const links = [];

    if (user) {
        links.push({
            key: "dashboard",
            href: dashboardPathForRole(user.role),
            label: user.role === "STUDENT" ? "Placement Dashboard" : `${roleLabel(user.role)} Dashboard`
        });
        links.push({key: "applications", href: "/applications.html", label: "Applications"});
    } else {
        links.push({key: "auth", href: "/index.html", label: "Login"});
    }

    nav.innerHTML = links.map((link) => `
        <a class="nav-link ${link.key === activePage ? "active" : ""}" href="${link.href}">
            ${escapeHtml(link.label)}
        </a>
    `).join("");
}

function renderHeaderActions(user) {
    const actions = document.getElementById("headerActions");
    if (!actions) {
        return;
    }

    if (!user) {
        actions.innerHTML = '<a class="button secondary" href="/index.html">Sign In</a>';
        return;
    }

    actions.innerHTML = `
        <span class="user-chip">${escapeHtml(user.name)} | ${escapeHtml(roleLabel(user.role))}</span>
        <button class="button ghost" id="logoutButton" type="button">Logout</button>
    `;

    const logoutButton = document.getElementById("logoutButton");
    if (logoutButton) {
        logoutButton.addEventListener("click", () => {
            clearSession();
            setFlash("success", "Logged out successfully.");
            window.location.replace("/index.html");
        });
    }
}

export function showMessage(element, message, type = "info") {
    if (!element) {
        return;
    }

    element.textContent = message;
    element.className = `alert ${type}`;
    element.classList.remove("hidden");
}

export function hideMessage(element) {
    if (!element) {
        return;
    }

    element.className = "alert hidden";
    element.textContent = "";
}

export function consumeFlashInto(element) {
    const flash = consumeFlash();
    if (flash) {
        showMessage(element, flash.message, flash.type);
    }
}

export function renderStats(container, items) {
    if (!container) {
        return;
    }

    container.innerHTML = items.map((item) => `
        <article class="stat-card">
            <span class="stat-label">${escapeHtml(item.label)}</span>
            <strong>${escapeHtml(String(item.value))}</strong>
        </article>
    `).join("");
}

export function emptyState(message) {
    return `<div class="empty-state">${escapeHtml(message)}</div>`;
}

export function setButtonBusy(button, busy, busyLabel = "Please wait...") {
    if (!button) {
        return;
    }

    if (busy) {
        button.dataset.originalLabel = button.textContent;
        button.textContent = busyLabel;
        button.disabled = true;
        return;
    }

    button.disabled = false;
    button.textContent = button.dataset.originalLabel || button.textContent;
}

export function setPageBusy(busy, label = "Loading workspace...") {
    ensurePageLoader();

    if (!loaderElement) {
        return;
    }

    loaderElement.querySelector(".page-loader-copy").textContent = label;
    loaderElement.classList.toggle("visible", busy);
}

export function renderLoadingCards(container, count = 3) {
    if (!container) {
        return;
    }

    container.innerHTML = `
        <div class="loading-grid">
            ${Array.from({length: count}, () => `
                <article class="loading-card">
                    <span class="loading-chip"></span>
                    <span class="loading-line medium"></span>
                    <span class="loading-line long"></span>
                    <span class="loading-line long"></span>
                    <span class="loading-line short"></span>
                </article>
            `).join("")}
        </div>
    `;
}

export function renderLoadingStats(container, count = 4) {
    if (!container) {
        return;
    }

    container.innerHTML = `
        <div class="loading-stats">
            ${Array.from({length: count}, () => `
                <article class="loading-stat">
                    <span class="loading-chip"></span>
                    <span class="loading-line medium"></span>
                    <span class="loading-line short"></span>
                </article>
            `).join("")}
        </div>
    `;
}

export function renderLoadingSummary(container, count = 3) {
    if (!container) {
        return;
    }

    container.innerHTML = `
        <div class="loading-summary">
            ${Array.from({length: count}, () => `
                <article class="loading-summary-item">
                    <span class="loading-line medium"></span>
                    <span class="loading-line long"></span>
                </article>
            `).join("")}
        </div>
    `;
}

function ensurePageLoader() {
    if (loaderElement || !document.body) {
        return;
    }

    loaderElement = document.createElement("div");
    loaderElement.className = "page-loader";
    loaderElement.setAttribute("aria-live", "polite");
    loaderElement.innerHTML = `
        <span class="page-loader-spinner" aria-hidden="true"></span>
        <span class="page-loader-copy">Loading workspace...</span>
    `;
    document.body.appendChild(loaderElement);
}
