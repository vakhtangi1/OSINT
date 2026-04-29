const API_BASE = "http://localhost:8080/api";

export function getToken() {
    return localStorage.getItem("token");
}

export function saveAuth(data) {
    localStorage.setItem("token", data.token);
    localStorage.setItem("username", data.username);
    localStorage.setItem("role", data.role);
}

export function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
}

export async function apiFetch(path, options = {}) {
    const token = getToken();

    const res = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(options.headers || {}),
        },
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "Request failed");
    }

    const contentType = res.headers.get("content-type");

    if (contentType && contentType.includes("application/json")) {
        return res.json();
    }

    return res.text();
}

export async function loginUser(payload) {
    return apiFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}

export async function registerUser(payload) {
    return apiFetch("/auth/register", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}

export async function verifyMfa(payload) {
    return apiFetch("/auth/mfa/verify", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}