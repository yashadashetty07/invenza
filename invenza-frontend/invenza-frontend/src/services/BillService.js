import api from "../api/axios";
const getAllBills = () => api.get("/bills");

const getBillById = (id) => api.get(`/bills/${id}`);

const createBill = (bill) => api.post("/bills", bill);

const updateBill = (id, bill) => api.put(`/bills/${id}`, bill);

const deleteBill = (id) => api.delete(`/bills/${id}`);

const generatePDF = (id) =>
    api.get(`/pdf/bills/${id}`, { responseType: "blob" });

const BillService = {
    getAllBills,
    getBillById,
    createBill,
    updateBill,
    deleteBill,
    generatePDF,
};

export default BillService;
