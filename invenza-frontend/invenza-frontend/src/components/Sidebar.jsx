import { Link, useLocation } from "react-router-dom";
import { useContext } from "react";
import { AuthContext } from "../context/AuthContext";

const Sidebar = () => {
  const location = useLocation();
  const { user } = useContext(AuthContext);
  const role = user?.role?.toLowerCase();

  const isActive = (path) => location.pathname.startsWith(path);
  const dashboardPath = role === "admin" ? "/admin" : "/cashier";

  return (
    <div
      className="text-white d-flex flex-column justify-content-between py-4 px-3"
      style={{
        minHeight: "100vh",
        boxShadow: "3px 0 10px rgba(0,0,0,0.3)",
        backgroundColor: "#0a1931",
      }}
    >
      <div>
        <ul className="nav flex-column text-center">

          <li className="nav-item mb-3">
            <Link
              to="/change-password"
              className={`btn w-100 text-start px-3 py-2 mb-2 ${isActive("/change-password")
                ? "btn-primary text-white fw-bold"
                : "btn-outline-light text-white-50"
                }`}
            >
              Change Password
            </Link>
          </li>

          <li className="nav-item mb-4">
            <Link
              to={dashboardPath}
              className={`btn w-100 text-start px-3 py-2 mb-2 ${isActive(dashboardPath)
                ? "btn-primary text-white fw-bold"
                : "btn-outline-light text-white-50"
                }`}
            >
              Dashboard
            </Link>
          </li>

          {role === "admin" && (
            <>
              <li className="nav-item mt-4 mb-2">
                <span className="text-uppercase text-secondary small fw-bold">
                  Inventory
                </span>
              </li>

              {[
                { path: "/products", label: "Products" },
                { path: "/vendors", label: "Vendors" },
                { path: "/purchase-orders", label: "Purchase Orders" },
              ].map(({ path, label }) => (
                <li className="nav-item mb-2" key={path}>
                  <Link
                    to={path}
                    className={`btn w-100 text-start px-3 py-2 mb-2 ${isActive(path)
                      ? "btn-primary text-white fw-semibold"
                      : "btn-outline-light text-white-50"
                      }`}
                  >
                    {label}
                  </Link>
                </li>
              ))}

              <li className="nav-item mt-4 mb-2">
                <span className="text-uppercase text-secondary small fw-bold">
                  Bills & QUotations
                </span>
              </li>

              {[
                { path: "/bills", label: "Bills" },
                { path: "/quotations", label: "Quotations" },
              ].map(({ path, label }) => (
                <li className="nav-item mb-2" key={path}>
                  <Link
                    to={path}
                    className={`btn w-100 text-start px-3 py-2 mb-2 ${isActive(path)
                      ? "btn-primary text-white fw-semibold"
                      : "btn-outline-light text-white-50"
                      }`}
                  >
                    {label}
                  </Link>
                </li>
              ))}
            </>
          )}

          {role === "cashier" && (
            <>
              <li className="nav-item mt-4 mb-2">
                <span className="text-uppercase text-secondary small fw-bold">
                  Billing
                </span>
              </li>

              {[
                { path: "/bills", label: "Bills" },
                { path: "/quotations", label: "Quotations" },
              ].map(({ path, label }) => (
                <li className="nav-item mb-2" key={path}>
                  <Link
                    to={path}
                    className={`btn w-100 text-start px-3 py-2 mb-2 ${isActive(path)
                      ? "btn-primary text-white fw-semibold"
                      : "btn-outline-light text-white-50"
                      }`}
                  >
                    {label}
                  </Link>
                </li>
              ))}
            </>
          )}
        </ul>
      </div>

      <div className="text-center mt-auto">
        <hr className="border-secondary" />
        <small className="text-secondary">© 2025 Invenza</small>
      </div>
    </div>
  );
};

export default Sidebar;
