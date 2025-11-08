import api from "../api/axios";
const getAllPurchaseOrders = () => api.get("/purchase-orders");
const getPurchaseOrderById = (id) => api.get(`/purchase-orders/${id}`);
const createPurchaseOrder = (purchaseOrder) => api.post("/purchase-orders", purchaseOrder);
const updatePurchaseOrder = (id, purchaseOrder) => api.put(`/purchase-orders/${id}`, purchaseOrder);
const deletePurchaseOrder = (id) => api.delete(`/purchase-orders/${id}`);
const generatePurchaseOrderPDF = (id) => api.get(`/pdf/purchase-order/${id}`, { responseType: "blob" });

const PurchaseOrderService = {
    getAllPurchaseOrders,
    getPurchaseOrderById,
    createPurchaseOrder,
    updatePurchaseOrder,
    deletePurchaseOrder,
    generatePurchaseOrderPDF,
};

export default PurchaseOrderService;
