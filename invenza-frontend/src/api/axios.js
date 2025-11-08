import axios from "axios";

const BASE_URL = "https://invenza-production.up.railway.app/api";

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            console.warn("⚠️ Session expired — redirecting to login...");
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            window.location.href = "/login";
        } else if (error.response?.status === 403) {
            console.warn("🚫 Access denied — insufficient permissions.");
        } else if (!error.response) {
            console.error("❌ Network error — unable to connect to the server.");
        }
        return Promise.reject(error);
    }
);

export default api;
