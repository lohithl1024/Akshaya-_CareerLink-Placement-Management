/*
Development Notes
- JWT session key: localStorage["careerlink.session"]
- Flash message key: sessionStorage["careerlink.flash"]
- Pending job key: sessionStorage["careerlink.pendingJob"]
- API convention: same-origin requests under /api/*
*/

const SESSION_KEY = "careerlink.session";
const FLASH_KEY = "careerlink.flash";
const PENDING_JOB_KEY = "careerlink.pendingJob";

function readStorage(storage, key) {
    const raw = storage.getItem(key);
    if (!raw) {
        return null;
    }

    try {
        return JSON.parse(raw);
    } catch (error) {
        storage.removeItem(key);
        return null;
    }
}

export function getSession() {
    return readStorage(localStorage, SESSION_KEY) || {};
}

export function getToken() {
    return getSession().token || null;
}

export function getStoredUser() {
    return getSession().user || null;
}

export function saveSession(authResponse) {
    localStorage.setItem(SESSION_KEY, JSON.stringify({
        token: authResponse.token,
        tokenType: authResponse.tokenType || "Bearer",
        user: authResponse.user || null
    }));
}

export function updateStoredUser(user) {
    const session = getSession();
    localStorage.setItem(SESSION_KEY, JSON.stringify({
        token: session.token || null,
        tokenType: session.tokenType || "Bearer",
        user
    }));
}

export function clearSession() {
    localStorage.removeItem(SESSION_KEY);
}

export function setFlash(type, message) {
    sessionStorage.setItem(FLASH_KEY, JSON.stringify({type, message}));
}

export function consumeFlash() {
    const flash = readStorage(sessionStorage, FLASH_KEY);
    sessionStorage.removeItem(FLASH_KEY);
    return flash;
}

export function setPendingJob(jobId) {
    sessionStorage.setItem(PENDING_JOB_KEY, String(jobId));
}

export function getPendingJob() {
    const value = sessionStorage.getItem(PENDING_JOB_KEY);
    return value ? Number(value) : null;
}

export function clearPendingJob() {
    sessionStorage.removeItem(PENDING_JOB_KEY);
}

export function dashboardPathForRole(role) {
    return role === "STUDENT" ? "/placements.html" : "/recruiter-dashboard.html";
}

export function roleLabel(role) {
    if (role === "ADMIN") {
        return "Admin";
    }
    if (role === "RECRUITER") {
        return "Recruiter";
    }
    return "Student";
}

export function buildQuery(params) {
    const query = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
        if (typeof value === "boolean") {
            query.set(key, String(value));
            return;
        }

        if (value !== null && value !== undefined && String(value).trim() !== "") {
            query.set(key, String(value).trim());
        }
    });

    const queryString = query.toString();
    return queryString ? `?${queryString}` : "";
}

export async function apiRequest(path, options = {}) {
    const headers = {
        Accept: "application/json",
        ...(options.headers || {})
    };
    const token = getToken();
    const hasBody = options.body !== undefined && options.body !== null;

    if (token && !options.skipAuth) {
        headers.Authorization = `Bearer ${token}`;
    }

    if (hasBody) {
        headers["Content-Type"] = "application/json";
    }

    const response = await fetch(path, {
        method: options.method || "GET",
        headers,
        body: hasBody ? JSON.stringify(options.body) : undefined
    });

    const payload = await response.json().catch(() => ({}));

    if (!response.ok) {
        if (response.status === 401) {
            clearSession();
        }

        const error = new Error(extractErrorMessage(payload));
        error.status = response.status;
        error.payload = payload;
        throw error;
    }

    return payload;
}

export function extractErrorMessage(payload) {
    if (payload.validationErrors) {
        return Object.values(payload.validationErrors).join(" ");
    }
    return payload.message || "Request failed.";
}

export async function resolveCurrentUser() {
    if (!getToken()) {
        return null;
    }

    const payload = await apiRequest("/api/auth/me");
    updateStoredUser(payload.data);
    return payload.data;
}

export async function requireAuth(allowedRoles = []) {
    try {
        const user = await resolveCurrentUser();

        if (!user) {
            setFlash("error", "Please login to continue.");
            window.location.replace("/index.html");
            return null;
        }

        if (allowedRoles.length && !allowedRoles.includes(user.role)) {
            setFlash("error", "That page is not available for your role.");
            window.location.replace(dashboardPathForRole(user.role));
            return null;
        }

        return user;
    } catch (error) {
        if (error.status === 401 || error.status === 403) {
            setFlash("error", "Your session expired. Please login again.");
            window.location.replace("/index.html");
            return null;
        }
        throw error;
    }
}

export function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

export function formatDate(value) {
    if (!value) {
        return "N/A";
    }
    return new Date(`${value}T00:00:00`).toLocaleDateString([], {
        day: "2-digit",
        month: "short",
        year: "numeric"
    });
}

export function formatDateTime(value) {
    if (!value) {
        return "N/A";
    }
    return new Date(value).toLocaleString([], {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}

export function titleCase(value) {
    return String(value || "")
        .toLowerCase()
        .split("_")
        .filter(Boolean)
        .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
        .join(" ");
}

export function truncate(value, limit = 180) {
    const text = String(value || "");
    if (text.length <= limit) {
        return text;
    }
    return `${text.slice(0, limit).trim()}...`;
}
