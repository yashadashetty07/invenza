import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import BillService from "../../services/BillService";
import { toast } from "react-toastify";

const BillView = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [bill, setBill] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchBill();
    }, [id]);

    const fetchBill = async () => {
        try {
            const res = await BillService.getBillById(id);
            setBill(res.data);
        } catch (err) {
            console.error("Error fetching bill:", err);
            toast.error("Failed to fetch bill details");
        } finally {
            setLoading(false);
        }
    };

    const handleGeneratePDF = async () => {
        try {
            const response = await BillService.generatePDF(id);
            const blob = new Blob([response.data], { type: "application/pdf" });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = `bill_${id}.pdf`;
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
                    <p>Loading bill...</p>
                </div>
            </Layout>
        );

    if (!bill)
        return (
            <Layout>
                <div className="text-center mt-5">
                    <p>No bill found.</p>
                </div>
            </Layout>
        );

    return (
        <Layout>
            <div className="container mt-4">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h2>Bill Details</h2>
                    <div>
                        <button
                            className="btn btn-warning me-2"
                            onClick={() => navigate(`/bills/edit/${bill.id}`)}
                        >
                            Edit Bill
                        </button>
                        <button className="btn btn-success me-2" onClick={handleGeneratePDF}>
                            Generate PDF
                        </button>
                        <button className="btn btn-secondary" onClick={() => navigate("/bills")}>
                            Back to List
                        </button>
                    </div>
                </div>

                <div className="card p-4 mb-3">
                    <h5>Customer Details</h5>
                    <p><strong>Name:</strong> {bill.customerName}</p>
                    <p><strong>Address:</strong> {bill.customerAddress || "-"}</p>
                    <p><strong>GSTIN:</strong> {bill.customerGSTIN || "-"}</p>
                    <p><strong>Bill No:</strong> {bill.billNumber}</p>
                    <p><strong>Date:</strong> {new Date(bill.billDate).toLocaleDateString()}</p>
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
                                <th>GST (%)</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            {bill.items.map((item, idx) => (
                                <tr key={idx}>
                                    <td>{item.productName}</td>
                                    <td>{item.hsnCode}</td>
                                    <td>{item.quantity}</td>
                                    <td>{item.mrpPrice}</td>
                                    <td>{item.discountedPrice}</td>
                                    <td>{item.gstAmount}</td>
                                    <td>{item.totalFinalPrice?.toFixed(2)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <h5 className="text-end mt-3">
                        Grand Total: ₹{bill.finalAmount?.toFixed(2)}
                    </h5>
                </div>
            </div>
        </Layout>
    );
};

export default BillView;
