import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Layout from "../../components/Layout";
import VendorService from "../../services/VendorService";
import ProductService from "../../services/ProductService";
import PurchaseOrderService from "../../services/PurchaseOrderService";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Select from "react-select";

const PurchaseOrderForm = () => {
    const [vendors, setVendors] = useState([]);
    const [products, setProducts] = useState([]);
    const [order, setOrder] = useState({
        vendorId: "",
        items: [],
        status: "PENDING",
    });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const navigate = useNavigate();
    const { id } = useParams();

    useEffect(() => {
        const init = async () => {
            try {
                await Promise.all([fetchVendors(), fetchProducts()]);
                if (id) await fetchOrderById(id);
            } catch (err) {
                console.error("Initialization error:", err);
                toast.error("Failed to load purchase order data.");
            } finally {
                setLoading(false);
            }
        };
        init();
    }, [id]);

    const fetchVendors = async () => {
        try {
            const res = await VendorService.getAllVendors();
            const data = Array.isArray(res.data) ? res.data : res;
            setVendors(data || []);
        } catch (error) {
            console.error("Error fetching vendors:", error);
            toast.error("Failed to fetch vendors.");
        }
    };

    const fetchProducts = async () => {
        try {
            const res = await ProductService.getAllProducts();
            const data = Array.isArray(res.data) ? res.data : res;
            setProducts(data || []);
        } catch (error) {
            console.error("Error fetching products:", error);
            toast.error("Failed to fetch products.");
        }
    };

    const fetchOrderById = async (orderId) => {
        try {
            const res = await PurchaseOrderService.getPurchaseOrderById(orderId);
            const orderData = res.data || res;

            setOrder({
                vendorId: orderData.vendorId || "",
                status: orderData.status || "PENDING",
                items:
                    orderData.items?.map((i) => ({
                        productId: i.productId,
                        quantity: i.quantity,
                        price: i.price,
                    })) || [],
            });
        } catch (error) {
            console.error("Error fetching order:", error);
            toast.error("Failed to fetch order details.");
        }
    };


    const handleAddItem = () => {
        setOrder((prev) => ({
            ...prev,
            items: [...prev.items, { productId: "", quantity: 1, price: 0 }],
        }));
    };

    const handleRemoveItem = (index) => {
        setOrder((prev) => ({
            ...prev,
            items: prev.items.filter((_, i) => i !== index),
        }));
    };

    const handleProductSelect = async (index, productId) => {
        try {
            const product =
                products.find((p) => p.id === parseInt(productId)) ||
                (await ProductService.getProductById(productId)).data;

            setOrder((prev) => {
                const updated = [...prev.items];
                updated[index] = {
                    ...updated[index],
                    productId: product.id,
                    price: product.price || 0,
                };
                return { ...prev, items: updated };
            });
        } catch (error) {
            console.error("Error selecting product:", error);
            toast.error("Failed to load product details.");
        }
    };

    const handleItemChange = (index, field, value) => {
        setOrder((prev) => {
            const updated = [...prev.items];
            updated[index][field] = value;
            return { ...prev, items: updated };
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);

        const sanitizedItems = order.items.map((i) => ({
            productId: Number(i.productId),
            quantity: Number(i.quantity) || 1,
            price: Number(i.price) || 0,
        }));

        const payload = {
            vendorId: Number(order.vendorId),
            status: order.status,
            items: sanitizedItems,
        };

        try {
            if (id) {
                await PurchaseOrderService.updatePurchaseOrder(id, payload);
                toast.success("✅ Purchase order updated successfully!");
            } else {
                await PurchaseOrderService.createPurchaseOrder(payload);
                toast.success("✅ Purchase order created successfully!");
            }
            navigate("/purchase-orders");
        } catch (error) {
            console.error("Save error:", error);
            toast.error(error.response?.data?.message || "Failed to save purchase order.");
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <Layout>
                <div className="d-flex justify-content-center align-items-center" style={{ height: "70vh" }}>
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                </div>
            </Layout>
        );
    }

    return (
        <Layout>
            <div className="container mt-4">
                <div className="card shadow-sm p-4">
                    <div className="d-flex justify-content-between align-items-center mb-4">
                        <h4 className="mb-0">{id ? "Edit Purchase Order" : "Create Purchase Order"}</h4>
                        <button onClick={() => navigate("/purchase-orders")} className="btn btn-secondary">
                            Back
                        </button>
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label className="form-label fw-bold">Vendor</label>
                            <select
                                className="form-select"
                                value={order.vendorId}
                                onChange={(e) =>
                                    setOrder((prev) => ({ ...prev, vendorId: e.target.value }))
                                }
                                required
                            >
                                <option value="">Select Vendor</option>
                                {vendors.map((v) => (
                                    <option key={v.id} value={v.id}>
                                        {v.name || v.vendorName} ({v.gstNumber || "N/A"})
                                    </option>
                                ))}
                            </select>
                        </div>

                        <h5 className="mt-4 mb-3">Order Items</h5>
                        <div className="table-responsive">
                            <table className="table table-bordered align-middle">
                                <thead className="table-light">
                                    <tr>
                                        <th>Product</th>
                                        <th>Qty</th>
                                        <th>Price (₹)</th>
                                        <th>Total</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {order.items.map((item, index) => (
                                        <tr key={index}>
                                            <td>
                                                <Select
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
                                                                        (p) => p.id === parseInt(item.productId)
                                                                    )?.name || "",
                                                            }
                                                            : null
                                                    }
                                                    onChange={(option) => handleProductSelect(index, option.value)}
                                                    placeholder="Select product..."
                                                    isSearchable
                                                    menuPortalTarget={document.body}
                                                    styles={{
                                                        menuPortal: (base) => ({ ...base, zIndex: 9999 }),
                                                        menu: (base) => ({ ...base, zIndex: 9999 }),
                                                    }}
                                                />
                                            </td>
                                            <td>
                                                <input
                                                    type="number"
                                                    className="form-control"
                                                    value={item.quantity}
                                                    min="1"
                                                    onChange={(e) =>
                                                        handleItemChange(index, "quantity", e.target.value)
                                                    }
                                                    required
                                                />
                                            </td>
                                            <td>
                                                <input
                                                    type="number"
                                                    className="form-control"
                                                    value={item.price}
                                                    min="0"
                                                    step="0.01"
                                                    onChange={(e) =>
                                                        handleItemChange(index, "price", e.target.value)
                                                    }
                                                    required
                                                />
                                            </td>
                                            <td>₹{(item.quantity * item.price).toFixed(2)}</td>
                                            <td>
                                                <button
                                                    type="button"
                                                    className="btn btn-sm btn-outline-danger"
                                                    onClick={() => handleRemoveItem(index)}
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
                            className="btn btn-outline-primary mb-3"
                            onClick={handleAddItem}
                        >
                            + Add Item
                        </button>

                        <div className="mb-4">
                            <label className="form-label fw-bold">Status</label>
                            <select
                                className="form-select"
                                value={order.status}
                                onChange={(e) =>
                                    setOrder((prev) => ({ ...prev, status: e.target.value }))
                                }
                            >
                                <option value="PENDING">Pending</option>
                                <option value="APPROVED">Approved</option>
                                <option value="DELIVERED">Delivered</option>
                                <option value="PARTIALLY_DELIVERED">Partially Delivered</option>
                                <option value="CANCELLED">Cancelled</option>
                            </select>
                        </div>

                        <div className="d-flex justify-content-end">
                            <button
                                type="submit"
                                className="btn btn-success me-2"
                                disabled={saving}
                            >
                                {saving
                                    ? "Saving..."
                                    : id
                                        ? "Update Purchase Order"
                                        : "Create Purchase Order"}
                            </button>
                            <button
                                type="button"
                                className="btn btn-secondary"
                                onClick={() => navigate("/purchase-orders")}
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

export default PurchaseOrderForm;