import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Layout from "../../components/Layout";
import ProductService from "../../services/ProductService";

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const response = await ProductService.getAllProducts();
      setProducts(response.data);
    } catch (error) {
      toast.error("Failed to fetch products.");
      console.error("Error fetching products:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (id) => navigate(`/products/edit/${id}`);

  const handleDelete = async (id) => {
    const confirmDelete = window.confirm("Are you sure you want to delete this product?");
    if (!confirmDelete) return;

    setDeletingId(id);
    try {
      await ProductService.deleteProduct(id);
      setProducts(products.filter((p) => p.id !== id));
      toast.success("Product deleted successfully.");
    } catch (error) {
      toast.error("Failed to delete product.");
      console.error("Error deleting product:", error);
    } finally {
      setDeletingId(null);
    }
  };

  const filteredProducts = products.filter(
    (p) =>
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.hsnCode.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <Layout>
      {loading && (
        <div
          className="position-fixed top-0 start-0 w-100 h-100 d-flex flex-column justify-content-center align-items-center bg-dark bg-opacity-50"
          style={{ zIndex: 1050 }}
        >
          <div className="spinner-border text-light mb-3" role="status" style={{ width: "3rem", height: "3rem" }}>
            <span className="visually-hidden">Loading...</span>
          </div>
          <h6 className="text-white fw-semibold">Loading Products...</h6>
        </div>
      )}

      <div className="container mt-4">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h1 className="h4">Products List</h1>
          <div>
            <button
              onClick={() => navigate("/products/add")}
              className="btn btn-success me-2"
            >
              Add Product
            </button>
            <button
              onClick={() => navigate("/products/import")}
              className="btn btn-outline-primary"
            >
              Bulk Import
            </button>
          </div>
        </div>

        <div className="mb-3">
          <input
            type="text"
            className="form-control"
            placeholder="Search by Name or HSN Code"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        {!loading && (
          <table className="table table-bordered table-striped">
            <thead className="table-dark">
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>HSN Code</th>
                <th>Unit</th>
                <th>Price (₹)</th>
                <th>GST (%)</th>
                <th>Qty</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredProducts.length > 0 ? (
                filteredProducts.map((p) => (
                  <tr key={p.id}>
                    <td>{p.id}</td>
                    <td>{p.name}</td>
                    <td>{p.hsnCode}</td>
                    <td>{p.unit}</td>
                    <td>{p.price}</td>
                    <td>{p.gstRate}</td>
                    <td>{p.quantity}</td>
                    <td>
                      <button
                        onClick={() => handleEdit(p.id)}
                        className="btn btn-primary btn-sm me-2"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(p.id)}
                        className="btn btn-danger btn-sm"
                        disabled={deletingId === p.id}
                      >
                        {deletingId === p.id ? "Deleting..." : "Delete"}
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="10" className="text-center text-muted py-3">
                    No products found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </div>
    </Layout>
  );
};

export default ProductList;
