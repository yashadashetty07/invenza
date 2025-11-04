import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import Loader from "../components/Loader";
import ProductService from "../services/ProductService";
import VendorService from "../services/VendorService";
import PurchaseOrderService from "../services/PurchaseOrderService";
import BillService from "../services/BillService";
import { useNavigate } from "react-router-dom";

const AdminDashboard = () => {
  const [stats, setStats] = useState({
    products: 0,
    vendors: 0,
    purchaseOrders: 0,
    bills: 0,
  });
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  useEffect(() => {
    const fetchStats = async () => {
      try {
        setLoading(true);

        const [
          productsRes,
          vendorsRes,
          purchaseOrdersRes,
          billsRes,
        ] = await Promise.all([
          ProductService.getAllProducts(),
          VendorService.getAllVendors(),
          PurchaseOrderService.getAllPurchaseOrders(),
          BillService.getAllBills(),
        ]);

        const products =
          Array.isArray(productsRes?.data) ? productsRes.data : productsRes;
        const vendors = Array.isArray(vendorsRes)
          ? vendorsRes
          : vendorsRes?.data || [];
        const purchaseOrders = Array.isArray(purchaseOrdersRes?.data)
          ? purchaseOrdersRes.data
          : purchaseOrdersRes;
        const bills = Array.isArray(billsRes?.data)
          ? billsRes.data
          : billsRes;

        setStats({
          products: products.length,
          vendors: vendors.length,
          purchaseOrders: purchaseOrders.length,
          bills: bills.length,
        });
      } catch (error) {
        console.error("Error fetching dashboard stats:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  if (loading) {
    return (
      <Layout>
        <div className="d-flex justify-content-center align-items-center" style={{ height: "80vh" }}>
          <Loader />
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="container-fluid mt-4">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h2 className="text-dark fw-bold">📊 Admin Dashboard</h2>
          <span className="badge bg-secondary px-3 py-2 shadow-sm">
            Last Updated: {new Date().toLocaleString()}
          </span>
        </div>

        <div className="row g-4">
          <div className="col-md-3 col-sm-6">
            <div className="card text-white bg-primary shadow-lg border-0 h-100">
              <div className="card-body text-center">
                <h6 className="card-title text-uppercase">Products</h6>
                <p className="fs-1 fw-bold mb-0">{stats.products}</p>
              </div>
            </div>
          </div>

          <div className="col-md-3 col-sm-6">
            <div className="card text-white bg-success shadow-lg border-0 h-100">
              <div className="card-body text-center">
                <h6 className="card-title text-uppercase">Vendors</h6>
                <p className="fs-1 fw-bold mb-0">{stats.vendors}</p>
              </div>
            </div>
          </div>

          <div className="col-md-3 col-sm-6">
            <div className="card text-white bg-warning shadow-lg border-0 h-100">
              <div className="card-body text-center">
                <h6 className="card-title text-uppercase">Purchase Orders</h6>
                <p className="fs-1 fw-bold mb-0">{stats.purchaseOrders}</p>
              </div>
            </div>
          </div>

          <div className="col-md-3 col-sm-6">
            <div className="card text-white bg-danger shadow-lg border-0 h-100">
              <div className="card-body text-center">
                <h6 className="card-title text-uppercase">Bills</h6>
                <p className="fs-1 fw-bold mb-0">{stats.bills}</p>
              </div>
            </div>
          </div>
        </div>

        <div className="card shadow-sm border-0 mt-4">
          <div className="card-header bg-white border-0 d-flex align-items-center justify-content-between">
            <h5 className="mb-0 fw-semibold text-primary">Quick Actions</h5>
          </div>

          <div className="card-body">
            <div className="row g-3">
              <div className="col-6 col-md-4 col-lg-3">
                <button
                  onClick={() => navigate("/products/add")}
                  className="btn btn-outline-primary w-100 py-3 rounded-3 shadow-sm fw-medium action-btn"
                >
                  <i className="bi bi-box-seam me-2"></i> Add Product
                </button>
              </div>
              <div className="col-6 col-md-4 col-lg-3">
                <button
                  onClick={() => navigate("/vendors/add")}
                  className="btn btn-outline-info w-100 py-3 rounded-3 shadow-sm fw-medium action-btn"
                >
                  <i className="bi bi-person-plus me-2"></i> Add Vendor
                </button>
              </div>
              <div className="col-6 col-md-4 col-lg-3">
                <button
                  onClick={() => navigate("/bills/new")}
                  className="btn btn-outline-success w-100 py-3 rounded-3 shadow-sm fw-medium action-btn"
                >
                  <i className="bi bi-receipt me-2"></i> Create Bill
                </button>
              </div>
              <div className="col-6 col-md-4 col-lg-3">
                <button
                  onClick={() => navigate("/quotations/add")}
                  className="btn btn-outline-danger w-100 py-3 rounded-3 shadow-sm fw-medium action-btn"
                >
                  <i className="bi bi-file-earmark-text me-2"></i> Create Quotation
                </button>
              </div>
            </div>
          </div>
        </div>


      </div>
    </Layout>
  );
};

export default AdminDashboard;
