import { Link } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { useContext } from "react";

const Navbar = () => {
  const { logout } = useContext(AuthContext);

  return (
    <nav
      className="navbar navbar-dark bg-dark px-4 py-3 d-flex justify-content-between align-items-center"
      style={{
        boxShadow: "0 2px 6px rgba(0,0,0,0.2)",
        borderBottom: "1px solid rgba(255,255,255,0.1)",
      }}
    >
      <Link className="navbar-brand fw-semibold fs-5" to="/" style={{ letterSpacing: "0.5px" }}>
        🧾 Invenza
      </Link>
      <button
        onClick={logout}
        className="btn btn-outline-light btn-sm px-3 py-1"
        style={{ borderRadius: "4px" }}
      >
        Logout
      </button>
    </nav>
  );
};

export default Navbar;