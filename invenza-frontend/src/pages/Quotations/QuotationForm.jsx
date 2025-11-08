import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Layout from "../../components/Layout";
import ProductService from "../../services/ProductService";
import QuotationService from "../../services/QuotationService";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Select from "react-select";

const QuotationForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [products, setProducts] = useState([]);
    const [deleting, setDeleting] = useState(false);
    const [processingItem, setProcessingItem] = useState(false);

    const [quotation, setQuotation] = useState({
        quotationNumber: "",
        quotationDate: new Date().toISOString().split("T")[0],
        customerName: "",
        customerAddress: "",
        customerGSTIN: "",
        items: [],
        finalAmount: 0,
    });


    useEffect(() => {
        const init = async () => {
            try {
                const res = await ProductService.getAllProducts();
                const data = Array.isArray(res.data)
                    ? res.data
                    : res.data.content || res.data;
                setProducts(data || []);

                if (id) {
                    const qres = await QuotationService.getQuotationById(id);
                    const q = qres.data;
                    setQuotation({
                        ...q,
                        quotationDate: q.quotationDate
                            ? q.quotationDate.split("T")[0]
                            : new Date().toISOString().split("T")[0],
                        finalAmount: q.totalAmount || 0,
                        items: (q.items || []).map((item) => ({
                            ...item,
                            mrp: item.mrpPrice || 0,
                            total: item.totalPrice || 0,
                        })),
                    });
                }
            } catch (err) {
                console.error("Initialization error:", err);
                toast.error("Failed to load quotation data.");
            } finally {
                setLoading(false);
            }
        };
        init();
    }, [id]);

    const handleChange = (e) => {
        setQuotation({ ...quotation, [e.target.name]: e.target.value });
    };

    const handleItemChange = (index, field, value) => {
        const updatedItems = [...quotation.items];
        let item = { ...updatedItems[index], [field]: value };

        if (field === "productId") {
            const product = products.find((p) => p.id == value);
            if (product) {
                item.productId = product.id;
                item.productName = product.name;
                item.hsnCode = product.hsnCode || "";
                item.mrp = product.price || 0;
                item.discountedPrice = product.price || 0;
                item.gstRate = product.gstRate || 0;
            }
        }

        const qty = parseFloat(item.quantity) || 0;
        const disc = parseFloat(item.discountedPrice) || 0;
        const gst = parseFloat(item.gstRate) || 0;
        item.total = qty * disc + (qty * disc * gst) / 100;

        updatedItems[index] = item;
        const finalAmount = updatedItems.reduce(
            (sum, it) => sum + (parseFloat(it.total) || 0),
            0
        );

        setQuotation({ ...quotation, items: updatedItems, finalAmount });
    };

    const addItem = () => {
        setProcessingItem(true);
        setTimeout(() => {
            setQuotation((prev) => ({
                ...prev,
                items: [
                    ...prev.items,
                    {
                        productId: "",
                        productName: "",
                        hsnCode: "",
                        quantity: 1,
                        mrp: 0,
                        discountedPrice: 0,
                        gstRate: 0,
                        total: 0,
                    },
                ],
            }));
            setProcessingItem(false);
        }, 200);
    };


    const removeItem = (index) => {
        setProcessingItem(true);
        setTimeout(() => {
            const updatedItems = quotation.items.filter((_, i) => i !== index);
            const finalAmount = updatedItems.reduce(
                (sum, it) => sum + (parseFloat(it.total) || 0),
                0
            );
            setQuotation({ ...quotation, items: updatedItems, finalAmount });
            setProcessingItem(false);
        }, 200);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        try {
            if (id) {
                await QuotationService.updateQuotation(id, quotation);
                toast.success("Quotation updated successfully!");
            } else {
                await QuotationService.createQuotation(quotation);
                toast.success("Quotation created successfully!");
            }
            navigate("/quotations");
        } catch (error) {
            console.error("Error saving quotation:", error);
            toast.error("Failed to save quotation");
        } finally {
            setSaving(false);
        }
    };

    const renderLoader = (text) => (
        <Layout>
            <div className="text-center mt-5">
                <div className="spinner-border text-primary" role="status"></div>
                <p className="mt-2">{text}</p>
            </div>
        </Layout>
    );

    if (loading) return renderLoader("Loading quotation data...");
    if (saving) return renderLoader(id ? "Updating quotation..." : "Saving quotation...");
    if (deleting) return renderLoader("Deleting quotation...");

    return (
        <Layout>
            <div className="container mt-4">
                <div className="card shadow-sm p-4">
                    <div className="d-flex justify-content-between align-items-center mb-4">
                        <h4 className="mb-0">
                            {id ? "Edit Quotation" : "Create Quotation"}
                        </h4>
                        <button
                            onClick={() => navigate("/quotations")}
                            className="btn btn-secondary"
                        >
                            Back
                        </button>
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="row mb-3">
                            {id && (
                                <div className="col-md-3">
                                    <label className="form-label fw-bold">
                                        Quotation No.
                                    </label>
                                    <input
                                        type="text"
                                        name="quotationNumber"
                                        value={quotation.quotationNumber || ""}
                                        className="form-control"
                                        readOnly
                                    />
                                </div>
                            )}

                            <div className="col-md-3">
                                <label className="form-label fw-bold">Date</label>
                                <input
                                    type="date"
                                    name="quotationDate"
                                    value={quotation.quotationDate}
                                    onChange={handleChange}
                                    className="form-control"
                                    required
                                />
                            </div>

                            <div className="col-md-3">
                                <label className="form-label fw-bold">
                                    Customer Name
                                </label>
                                <input
                                    type="text"
                                    name="customerName"
                                    value={quotation.customerName}
                                    onChange={handleChange}
                                    className="form-control"
                                    required
                                />
                            </div>

                            <div className="col-md-3">
                                <label className="form-label fw-bold">GSTIN</label>
                                <input
                                    type="text"
                                    name="customerGSTIN"
                                    value={quotation.customerGSTIN}
                                    onChange={handleChange}
                                    className="form-control"
                                />
                            </div>
                        </div>

                        <div className="mb-4">
                            <label className="form-label fw-bold">
                                Customer Address
                            </label>
                            <textarea
                                name="customerAddress"
                                value={quotation.customerAddress}
                                onChange={handleChange}
                                className="form-control"
                                rows="2"
                            ></textarea>
                        </div>

                        <h5 className="mt-4 mb-3">Quotation Items</h5>
                        <div className="table-responsive">
                            <table className="table table-bordered align-middle">
                                <thead className="table-light">
                                    <tr>
                                        <th>Product</th>
                                        <th>HSN</th>
                                        <th>Qty</th>
                                        <th>MRP (₹)</th>
                                        <th>Selling Price (₹)</th>
                                        <th>GST%</th>
                                        <th>Total (₹)</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {quotation.items.map((item, index) => (
                                        <tr key={index}>
                                            <td style={{ minWidth: "180px" }}>
                                                <Select
                                                    classNamePrefix="select"
                                                    menuPortalTarget={document.body}
                                                    styles={{
                                                        menuPortal: (base) => ({
                                                            ...base,
                                                            zIndex: 9999,
                                                        }),
                                                    }}
                                                    options={products.map((p) => ({
                                                        value: p.id,
                                                        label: p.name,
                                                    }))}
                                                    value={
                                                        item.productId
                                                            ? {
                                                                value: item.productId,
                                                                label:
                                                                    products.find(
                                                                        (p) =>
                                                                            p.id ===
                                                                            parseInt(
                                                                                item.productId
                                                                            )
                                                                    )?.name || "",
                                                            }
                                                            : null
                                                    }
                                                    onChange={(option) =>
                                                        handleItemChange(
                                                            index,
                                                            "productId",
                                                            option.value
                                                        )
                                                    }
                                                    placeholder="Select product..."
                                                    isSearchable
                                                />
                                            </td>
                                            <td>{item.hsnCode || "-"}</td>
                                            <td>
                                                <input
                                                    type="number"
                                                    className="form-control"
                                                    value={item.quantity || ""}
                                                    min="1"
                                                    onChange={(e) =>
                                                        handleItemChange(
                                                            index,
                                                            "quantity",
                                                            e.target.value
                                                        )
                                                    }
                                                />
                                            </td>
                                            <td>
                                                ₹
                                                {(item.mrp
                                                    ? parseFloat(item.mrp).toFixed(2)
                                                    : "0.00")}
                                            </td>
                                            <td>
                                                <input
                                                    type="number"
                                                    className="form-control"
                                                    value={item.discountedPrice || ""}
                                                    min="0"
                                                    step="0.01"
                                                    onChange={(e) =>
                                                        handleItemChange(
                                                            index,
                                                            "discountedPrice",
                                                            e.target.value
                                                        )
                                                    }
                                                />
                                            </td>
                                            <td>{item.gstRate || 0}%</td>
                                            <td>
                                                ₹
                                                {(item.total
                                                    ? parseFloat(item.total).toFixed(2)
                                                    : "0.00")}
                                            </td>
                                            <td>
                                                <button
                                                    type="button"
                                                    className="btn btn-sm btn-outline-danger"
                                                    onClick={() => removeItem(index)}
                                                >
                                                    ✕
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        <button
                            type="button"
                            className="btn btn-outline-primary mt-2"
                            onClick={addItem}
                            disabled={processingItem}
                        >
                            {processingItem ? "Adding..." : "Add Item"}
                        </button>


                        <h5 className="text-end me-2 mb-4">
                            Grand Total:{" "}
                            <span className="fw-bold">
                                ₹
                                {(quotation.finalAmount
                                    ? quotation.finalAmount.toFixed(2)
                                    : "0.00")}
                            </span>
                        </h5>

                        <div className="d-flex justify-content-end">
                            <button
                                type="submit"
                                className="btn btn-success me-2"
                                disabled={saving || deleting || processingItem}
                            >
                                {saving
                                    ? "Saving..."
                                    : id
                                        ? "Update Quotation"
                                        : "Create Quotation"}
                            </button>
                            <button
                                type="button"
                                className="btn btn-secondary"
                                onClick={() => navigate("/quotations")}
                                disabled={saving || deleting || processingItem}
                            >
                                Cancel
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </Layout>
    );
};

export default QuotationForm;
