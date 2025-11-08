import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import QuotationService from "../../services/QuotationService";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Loader from "../../components/Loader";

const QuotationList = () => {
    const [quotations, setQuotations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [deleting, setDeleting] = useState(false);
    const [refreshing, setRefreshing] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        fetchQuotations();
    }, []);

    const fetchQuotations = async () => {
        setLoading(true);
        try {
            const res = await QuotationService.getAllQuotations();
            const data = Array.isArray(res.data)
                ? res.data
                : res.data.content || [];
            setQuotations(data);
        } catch (error) {
            console.error("Error fetching quotations:", error);
            toast.error("Failed to load quotations");
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this quotation?"))
            return;

        setDeleting(true);
        try {
            await QuotationService.deleteQuotation(id);
            toast.success("Quotation deleted successfully!");
            setRefreshing(true);
            await fetchQuotations();
        } catch (error) {
            console.error("Error deleting quotation:", error);
            toast.error("Failed to delete quotation");
        } finally {
            setDeleting(false);
            setRefreshing(false);
        }
    };

    if (loading) {
        return (
            <Layout>
                <div className="text-center mt-4">
                    <div className="spinner-border text-primary" role="status"></div>
                    <p>Loading quotation data...</p>
                </div>
            </Layout>
        );
    }

    if (loading) {
        return (
            <Layout>
                <div className="text-center mt-4">
                    <div className="spinner-border text-primary" role="status"></div>
                    <p>Refreshing List...</p>
                </div>
            </Layout>
        );
    }


    return (
        <Layout>
            <div className="container mt-4">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h2>Quotations</h2>
                    <Link
                        to="/quotations/add"
                        className="btn btn-primary"
                        disabled={deleting || refreshing}
                    >
                        + New Quotation
                    </Link>
                </div>

                {quotations.length === 0 ? (
                    <div className="alert alert-info text-center">
                        No quotations found.
                    </div>
                ) : (
                    <div className="table-responsive">
                        <table className="table table-bordered align-middle">
                            <thead className="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Quotation No.</th>
                                    <th>Customer Name</th>
                                    <th>Date</th>
                                    <th>Total Amount (₹)</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {quotations.map((quotation, index) => (
                                    <tr key={quotation.id}>
                                        <td>{index + 1}</td>
                                        <td>{quotation.quotationNumber}</td>
                                        <td>{quotation.customerName || "-"}</td>
                                        <td>
                                            {quotation.createdAt
                                                ? new Date(
                                                    quotation.createdAt
                                                ).toLocaleDateString()
                                                : "-"}
                                        </td>
                                        <td>
                                            {quotation.totalAmount
                                                ? quotation.totalAmount.toFixed(
                                                    2
                                                )
                                                : "0.00"}
                                        </td>
                                        <td>
                                            <div className="btn-group">
                                                <button
                                                    className="btn btn-sm btn-info"
                                                    onClick={() =>
                                                        navigate(
                                                            `/quotations/view/${quotation.id}`
                                                        )
                                                    }
                                                    disabled={deleting}
                                                >
                                                    View
                                                </button>
                                                <button
                                                    className="btn btn-sm btn-warning mx-1"
                                                    onClick={() =>
                                                        navigate(
                                                            `/quotations/edit/${quotation.id}`
                                                        )
                                                    }
                                                    disabled={deleting}
                                                >
                                                    Edit
                                                </button>
                                                <button
                                                    className="btn btn-sm btn-danger"
                                                    onClick={() =>
                                                        handleDelete(
                                                            quotation.id
                                                        )
                                                    }
                                                    disabled={deleting}
                                                >
                                                    {deleting
                                                        ? "Deleting..."
                                                        : "Delete"}
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

export default QuotationList;
