import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import QuotationService from "../../services/QuotationService";
import { toast } from "react-toastify";

const QuotationView = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [quotation, setQuotation] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchQuotation();
    }, [id]);

    const fetchQuotation = async () => {
        try {
            const res = await QuotationService.getQuotationById(id);
            setQuotation(res.data);
        } catch (err) {
            console.error("Error fetching quotation:", err);
            toast.error("Failed to fetch quotation details");
        } finally {
            setLoading(false);
        }
    };

    const handleGeneratePDF = async () => {
        try {
            const response = await QuotationService.generatePDF(id);
            const blob = new Blob([response.data], { type: "application/pdf" });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = `quotation_${id}.pdf`;
            link.click();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error("Error generating PDF:", error);
            toast.error("Failed to generate PDF.");
        }
    };

    if (loading)
        return (
            <Layout>
                <div className="text-center mt-5">
                    <div className="spinner-border text-primary"></div>
                    <p>Loading quotation...</p>
                </div>
            </Layout>
        );

    if (!quotation)
        return (
            <Layout>
                <div className="text-center mt-5">
                    <p>No quotation found.</p>
                </div>
            </Layout>
        );

    return (
        <Layout>
            <div className="container mt-4">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h2>Quotation Details</h2>
                    <div>
                        <button
                            className="btn btn-warning me-2"
                            onClick={() => navigate(`/quotations/edit/${quotation.id}`)}
                        >
                            Edit Quotation
                        </button>
                        <button className="btn btn-success me-2" onClick={handleGeneratePDF}>
                            Generate PDF
                        </button>
                        <button className="btn btn-secondary" onClick={() => navigate("/quotations")}>
                            Back to List
                        </button>
                    </div>
                </div>

                <div className="card p-4 mb-3">
                    <h5>Customer Details</h5>
                    <p><strong>Name:</strong> {quotation.customerName}</p>
                    <p><strong>Address:</strong> {quotation.customerAddress || "-"}</p>
                    <p><strong>GSTIN:</strong> {quotation.customerGSTIN || "-"}</p>
                    <p><strong>Quotation No:</strong> {quotation.quotationNumber}</p>
                    <p><strong>Date:</strong> {new Date(quotation.createdAt).toLocaleDateString()}</p>
                </div>

                <div className="card p-3">
                    <h5>Items</h5>
                    <table className="table table-bordered">
                        <thead className="table-light">
                            <tr>
                                <th>Product</th>
                                <th>HSN</th>
                                <th>Qty</th>
                                <th>MRP</th>
                                <th>Discounted Price</th>
                                <th>Discount (₹)</th>
                                <th>GST (%)</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            {quotation.items.map((item, idx) => (
                                <tr key={idx}>
                                    <td>{item.productName}</td>
                                    <td>{item.hsnCode || "-"}</td>
                                    <td>{item.quantity}</td>
                                    <td>{item.mrpPrice?.toFixed(2)}</td>
                                    <td>{item.discountedPrice?.toFixed(2)}</td>
                                    <td>{item.discount?.toFixed(2)}</td>
                                    <td>{item.gstRate?.toFixed(2)}%</td>
                                    <td>{item.totalPrice?.toFixed(2)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <h5 className="text-end mt-3">
                        Grand Total: ₹{quotation.totalAmount?.toFixed(2)}
                    </h5>
                </div>
            </div>
        </Layout>
    );
};

export default QuotationView;
