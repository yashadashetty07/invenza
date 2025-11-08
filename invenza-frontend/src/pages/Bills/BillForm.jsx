import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Layout from "../../components/Layout";
import ProductService from "../../services/ProductService";
import BillService from "../../services/BillService";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const BillForm = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [saving, setSaving] = useState(false);
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(!!id);
    const [bill, setBill] = useState({
        customerName: "",
        customerAddress: "",
        customerGSTIN: "",
        billNumber: "",
        billDate: new Date().toISOString().split("T")[0],
        items: [],
    });

    useEffect(() => {
        ProductService.getAllProducts()
            .then((res) => {
                const data =
                    Array.isArray(res.data)
                        ? res.data
                        : res.data.content || res.data.products || [];
                setProducts(data);
            })
            .catch((err) => {
                console.error("Error fetching products:", err);
                toast.error("Failed to load products");
            });
    }, []);

    useEffect(() => {
        if (id) {
            setLoading(true);
            BillService.getBillById(id)
                .then((res) => {
                    const data = res.data;
                    setBill({
                        ...data,
                        billDate: new Date(data.billDate)
                            .toISOString()
                            .split("T")[0],
                    });
                })
                .catch((err) => {
                    console.error("Error fetching bill:", err);
                    toast.error("Failed to load bill details");
                })
                .finally(() => setLoading(false));
        }
    }, [id]);

    const handleChange = (e) => {
        setBill({ ...bill, [e.target.name]: e.target.value });
    };

    const handleItemChange = (index, e) => {
        const updatedItems = [...bill.items];
        updatedItems[index][e.target.name] = e.target.value;

        const quantity = parseFloat(updatedItems[index].quantity) || 0;
        const discountedPrice =
            parseFloat(updatedItems[index].discountedPrice) || 0;
        const gstAmount = parseFloat(updatedItems[index].gstAmount) || 0;

        updatedItems[index].unitFinalPrice =
            discountedPrice + (discountedPrice * gstAmount) / 100;
        updatedItems[index].totalFinalPrice =
            updatedItems[index].unitFinalPrice * quantity;

        setBill({ ...bill, items: updatedItems });
    };

    const addItem = () => {
        setBill({
            ...bill,
            items: [
                ...bill.items,
                {
                    productId: "",
                    productName: "",
                    hsnCode: "",
                    quantity: 1,
                    mrpPrice: 0,
                    discountedPrice: 0,
                    gstAmount: 0,
                    unitFinalPrice: 0,
                    totalFinalPrice: 0,
                },
            ],
        });
    };

    const removeItem = (index) => {
        const updatedItems = bill.items.filter((_, i) => i !== index);
        setBill({ ...bill, items: updatedItems });
    };

    const calculateGrandTotal = () =>
        bill.items.reduce(
            (sum, item) => sum + parseFloat(item.totalFinalPrice || 0),
            0
        );

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        try {
            const payload = { ...bill, finalAmount: calculateGrandTotal() };

            if (id) {
                await BillService.updateBill(id, payload);
                toast.success("Bill updated successfully!");
            } else {
                await BillService.createBill(payload);
                toast.success("Bill created successfully!");
            }

            navigate("/bills");
        } catch (error) {
            console.error("Error saving bill:", error);
            toast.error("Error saving bill");
        } finally {
            setSaving(false);
        }
    };


    if (loading) {
        return (
            <Layout>
                <div className="d-flex justify-content-center align-items-center" style={{ height: "60vh" }}>
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                    <p className="ms-2 mb-0">Loading bill details...</p>
                </div>
            </Layout>
        );
    }

    return (
        <Layout>
            <div className="container mt-3">
                <h2>{id ? "Edit Bill" : "Create New Bill"}</h2>

                <form onSubmit={handleSubmit}>
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label>Customer Name</label>
                            <input
                                type="text"
                                name="customerName"
                                value={bill.customerName}
                                onChange={handleChange}
                                className="form-control"
                                required
                            />
                        </div>

                        {id && (
                            <div className="col-md-6 mb-3">
                                <label>Bill Number</label>
                                <input
                                    type="text"
                                    name="billNumber"
                                    value={bill.billNumber}
                                    readOnly
                                    className="form-control bg-light"
                                />
                            </div>
                        )}
                    </div>

                    <div className="mb-3">
                        <label>Customer Address</label>
                        <textarea
                            name="customerAddress"
                            value={bill.customerAddress}
                            onChange={handleChange}
                            className="form-control"
                        />
                    </div>

                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label>Customer GSTIN</label>
                            <input
                                type="text"
                                name="customerGSTIN"
                                value={bill.customerGSTIN}
                                onChange={handleChange}
                                className="form-control"
                            />
                        </div>
                        <div className="col-md-6 mb-3">
                            <label>Bill Date</label>
                            <input
                                type="date"
                                name="billDate"
                                value={bill.billDate}
                                onChange={handleChange}
                                className="form-control"
                            />
                        </div>
                    </div>

                    <h4 className="mt-4">Items</h4>
                    <div className="table-responsive">
                        <table className="table table-bordered align-middle">
                            <thead className="table-light">
                                <tr>
                                    <th>Product</th>
                                    <th>HSN</th>
                                    <th>Qty</th>
                                    <th>MRP</th>
                                    <th>Discounted Price</th>
                                    <th>GST (%)</th>
                                    <th>Unit Final Price</th>
                                    <th>Total</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                {bill.items.map((item, index) => (
                                    <tr key={index}>
                                        <td>
                                            <select
                                                name="productId"
                                                value={item.productId}
                                                onChange={(e) => {
                                                    const product = products.find(
                                                        (p) =>
                                                            p.id ===
                                                            parseInt(e.target.value)
                                                    );
                                                    if (product) {
                                                        const updatedItems = [
                                                            ...bill.items,
                                                        ];
                                                        updatedItems[index] = {
                                                            ...updatedItems[index],
                                                            productId: product.id,
                                                            productName: product.name,
                                                            hsnCode: product.hsnCode,
                                                            mrpPrice: product.price,
                                                            discountedPrice:
                                                                product.price,
                                                            gstAmount:
                                                                product.gstRate,
                                                            quantity: 1,
                                                            unitFinalPrice:
                                                                product.price +
                                                                (product.price *
                                                                    product.gstRate) /
                                                                100,
                                                            totalFinalPrice:
                                                                (product.price +
                                                                    (product.price *
                                                                        product.gstRate) /
                                                                    100) * 1,
                                                        };
                                                        setBill({
                                                            ...bill,
                                                            items: updatedItems,
                                                        });
                                                    }
                                                }}
                                                className="form-select"
                                                required
                                            >
                                                <option value="">
                                                    Select Product
                                                </option>
                                                {products.map((p) => (
                                                    <option key={p.id} value={p.id}>
                                                        {p.name}
                                                    </option>
                                                ))}
                                            </select>
                                        </td>
                                        <td>{item.hsnCode}</td>
                                        <td>
                                            <input
                                                type="number"
                                                name="quantity"
                                                value={item.quantity}
                                                onChange={(e) =>
                                                    handleItemChange(index, e)
                                                }
                                                className="form-control"
                                                min="1"
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="number"
                                                name="mrpPrice"
                                                value={item.mrpPrice}
                                                readOnly
                                                className="form-control bg-light"
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="number"
                                                name="discountedPrice"
                                                value={item.discountedPrice}
                                                onChange={(e) =>
                                                    handleItemChange(index, e)
                                                }
                                                className="form-control"
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="number"
                                                name="gstAmount"
                                                value={item.gstAmount}
                                                onChange={(e) =>
                                                    handleItemChange(index, e)
                                                }
                                                className="form-control"
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="number"
                                                name="unitFinalPrice"
                                                value={item.unitFinalPrice.toFixed(
                                                    2
                                                )}
                                                readOnly
                                                className="form-control bg-light"
                                            />
                                        </td>
                                        <td>
                                            <input
                                                type="number"
                                                name="totalFinalPrice"
                                                value={item.totalFinalPrice.toFixed(
                                                    2
                                                )}
                                                readOnly
                                                className="form-control bg-light"
                                            />
                                        </td>
                                        <td>
                                            <button
                                                type="button"
                                                className="btn btn-danger btn-sm"
                                                onClick={() => removeItem(index)}
                                            >
                                                Remove
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    <button
                        type="button"
                        className="btn btn-secondary mb-3"
                        onClick={addItem}
                    >
                        + Add Item
                    </button>

                    <div className="mt-3">
                        <h4>Grand Total: ₹{calculateGrandTotal().toFixed(2)}</h4>
                    </div>
                    <button type="submit" className="btn btn-primary mt-3" disabled={saving}>
                        {saving ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2"></span>
                                {id ? "Updating..." : "Creating..."}
                            </>
                        ) : (
                            id ? "Update Bill" : "Create Bill"
                        )}
                    </button>

                </form>
            </div>
        </Layout>
    );
};

export default BillForm;
