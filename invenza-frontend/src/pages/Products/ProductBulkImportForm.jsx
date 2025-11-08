import React, { useState } from "react";
import Papa from "papaparse";
import api from "../../api/axios";
import { toast } from "react-toastify";
import Layout from "../../components/Layout";

const BulkImportForm = () => {
    const [products, setProducts] = useState([]);
    const [rawCount, setRawCount] = useState(0);
    const [skippedCount, setSkippedCount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [fileName, setFileName] = useState("");
    const [status, setStatus] = useState("");

    const isValidProduct = (p) =>
        p.name &&
        p.hsnCode &&
        p.unit &&
        !isNaN(p.price) &&
        !isNaN(p.gstRate) &&
        !isNaN(p.quantity);

    const cleanProduct = (p) =>
        Object.fromEntries(
            Object.entries(p).map(([k, v]) => [k, v?.toString().trim()])
        );

    const handleFileUpload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        setFileName(file.name);
        setLoading(true);
        setProducts([]);
        setRawCount(0);
        setSkippedCount(0);
        setStatus("📄 Parsing CSV file...");

        try {
            const existingHsnsRes = await api.get("/products/hsns");
            const existingHsns = existingHsnsRes.data;

            Papa.parse(file, {
                header: true,
                skipEmptyLines: true,
                complete: (results) => {
                    setStatus("🔍 Validating products...");
                    const rawProducts = results.data.map(cleanProduct);
                    setRawCount(rawProducts.length);

                    const validProducts = rawProducts
                        .filter(isValidProduct)
                        .map((p) => ({
                            ...p,
                            price: parseFloat(p.price),
                            gstRate: parseFloat(p.gstRate),
                            quantity: parseFloat(p.quantity),
                        }));

                    const uniqueProducts = validProducts.filter(
                        (p) => !existingHsns.includes(p.hsnCode)
                    );

                    setSkippedCount(rawProducts.length - uniqueProducts.length);
                    setProducts(uniqueProducts);
                    setStatus("");

                    toast.info(
                        `${rawProducts.length - uniqueProducts.length} rows skipped (invalid or duplicate)`
                    );
                },
                error: (err) => {
                    console.error("CSV parsing error:", err);
                    setStatus("");
                    toast.error("❌ Failed to parse CSV file");
                },
            });
        } catch (err) {
            console.error("Error fetching HSNs:", err);
            setStatus("");
            toast.error("❌ Failed to fetch existing HSNs from backend");
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async () => {
        if (products.length === 0) {
            toast.warning("No valid products to import");
            return;
        }

        const confirmed = window.confirm(
            `⚠️ Are you sure you want to import ${products.length} products?`
        );
        if (!confirmed) return;

        setSubmitting(true);
        setStatus("🚀 Importing products...");
        try {
            const response = await api.post("/products/bulk", products);
            const importedCount = response.data.length || 0;

            toast.success(`✅ Successfully imported ${importedCount} products!`);
            setProducts([]);
            setRawCount(0);
            setSkippedCount(0);
            setFileName("");
        } catch (err) {
            console.error("Import error:", err);
            toast.error("❌ Failed to import products. Check console for details.");
        } finally {
            setSubmitting(false);
            setStatus("");
        }
    };

    return (
        <Layout>
            {/* Overlay Loader */}
            {(loading || submitting) && (
                <div
                    className="position-fixed top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center bg-dark bg-opacity-50"
                    style={{ zIndex: 1050 }}
                >
                    <div className="spinner-border text-light mb-3" role="status" style={{ width: "3rem", height: "3rem" }}>
                        <span className="visually-hidden">Loading...</span>
                    </div>
                    <h6 className="text-white fw-semibold">{status || "Processing, please wait..."}</h6>
                </div>
            )}

            <div className="container mt-4">
                <div className="card shadow-sm border-0">
                    <div className="card-body">
                        <div className="d-flex justify-content-between align-items-center mb-4">
                            <h4 className="mb-0 fw-bold text-primary">📦 Bulk Product Import</h4>
                            <a
                                href="/templates/product-import-template.csv"
                                download
                                className="btn btn-outline-primary"
                            >
                                ⬇️ Download CSV Template
                            </a>
                        </div>

                        <div className="mb-3">
                            <label className="form-label fw-semibold">Upload CSV File:</label>
                            <input
                                type="file"
                                accept=".csv"
                                onChange={handleFileUpload}
                                className="form-control"
                                disabled={loading || submitting}
                            />
                        </div>

                        {fileName && (
                            <p className="text-muted">
                                Selected file: <strong>{fileName}</strong>
                            </p>
                        )}

                        {status && !loading && !submitting && (
                            <div className="alert alert-info py-2" role="alert">
                                {status}
                            </div>
                        )}

                        {products.length > 0 && !loading && (
                            <>
                                <div className="alert alert-success py-2">
                                    ✅ {products.length} products ready to import (out of{" "}
                                    {rawCount} uploaded, {skippedCount} skipped)
                                </div>

                                <div className="table-responsive mt-3">
                                    <table className="table table-bordered table-striped align-middle">
                                        <thead className="table-light">
                                            <tr>
                                                <th>Name</th>
                                                <th>HSN Code</th>
                                                <th>Unit</th>
                                                <th>Price</th>
                                                <th>GST (%)</th>
                                                <th>Quantity</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {products.slice(0, 100).map((p, i) => (
                                                <tr key={i}>
                                                    <td>{p.name}</td>
                                                    <td>{p.hsnCode}</td>
                                                    <td>{p.unit}</td>
                                                    <td>{p.price}</td>
                                                    <td>{p.gstRate}</td>
                                                    <td>{p.quantity}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                                {products.length > 100 && (
                                    <p className="text-muted">Showing first 100 rows only...</p>
                                )}
                            </>
                        )}

                        <div className="d-flex justify-content-end mt-4">
                            <button
                                onClick={handleSubmit}
                                className="btn btn-success px-4"
                                disabled={submitting || products.length === 0}
                            >
                                {submitting ? "Importing..." : "🚀 Import Products"}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </Layout>
    );
};

export default BulkImportForm;
