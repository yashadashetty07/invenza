import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Layout from "../../components/Layout";
import VendorService from "../../services/VendorService";

const VendorForm = () => {
    const [vendor, setVendor] = useState({
        name: "",
        email: "",
        phone: "",
        gstNumber: "",
        address: "",
    });
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const { id } = useParams();
    const isEdit = !!id;

    useEffect(() => {
        if (isEdit) fetchVendorById();
    }, [id]);

    const fetchVendorById = async () => {
        setLoading(true);
        try {
            const response = await VendorService.getVendorById(id);
            setVendor(response.data);
        } catch (error) {
            const msg = error.response?.data?.message || "❌ Failed to load vendor details.";
            toast.error(msg);
            console.error("Error fetching vendor:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setVendor((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            if (isEdit) {
                await VendorService.updateVendor(id, vendor);
                toast.success("✅ Vendor updated successfully!");
            } else {
                await VendorService.createVendor(vendor);
                toast.success("✅ Vendor added successfully!");
            }
            navigate("/vendors");
        } catch (error) {
            const msg = error.response?.data?.message || "❌ Operation failed!";
            toast.error(msg);
            console.error("Error saving vendor:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Layout>
            {/* Overlay Loader */}
            {loading && (
                <div
                    className="position-fixed top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center bg-dark bg-opacity-50"
                    style={{ zIndex: 1050 }}
                >
                    <div
                        className="spinner-border text-light mb-3"
                        role="status"
                        style={{ width: "3rem", height: "3rem" }}
                    >
                        <span className="visually-hidden">Loading...</span>
                    </div>
                    <h6 className="text-white fw-semibold">
                        {isEdit ? "Updating Vendor..." : "Saving Vendor..."}
                    </h6>
                </div>
            )}

            <div className="container mt-4">
                <div className="card shadow-sm border-0">
                    <div className="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                        <h5 className="mb-0 fw-semibold">
                            {isEdit ? "✏️ Edit Vendor" : "➕ Add Vendor"}
                        </h5>
                        <button
                            onClick={() => navigate("/vendors")}
                            className="btn btn-light btn-sm"
                        >
                            ← Back
                        </button>
                    </div>

                    <div className="card-body">
                        <form onSubmit={handleSubmit}>
                            {/* Vendor Name */}
                            <div className="mb-3">
                                <label className="form-label fw-semibold">Vendor Name</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    name="name"
                                    value={vendor.name}
                                    onChange={handleChange}
                                    required
                                    disabled={loading}
                                    placeholder="Enter vendor name"
                                />
                            </div>

                            {/* Phone */}
                            <div className="mb-3">
                                <label className="form-label fw-semibold">Phone</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    name="phone"
                                    value={vendor.phone}
                                    onChange={handleChange}
                                    required
                                    disabled={loading}
                                    placeholder="Enter phone number"
                                />
                            </div>

                            {/* Email */}
                            <div className="mb-3">
                                <label className="form-label fw-semibold">Email</label>
                                <input
                                    type="email"
                                    className="form-control"
                                    name="email"
                                    value={vendor.email}
                                    onChange={handleChange}
                                    disabled={loading}
                                    placeholder="Enter email address"
                                />
                            </div>

                            {/* Address */}
                            <div className="mb-3">
                                <label className="form-label fw-semibold">Address</label>
                                <textarea
                                    className="form-control"
                                    name="address"
                                    rows="3"
                                    value={vendor.address}
                                    onChange={handleChange}
                                    disabled={loading}
                                    placeholder="Enter full address"
                                ></textarea>
                            </div>

                            {/* GST Number */}
                            <div className="mb-3">
                                <label className="form-label fw-semibold">GST Number</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    name="gstNumber"
                                    value={vendor.gstNumber}
                                    onChange={handleChange}
                                    required
                                    disabled={loading}
                                    placeholder="Enter GST number"
                                />
                            </div>

                            {/* Submit Buttons */}
                            <div className="d-flex justify-content-end mt-4">
                                <button
                                    type="submit"
                                    className={`btn ${isEdit ? "btn-warning" : "btn-success"} px-4`}
                                    disabled={loading}
                                >
                                    {isEdit ? "Update Vendor" : "Add Vendor"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </Layout>
    );
};

export default VendorForm;
