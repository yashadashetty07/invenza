import { useContext } from "react";
import { Navigate, Outlet } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { jwtDecode } from "jwt-decode";

const isTokenExpired = (token) => {
  try {
    const { exp } = jwtDecode(token);
    return Date.now() >= exp * 1000;
  } catch {
    return true;
  }
};

const ProtectedRoute = ({ requiredRole, children }) => {
  const { user, token, loading } = useContext(AuthContext);


  if (loading) return null; // or a loader


  if (!user || !token || isTokenExpired(token)) {
    return <Navigate to="/" replace />;
  }

  if (requiredRole) {
    const required = Array.isArray(requiredRole) ? requiredRole : [requiredRole];
    const userRoles = Array.isArray(user.role)
      ? user.role.map((r) => r.toLowerCase())
      : [String(user.role).toLowerCase()];

    const allowed = required
      .map((r) => String(r).toLowerCase())
      .some((req) => userRoles.includes(req));

    if (!allowed) return <Navigate to="/unauthorized" replace />;
  }


  return children ? children : <Outlet />;
};

export default ProtectedRoute;
