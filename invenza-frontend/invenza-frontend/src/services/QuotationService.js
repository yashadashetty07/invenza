import api from "../api/axios";
const getAllQuotations = () => api.get("/quotations");
const getQuotationById = (id) => api.get(`/quotations/${id}`);
const createQuotation = (quotation) => api.post("/quotations", quotation);
const updateQuotation = (id, quotation) => api.put(`/quotations/${id}`, quotation);
const deleteQuotation = (id) => api.delete(`/quotations/${id}`);
const generatePDF = (id) => api.get(`/pdf/quotations/${id}`, { responseType: "blob" });

const QuotationService = {
    getAllQuotations,
    getQuotationById,
    createQuotation,
    updateQuotation,
    deleteQuotation,
    generatePDF,
};

export default QuotationService;
