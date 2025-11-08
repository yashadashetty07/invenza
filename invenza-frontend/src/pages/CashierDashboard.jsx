import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import Loader from "../components/Loader";
import ProductService from "../services/ProductService";
import BillService from "../services/BillService";
import QuotationService from "../services/QuotationService";
import { useNavigate } from "react-router-dom";

const CashierDashboard = () => {
  const [stats, setStats] = useState({
    products: 0,
    bills: 0,
    quotations: 0,
  });
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setLoading(true);

        const [productsRes, billsRes, quotationsRes] = await Promise.all([
          ProductService.getAllProducts(),
          BillService.getAllBills(),
          QuotationService.getAllQuotations(),
        ]);

        const products = Array.isArray(productsRes?.data)
          ? productsRes.data
          : productsRes;
        const bills = Array.isArray(billsRes?.data)
          ? billsRes.data
          : billsRes;
        const quotations = Array.isArray(quotationsRes?.data)
          ? quotationsRes.data
          : quotationsRes;

        setStats({
          products: products.length,
          bills: bills.length,
          quotations: quotations.length,
        });
      } catch (error) {
        console.error("Error fetching cashier dashboard stats:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  if (loading) {
    return (
      <Layout>
        <div
          className="d-flex justify-content-center align-items-center"
          style={{ height: "80vh" }}
        >
          <Loader />
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="container-fluid mt-4">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h2 className="text-dark fw-bold">💼 Cashier Dashboard</h2>
          <span className="badge bg-secondary px-3 py-2 shadow-sm">
            Last Updated: {new Date().toLocaleString()}
          </span>
        </div>

        {/* ===== Dashboard Cards ===== */}
        <div className="row g-4">
          <div className="col-md-4 col-sm-6">
            <div className="card text-white bg-primary shadow-lg border-0 h-100">
              <div className="card-body text-center">
                <h6 className="card-title text-uppercase">Products</h6>
                <p className="fs-1 fw-bold mb-0">{stats.products}</p>
              </div>
            </div>
          </div>

          <div className="col-md-4 col-sm-6">
            <div className="card text-white bg-success shadow-lg border-0 h-100">
              <div className="card-body text-center">
                <h6 className="card-title text-uppercase">Bills</h6>
                <p className="fs-1 fw-bold mb-0">{stats.bills}</p>
              </div>
            </div>
          </div>

          <div className="col-md-4 col-sm-6">
            <div className="card text-white bg-warning shadow-lg border-0 h-100">
              <div className="card-body text-center">
                <h6 className="card-title text-uppercase">Quotations</h6>
                <p className="fs-1 fw-bold mb-0">{stats.quotations}</p>
              </div>
            </div>
          </div>
        </div>

        {/* ===== Quick Actions ===== */}
        <div className="card shadow-sm border-0 mt-4">
          <div className="card-header bg-white border-0 d-flex align-items-center justify-content-between">
            <h5 className="mb-0 fw-semibold text-primary">Quick Actions</h5>
          </div>

          <div className="card-body">
            <div className="row g-3">
              <div className="col-6 col-md-4 col-lg-3">
                <button
                  onClick={() => navigate("/bills/new")}
                  className="btn btn-outline-success w-100 py-3 rounded-3 shadow-sm fw-medium"
                >
                  <i className="bi bi-receipt me-2"></i> Create Bill
                </button>
              </div>

              <div className="col-6 col-md-4 col-lg-3">
                <button
                  onClick={() => navigate("/bills")}
                  className="btn btn-outline-primary w-100 py-3 rounded-3 shadow-sm fw-medium"
                >
                  <i className="bi bi-list-ul me-2"></i> View Bills
                </button>
              </div>

              <div className="col-6 col-md-4 col-lg-3">
                <button
                  onClick={() => navigate("/quotations/add")}
                  className="btn btn-outline-warning w-100 py-3 rounded-3 shadow-sm fw-medium"
                >
                  <i className="bi bi-file-earmark-text me-2"></i> Create Quotation
                </button>
              </div>

              <div className="col-6 col-md-4 col-lg-3">
                <button
                  onClick={() => navigate("/quotations")}
                  className="btn btn-outline-info w-100 py-3 rounded-3 shadow-sm fw-medium"
                >
                  <i className="bi bi-journal-text me-2"></i> View Quotations
                </button>
              </div>

              <div className="col-6 col-md-4 col-lg-3">
                <button
                  onClick={() => navigate("/products")}
                  className="btn btn-outline-secondary w-100 py-3 rounded-3 shadow-sm fw-medium"
                >
                  <i className="bi bi-box-seam me-2"></i> View Products
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default CashierDashboard;
