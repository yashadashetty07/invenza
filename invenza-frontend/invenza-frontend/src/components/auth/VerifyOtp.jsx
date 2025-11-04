import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import AuthService from "../../services/AuthService";
import { toast } from "react-toastify";
import Layout from "../Layout";
import Loader from "../../components/Loader";

const VerifyOtp = () => {
    const [otp, setOtp] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const { state } = useLocation();
    const email = state?.email || "";

    const handleVerify = async (e) => {
        e.preventDefault();
        if (!otp.trim()) return toast.error("Please enter OTP");
        setLoading(true);

        try {
            await AuthService.verifyOtp(email, otp);
            toast.success("OTP verified successfully!");
            navigate("/reset-password", { state: { email } });
        } catch (error) {
            toast.error(error.response?.data || "Invalid or expired OTP");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Layout>
            <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
                <div className="card shadow-lg border-0 p-4" style={{ width: "400px", borderRadius: "12px" }}>
                    <h4 className="text-center text-primary fw-bold mb-3">Verify OTP</h4>
                    <p className="text-muted small text-center mb-4">
                        Enter the OTP sent to <strong>{email}</strong>.
                    </p>

                    <form onSubmit={handleVerify}>
                        <div className="mb-3">
                            <label className="form-label fw-semibold">OTP</label>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Enter OTP"
                                value={otp}
                                onChange={(e) => setOtp(e.target.value)}
                                required
                            />
                        </div>

                        <button className="btn btn-success w-100" type="submit" disabled={loading}>
                            {loading ? <Loader /> : "Verify OTP"}
                        </button>
                    </form>
                </div>
            </div>
        </Layout>
    );
};

export default VerifyOtp;
