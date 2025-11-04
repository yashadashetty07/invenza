import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import VendorService from "../../services/VendorService";
import PurchaseOrderService from "../../services/PurchaseOrderService";
import { toast } from "react-toastify";
import Loader from "../../components/Loader";

const PurchaseOrderList = () => {
    const [orders, setOrders] = useState([]);
    const [originalOrders, setOriginalOrders] = useState([]);
    const [vendors, setVendors] = useState([]);
    const [selectedVendor, setSelectedVendor] = useState("");
    const [statusFilter, setStatusFilter] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        setLoading(true);
        try {
            const [ordersData, vendorsData] = await Promise.all([
                PurchaseOrderService.getAllPurchaseOrders(),
                VendorService.getAllVendors(),
            ]);

            const normalizedOrders = normalize(ordersData);
            setOrders(normalizedOrders);
            setOriginalOrders(normalizedOrders);
            setVendors(normalize(vendorsData));
        } catch (error) {
            console.error("Error loading data:", error);
            toast.error("⚠️ Failed to load purchase orders or vendors.");
        } finally {
            setLoading(false);
        }
    };

    const normalize = (response) => {
        if (!response) return [];
        if (Array.isArray(response)) return response;
        if (Array.isArray(response.data)) return response.data;
        if (Array.isArray(response.content)) return response.content;
        return [];
    };

    const handleFilter = () => {
        let filtered = [...originalOrders];

        if (selectedVendor) {
            const vendorId = parseInt(selectedVendor);
            filtered = filtered.filter(
                (o) => o.vendor?.id === vendorId || o.vendorId === vendorId
            );
        }

        if (statusFilter) {
            filtered = filtered.filter(
                (o) => o.status?.toLowerCase() === statusFilter.toLowerCase()
            );
        }

        setOrders(filtered);
    };

    const resetFilters = () => {
        setSelectedVendor("");
        setStatusFilter("");
        setOrders(originalOrders);
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this order?")) return;
        setLoading(true);
        try {
            await PurchaseOrderService.deletePurchaseOrder(id);
            toast.success("🗑️ Purchase order deleted successfully!");
            loadData();
        } catch (error) {
            console.error("Error deleting order:", error);
            toast.error("❌ Failed to delete purchase order.");
        } finally {
            setLoading(false);
        }
    };

    const getVendorName = (order) => {
        const vendor = vendors.find((v) => v.id === order.vendorId);
        return vendor ? vendor.vendorName || vendor.name : "N/A";
    };

    const getOrderDate = (order) => {
        const date = order.orderDate || order.createdAt || order.updatedAt;
        return date ? new Date(date).toLocaleDateString() : "—";
    };

    return (
        <Layout>
            <div className="container mt-4 position-relative">
                {loading && (
                    <div className="position-absolute top-0 start-0 w-100 h-100 bg-white bg-opacity-75 d-flex justify-content-center align-items-center" style={{ zIndex: 10 }}>
                        <Loader />
                    </div>
                )}

                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h3 className="fw-bold text-dark mb-0">📦 Purchase Orders</h3>
                    <button
                        onClick={() => navigate("/purchase-orders/add")}
                        className="btn btn-success fw-semibold shadow-sm"
                    >
                        ➕ Create New
                    </button>
                </div>

                <div className="card shadow-sm border-0 mb-4">
                    <div className="card-body">
                        <div className="row g-3">
                            <div className="col-md-4">
                                <select
                                    className="form-select"
                                    value={selectedVendor}
                                    onChange={(e) => setSelectedVendor(e.target.value)}
                                >
                                    <option value="">All Vendors</option>
                                    {vendors.map((v) => (
                                        <option key={v.id} value={v.id}>
                                            {v.vendorName || v.name}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div className="col-md-4">
                                <select
                                    className="form-select"
                                    value={statusFilter}
                                    onChange={(e) => setStatusFilter(e.target.value)}
                                >
                                    <option value="">All Statuses</option>
                                    <option value="Pending">Pending</option>
                                    <option value="Approved">Approved</option>
                                    <option value="Received">Received</option>
                                </select>
                            </div>
                            <div className="col-md-2 d-grid">
                                <button className="btn btn-primary fw-semibold" onClick={handleFilter}>
                                    Filter
                                </button>
                            </div>
                            <div className="col-md-2 d-grid">
                                <button className="btn btn-outline-secondary fw-semibold" onClick={resetFilters}>
                                    Reset
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="table-responsive shadow-sm">
                    <table className="table table-hover align-middle mb-0">
                        <thead className="table-dark">
                            <tr>
                                <th scope="col">#</th>
                                <th scope="col">Vendor</th>
                                <th scope="col">Total Amount</th>
                                <th scope="col">Status</th>
                                <th scope="col">Date</th>
                                <th scope="col" className="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {orders.length > 0 ? (
                                orders.map((order, index) => (
                                    <tr key={order.id}>
                                        <td>{index + 1}</td>
                                        <td>{getVendorName(order)}</td>
                                        <td>₹{order.totalAmount?.toFixed(2) || 0}</td>
                                        <td>
                                            <span
                                                className={`badge px-3 py-2 text-uppercase ${order.status === "Approved"
                                                    ? "bg-success"
                                                    : order.status === "Pending"
                                                        ? "bg-warning text-dark"
                                                        : order.status === "Received"
                                                            ? "bg-info text-dark"
                                                            : "bg-secondary"
                                                    }`}
                                            >
                                                {order.status || "—"}
                                            </span>
                                        </td>
                                        <td>{getOrderDate(order)}</td>
                                        <td className="text-center">
                                            <Link
                                                to={`/purchase-orders/edit/${order.id}`}
                                                className="btn btn-sm btn-outline-primary me-2"
                                            >
                                                ✏️ Edit
                                            </Link>
                                            <button
                                                onClick={() => handleDelete(order.id)}
                                                className="btn btn-sm btn-outline-danger"
                                            >
                                                🗑️ Delete
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="6" className="text-center py-4">
                                        No Purchase Orders Found
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </Layout>
    );
};

export default PurchaseOrderList;
