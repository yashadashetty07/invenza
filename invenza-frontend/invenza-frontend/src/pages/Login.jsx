import { useContext, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import AuthService from "../services/AuthService";
import Loader from "../components/Loader"; // must exist

const Login = () => {
    const navigate = useNavigate();
    const { login } = useContext(AuthContext);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            // 🧹 Always clear old credentials before new login
            localStorage.removeItem("user");
            localStorage.removeItem("token");

            const response = await AuthService.loginUser({ username, password });

            if (!response || !response.token) {
                setError("Invalid username or password");
                return;
            }

            const { token, role } = response;
            const normalizedRole = role.toLowerCase();

            // ✅ Save new credentials in context + localStorage
            login({ username, role: normalizedRole }, token);

            // ✅ Redirect based on role
            setTimeout(() => {
                if (normalizedRole === "admin") navigate("/admin");
                else if (normalizedRole === "cashier") navigate("/cashier");
                else navigate("/unauthorized");
            }, 100);

        } catch (err) {
            console.error("Login error:", err);
            setError("Login failed. Check credentials or server.");
        } finally {
            setLoading(false);
        }
    };



    return (
        <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
            <div className="card shadow-sm p-4" style={{ width: "400px" }}>
                <h3 className="text-center mb-4 text-primary">Login to Invenza</h3>

                {error && (
                    <div className="alert alert-danger py-2 text-center">{error}</div>
                )}

                {loading ? (
                    <div className="d-flex justify-content-center py-4">
                        <Loader />
                    </div>
                ) : (
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label className="form-label">Username</label>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Enter username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Password</label>
                            <input
                                type="password"
                                className="form-control"
                                placeholder="Enter password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary w-100 mb-3"
                            disabled={loading}
                        >
                            {loading ? "Logging in..." : "Login"}
                        </button>

                        <div className="text-center">
                            <Link
                                to="/forgot-password"
                                className="text-decoration-none text-secondary"
                            >
                                Forgot Password?
                            </Link>
                        </div>
                    </form>
                )}
            </div>
        </div>
    );
};

export default Login;
