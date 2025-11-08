import React, { useState } from "react";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import Loader from "../../components/Loader";
import UserService from "../../services/AuthService";

const ChangePassword = () => {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChangePassword = async (e) => {
        e.preventDefault();
        if (newPassword !== confirmPassword) {
            toast.error("New password and confirm password do not match");
            return;
        }

        setLoading(true);
        try {
            const response = await UserService.changePassword({
                oldPassword,
                newPassword,
            });

            toast.success(response.data?.message || "Password changed successfully!");

            setTimeout(() => {
                const userRole = localStorage.getItem("role");
                if (userRole === "ADMIN") navigate("/admin/dashboard");
                else if (userRole === "CASHIER") navigate("/cashier/dashboard");
                else navigate("/login");
            }, 1500);
        } catch (error) {
            const errMsg =
                error.response?.data?.message || "Error changing password";
            toast.error(errMsg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container mt-4">
            {loading && <Loader />}
            <div className="card shadow p-4 mx-auto" style={{ maxWidth: "400px" }}>
                <h4 className="text-center mb-3">Change Password</h4>
                <form onSubmit={handleChangePassword}>
                    <div className="form-group mb-3">
                        <label>Old Password</label>
                        <input
                            type="password"
                            className="form-control"
                            value={oldPassword}
                            onChange={(e) => setOldPassword(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group mb-3">
                        <label>New Password</label>
                        <input
                            type="password"
                            className="form-control"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group mb-3">
                        <label>Confirm Password</label>
                        <input
                            type="password"
                            className="form-control"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button className="btn btn-primary w-100" disabled={loading}>
                        {loading ? "Changing..." : "Change Password"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ChangePassword;
