
import api from "../api/axios";
const getAllPurchaseOrders = () => api.get("/purchase-orders");

const getOrderById = (id) => api.get(`/purchase-orders/${id}`);

const createOrder = (purchaseOrder) => api.post("/purchase-orders", purchaseOrder);

const updateOrder = (id, purchaseOrder) => api.put(`/purchase-orders/${id}`, purchaseOrder);

const deleteOrder = (id) => api.delete(`/purchase-orders/${id}`);

const generatePurchaseOrderPDF = (id) =>
    api.get(`/pdf/purchase-order/${id}`, { responseType: "blob" });

const PurchaseOrderService = {
    getAllPurchaseOrders,
    getOrderById,
    createOrder,
    updateOrder,
    deleteOrder,
    generatePurchaseOrderPDF,
};

export default PurchaseOrderService;
