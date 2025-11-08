import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import BillService from "../../services/BillService";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const BillList = () => {
    const [bills, setBills] = useState([]);
    const [loading, setLoading] = useState(true);
    const [deleting, setDeleting] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        fetchBills();
    }, []);

    const fetchBills = async () => {
        setLoading(true);
        try {
            const response = await BillService.getAllBills();
            const data = Array.isArray(response.data) ? response.data : [];
            setBills(data);
        } catch (error) {
            console.error("Error fetching bills:", error);
            toast.error("Failed to load bills.");
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (query) => {
        setSearchQuery(query);
    };

    const filteredBills = bills.filter((bill) => {
        const lowerQuery = searchQuery.toLowerCase();
        return (
            bill.customerName?.toLowerCase().includes(lowerQuery) ||
            bill.billNumber?.toLowerCase().includes(lowerQuery)
        );
    });

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this bill?")) return;
        setDeleting(true);
        try {
            await BillService.deleteBill(id);
            toast.success("Bill deleted successfully!");
            await fetchBills();
        } catch (error) {
            console.error("Error deleting bill:", error);
            toast.error("Failed to delete bill.");
        } finally {
            setDeleting(false);
        }
    };

    const handleGeneratePDF = async (id) => {
        try {
            const response = await BillService.generatePDF(id);
            const blob = new Blob([response.data], { type: "application/pdf" });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = `bill_${id}.pdf`;
            link.click();
            window.URL.revokeObjectURL(url);
            toast.success("Bill PDF generated!");
        } catch (error) {
            console.error("Error generating PDF:", error);
            toast.error("Failed to generate PDF.");
        }
    };

    if (loading) {
        return (
            <Layout>
                <div className="text-center mt-4">
                    <div className="spinner-border text-primary" role="status"></div>
                    <p>Loading bills...</p>
                </div>
            </Layout>
        );
    }

    return (
        <Layout>
            <div className="container mt-4">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h2>Bills</h2>
                    <div className="d-flex align-items-center">
                        <input
                            type="text"
                            className="form-control me-2"
                            placeholder="Search by customer or bill no..."
                            style={{ width: "250px" }}
                            value={searchQuery}
                            onChange={(e) => handleSearch(e.target.value)}
                        />
                        <Link
                            to="/bills/new"
                            className="btn btn-primary"
                            disabled={deleting}
                        >
                            + New Bill
                        </Link>
                    </div>
                </div>

                {filteredBills.length === 0 ? (
                    <div className="alert alert-info text-center">
                        No bills found.
                    </div>
                ) : (
                    <div className="table-responsive">
                        <table className="table table-bordered align-middle">
                            <thead className="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Bill No.</th>
                                    <th>Customer</th>
                                    <th>Date</th>
                                    <th>Final Amount (₹)</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredBills.map((bill, index) => (
                                    <tr key={bill.id}>
                                        <td>{index + 1}</td>
                                        <td>{bill.billNumber}</td>
                                        <td>{bill.customerName || "-"}</td>
                                        <td>
                                            {bill.billDate
                                                ? new Date(bill.billDate).toLocaleDateString()
                                                : "-"}
                                        </td>
                                        <td>
                                            {bill.finalAmount
                                                ? bill.finalAmount.toFixed(2)
                                                : "0.00"}
                                        </td>
                                        <td>
                                            <div className="btn-group">
                                                <button
                                                    className="btn btn-sm btn-info"
                                                    onClick={() => handleGeneratePDF(bill.id)}
                                                    disabled={deleting}
                                                >
                                                    PDF
                                                </button>
                                                <button
                                                    className="btn btn-sm btn-warning mx-1"
                                                    onClick={() =>
                                                        navigate(`/bills/view/${bill.id}`)
                                                    }
                                                    disabled={deleting}
                                                >
                                                    View
                                                </button>
                                                <button
                                                    className="btn btn-sm btn-danger"
                                                    onClick={() => handleDelete(bill.id)}
                                                    disabled={deleting}
                                                >
                                                    {deleting ? "Deleting..." : "Delete"}
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </Layout>
    );
};

export default BillList;
