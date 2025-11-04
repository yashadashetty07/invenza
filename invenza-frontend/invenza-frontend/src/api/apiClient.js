import api from "./axios";

export const auth = {
    login: (credentials) => api.post(`/auth/login`, credentials),
    register: (data) => api.post("/auth/register", data),
    logout: () => api.post(`/auth/logout`),
};

export const products = {
    getAll: () => api.get(`/api/products`),
    create: (data) => api.post(`/api/products`, data),
    update: (id, data) => api.put(`/api/products/${id}`, data),
    remove: (id) => api.delete(`/api/products/${id}`),
};

export const vendors = {
    getAll: (params) => api.get(`/api/vendors`, { params }),
    create: (data) => api.post(`/api/vendors`, data),
    update: (id, data) => api.put(`/api/vendors/${id}`, data),
    remove: (id) => api.delete(`/api/vendors/${id}`),
};

export const purchaseOrders = {
    getAll: () => api.get(`/api/purchase-orders`),
    create: (data) => api.post(`/api/purchase-orders`, data),
    update: (id, data) => api.put(`/api/purchase-orders/${id}`, data),
    remove: (id) => api.delete(`/api/purchase-orders/${id}`),
};

export const bills = {
    getAll: () => api.get(`/api/bills`),
    create: (data) => api.post(`/api/bills`, data),
};

export const quotations = {
    getAll: () => api.get(`/api/quotations`),
    create: (data) => api.post(`/api/quotations`, data),
};

export const users = {
    getAll: () => api.get(`/api/users`),
    create: (data) => api.post(`/api/users`, data),
    update: (id, data) => api.put(`/api/users/${id}`, data),
    remove: (id) => api.delete(`/api/users/${id}`),
};

export const pdf = {
    downloadBill: (id) => api.get(`/api/pdf/bill/${id}`, { responseType: `blob` }),
    downloadQuotation: (id) => api.get(`/api/pdf/quotation/${id}`, { responseType: `blob` }),
};

export default {
    auth,
    products,
    vendors,
    purchaseOrders,
    bills,
    quotations,
    users,
    pdf,
};
