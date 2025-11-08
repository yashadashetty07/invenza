import api from "../api/axios";
const BASE_URL = "/vendor";

class VendorService {
    getAllVendors() {
        return api.get(BASE_URL);
    }

    getVendorById(id) {
        return api.get(`${BASE_URL}/${id}`);
    }

    createVendor(vendor) {
        return api.post(BASE_URL, vendor);
    }

    updateVendor(id, vendor) {
        return api.put(`${BASE_URL}/${id}`, vendor);
    }

    deleteVendor(id) {
        return api.delete(`${BASE_URL}/${id}`);
    }
}

export default new VendorService();
