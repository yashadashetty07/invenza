import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Layout from "../../components/Layout";
import ProductService from "../../services/ProductService";

const ProductForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();

  const [product, setProduct] = useState({
    name: "",
    hsnCode: "",
    unit: "",
    price: "",
    gstRate: 18.0,
    quantity: "",
    discountType: "NONE",
    discountValue: 0.0,
  });

  const [loading, setLoading] = useState(false);
  const discountTypes = ["NONE", "PERCENTAGE", "FIXED"];

  useEffect(() => {
    if (id) fetchProduct(id);
  }, [id]);

  const fetchProduct = async (id) => {
    setLoading(true);
    try {
      const response = await ProductService.getProductById(id);
      setProduct(response.data);
    } catch (error) {
      console.error("Error fetching product:", error);
      toast.error("Failed to load product details.");
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProduct({ ...product, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (id) {
        await ProductService.updateProduct(id, product);
        toast.success("✅ Product updated successfully!");
      } else {
        await ProductService.createProduct(product);
        toast.success("✅ Product added successfully!");
      }
      navigate("/products");
    } catch (error) {
      console.error("Error saving product:", error);
      toast.error("❌ Failed to save product.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
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
          <h6 className="text-white fw-semibold">Processing Product...</h6>
        </div>
      )}

      <div className="container mt-5">
        <div className="card shadow-lg border-0">
          <div className="card-header bg-primary text-white text-center py-3">
            <h4 className="mb-0">
              {id ? "Edit Product Details" : "Add New Product"}
            </h4>
          </div>

          <div className="card-body p-4">
            <form onSubmit={handleSubmit}>
              <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label fw-semibold">Product Name</label>
                  <input
                    type="text"
                    name="name"
                    value={product.name}
                    onChange={handleChange}
                    className="form-control"
                    placeholder="Enter product name"
                    required
                  />
                </div>

                <div className="col-md-6">
                  <label className="form-label fw-semibold">HSN Code</label>
                  <input
                    type="text"
                    name="hsnCode"
                    value={product.hsnCode}
                    onChange={handleChange}
                    className="form-control"
                    placeholder="Enter HSN code"
                    required
                  />
                </div>

                <div className="col-md-6">
                  <label className="form-label fw-semibold">Unit</label>
                  <input
                    type="text"
                    name="unit"
                    value={product.unit}
                    onChange={handleChange}
                    className="form-control"
                    placeholder="e.g. pcs, box, kg"
                    required
                  />
                </div>

                <div className="col-md-6">
                  <label className="form-label fw-semibold">Price (₹)</label>
                  <input
                    type="number"
                    step="0.01"
                    name="price"
                    value={product.price}
                    onChange={handleChange}
                    className="form-control"
                    placeholder="Enter base price"
                    required
                  />
                </div>

                <div className="col-md-6">
                  <label className="form-label fw-semibold">GST Rate (%)</label>
                  <input
                    type="number"
                    step="0.01"
                    name="gstRate"
                    value={product.gstRate}
                    onChange={handleChange}
                    className="form-control"
                    required
                  />
                </div>

                <div className="col-md-6">
                  <label className="form-label fw-semibold">
                    Available Quantity
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    name="quantity"
                    value={product.quantity}
                    onChange={handleChange}
                    className="form-control"
                    required
                  />
                </div>

                <div className="col-md-6">
                  <label className="form-label fw-semibold">Discount Type</label>
                  <select
                    name="discountType"
                    value={product.discountType}
                    onChange={handleChange}
                    className="form-select"
                  >
                    {discountTypes.map((type) => (
                      <option key={type} value={type}>
                        {type}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="col-md-6">
                  <label className="form-label fw-semibold">Discount Value</label>
                  <input
                    type="number"
                    step="0.01"
                    name="discountValue"
                    value={product.discountValue}
                    onChange={handleChange}
                    className="form-control"
                  />
                </div>
              </div>

              <div className="text-center mt-4">
                <button
                  type="submit"
                  className={`btn ${id ? "btn-success" : "btn-primary"
                    } px-5 fw-semibold`}
                >
                  {id ? "Update Product" : "Add Product"}
                </button>
                <button
                  type="button"
                  className="btn btn-secondary ms-3 px-4 fw-semibold"
                  onClick={() => navigate("/products")}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default ProductForm;
