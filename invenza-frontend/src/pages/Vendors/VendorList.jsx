import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Layout from "../../components/Layout";
import VendorService from "../../services/VendorService";
import Loader from "../../components/Loader"; // make sure this exists

const VendorList = () => {
    const [vendors, setVendors] = useState([]);
    const [search, setSearch] = useState("");
    const [loading, setLoading] = useState(true);
    const [deletingId, setDeletingId] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 5;

    const navigate = useNavigate();

    useEffect(() => {
        fetchVendors();
    }, []);

    const fetchVendors = async () => {
        setLoading(true);
        try {
            const response = await VendorService.getAllVendors();
            const sanitized = response.data.map(({ id, name, phone, email, gstNumber }) => ({
                id, name, phone, email, gstNumber
            }));
            setVendors(sanitized);
        } catch (error) {
            toast.error("Failed to fetch vendors.");
            console.error("Error fetching vendors:", error);
        } finally {
            setLoading(false);
        }
    };


    const handleEdit = (id) => navigate(`/vendors/edit/${id}`);

    const handleDelete = async (id) => {
        const confirmDelete = window.confirm("Are you sure you want to delete this vendor?");
        if (!confirmDelete) return;

        setDeletingId(id);
        try {
            await VendorService.deleteVendor(id);
            setVendors(vendors.filter((v) => v.id !== id));
            toast.success("Vendor deleted successfully.");
        } catch (error) {
            toast.error("Failed to delete vendor.");
            console.error("Error deleting vendor:", error);
        } finally {
            setDeletingId(null);
        }
    };

    const filteredVendors = vendors.filter(
        (vendor) =>
            vendor.name.toLowerCase().includes(search.toLowerCase()) ||
            vendor.id.toString().includes(search)
    );

    const totalPages = Math.ceil(filteredVendors.length / itemsPerPage);
    const paginatedVendors = filteredVendors.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );

    return (
        <Layout>
            <div className="container mt-4">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h1 className="h4">Vendors List</h1>
                    <button
                        onClick={() => navigate("/vendors/add")}
                        className="btn btn-success"
                    >
                        Add Vendor
                    </button>
                </div>

                <div className="row mb-3">
                    <div className="col-md-4 mb-2">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Search by ID or Name"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                    </div>
                </div>

                {loading ? (
                    <Loader />
                ) : (
                    <>
                        <table className="table table-bordered table-striped">
                            <thead className="table-dark">
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Contact</th>
                                    <th>Email</th>
                                    <th>GST Number</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {paginatedVendors.map((vendor) => (
                                    <tr key={vendor.id}>
                                        <td>{vendor.id}</td>
                                        <td>{vendor.name}</td>
                                        <td>{vendor.phone}</td>
                                        <td>{vendor.email}</td>
                                        <td>{vendor.gstNumber}</td>
                                        <td>
                                            <button
                                                onClick={() => handleEdit(vendor.id)}
                                                className="btn btn-primary btn-sm me-2"
                                            >
                                                Edit
                                            </button>
                                            <button
                                                onClick={() => handleDelete(vendor.id)}
                                                className="btn btn-danger btn-sm"
                                                disabled={deletingId === vendor.id}
                                            >
                                                {deletingId === vendor.id ? "Deleting..." : "Delete"}
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                {paginatedVendors.length === 0 && (
                                    <tr>
                                        <td colSpan="6" className="text-center text-muted py-3">
                                            No vendors found.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>

                        <div className="d-flex justify-content-center mt-4">
                            {Array.from({ length: totalPages }, (_, i) => (
                                <button
                                    key={i}
                                    className={`btn btn-sm me-2 ${currentPage === i + 1 ? "btn-primary" : "btn-outline-secondary"
                                        }`}
                                    onClick={() => setCurrentPage(i + 1)}
                                >
                                    {i + 1}
                                </button>
                            ))}
                        </div>
                    </>
                )}
            </div>
        </Layout>
    );
};

export default VendorList;
