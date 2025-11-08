import api from "../api/axios";
const API_URL = "/auth"; // base route handled by api.js

// ✅ Login user and store token + role
export const loginUser = async (credentials) => {
  const response = await api.post(`${API_URL}/login`, credentials);
  localStorage.setItem("token", response.data.token);
  localStorage.setItem("role", response.data.role);
  return response.data;
};

// ✅ Forgot Password
const forgotPassword = (email) => api.post(`${API_URL}/forgot`, { email });

// ✅ Reset Password
const resetPassword = (email, newPassword) =>
  api.post(`${API_URL}/reset`, { email, newPassword });

// ✅ Change Password (token handled by interceptor)
const changePassword = (data) => api.post(`${API_URL}/change-password`, data);

const AuthService = {
  loginUser,
  forgotPassword,
  resetPassword,
  changePassword,
};

export default AuthService;
