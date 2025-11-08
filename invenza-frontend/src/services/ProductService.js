import api from "../api/axios";
const BASE_URL = "/products";

class ProductService {
  getAllProducts() {
    return api.get(BASE_URL);
  }

  getProductById(id) {
    return api.get(`${BASE_URL}/${id}`);
  }

  createProduct(product) {
    return api.post(BASE_URL, product);
  }

  updateProduct(id, product) {
    return api.put(`${BASE_URL}/${id}`, product);
  }

  deleteProduct(id) {
    return api.delete(`${BASE_URL}/${id}`);
  }

  importProducts(products) {
    return api.post(`${BASE_URL}/bulk`, products);
  }

  updateStock(id, newQuantity) {
    return api.patch(`${BASE_URL}/${id}/stock`, null, {
      params: { newQuantity },
    });
  }
}

export default new ProductService();
