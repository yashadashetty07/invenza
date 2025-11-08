import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import AuthService from "../../services/AuthService";
import { toast } from "react-toastify";
import Layout from "../Layout";
import Loader from "../../components/Loader";

const ResetPassword = () => {
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const { state } = useLocation();
    const email = state?.email || "";
    const navigate = useNavigate();

    const handleReset = async (e) => {
        e.preventDefault();
        if (!newPassword || !confirmPassword) return toast.error("All fields required");
        if (newPassword !== confirmPassword) return toast.error("Passwords do not match");

        setLoading(true);
        try {
            await AuthService.resetPassword(email, newPassword);
            toast.success("Password reset successfully!");
            navigate("/");
        } catch (error) {
            toast.error(error.response?.data || "Failed to reset password");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
            <div className="card shadow-lg border-0 p-4" style={{ width: "400px", borderRadius: "12px" }}>
                <h4 className="text-center text-primary fw-bold mb-3">Reset Password</h4>
                <p className="text-muted small text-center mb-4">
                    Reset password for <strong>{email}</strong>.
                </p>

                <form onSubmit={handleReset}>
                    <div className="mb-3">
                        <label className="form-label fw-semibold">New Password</label>
                        <input
                            type="password"
                            className="form-control"
                            placeholder="Enter new password"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-semibold">Confirm Password</label>
                        <input
                            type="password"
                            className="form-control"
                            placeholder="Re-enter new password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button className="btn btn-primary w-100" type="submit" disabled={loading}>
                        {loading ? <Loader /> : "Reset Password"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ResetPassword;
