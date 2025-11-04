import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import AuthService from "../../services/AuthService";
import Layout from "../Layout";
import Loader from "../../components/Loader";

const ForgotPassword = () => {
    const [email, setEmail] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!email.trim()) return toast.error("Please enter your email");
        setLoading(true);

        try {
            await AuthService.forgotPassword(email);
            toast.success("OTP sent to your email!");
            navigate("/verify-otp", { state: { email } });
        } catch (error) {
            toast.error(error.response?.data || "Failed to send OTP");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
            <div className="card shadow-lg border-0 p-4" style={{ width: "400px", borderRadius: "12px" }}>
                <h4 className="text-center text-primary fw-bold mb-3">Forgot Password</h4>
                <p className="text-muted small text-center mb-4">
                    Enter your registered email to receive a one-time password.
                </p>

                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label fw-semibold">Email</label>
                        <input
                            type="email"
                            className="form-control"
                            placeholder="Enter registered email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>

                    <button className="btn btn-primary w-100" type="submit" disabled={loading}>
                        {loading ? <Loader /> : "Send OTP"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ForgotPassword;
