import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import AdminDashboard from "./pages/AdminDashboard";
import CashierDashboard from "./pages/CashierDashboard";
import Unauthorized from "./pages/Unauthorized";
import ProductList from "./pages/Products/ProductList";
import ProductForm from "./pages/Products/ProductForm";
import VendorList from "./pages/Vendors/VendorList";
import VendorForm from "./pages/Vendors/VendorForm";
import PurchaseOrderList from "./pages/PurchaseOrders/PurchaseOrderList";
import PurchaseOrderForm from "./pages/PurchaseOrders/PurchaseOrderForm";
import QuotationList from "./pages/Quotations/QuotationList";
import QuotationForm from "./pages/Quotations/QuotationForm";
import QuotationView from "./pages/Quotations/QuotationView";
import BillList from "./pages/Bills/BillList";
import BillForm from "./pages/Bills/BillForm";
import BillView from "./pages/Bills/BillView";
import ProtectedRoute from "./components/ProtectedRoute";
import ForgotPassword from "./components/auth/ForgetPassword";
import ChangePassword from "./components/auth/ChangePassword";
import ResetPassword from "./components/auth/ResetPassword";
import VerifyOtp from "./components/auth/VerifyOtp";
import BulkImportForm from "./pages/Products/ProductBulkImportForm";

function App() {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<Login />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/verify-otp" element={<VerifyOtp />} />
      <Route path="/reset-password" element={<ResetPassword />} />

      {/* Shared (Admin + Cashier) */}
      <Route element={<ProtectedRoute requiredRole={["admin", "cashier"]} />}>
        <Route path="/change-password" element={<ChangePassword />} />
        <Route path="/products" element={<ProductList />} />
        <Route path="/products/add" element={<ProductForm />} />
        <Route path="/products/import" element={<BulkImportForm />} />
        <Route path="/products/edit/:id" element={<ProductForm />} />
        <Route path="/bills" element={<BillList />} />
        <Route path="/bills/add" element={<BillForm />} />
        <Route path="/bills/edit/:id" element={<BillForm />} />
        <Route path="/bills/view/:id" element={<BillView />} />
        <Route path="/quotations" element={<QuotationList />} />
        <Route path="/quotations/add" element={<QuotationForm />} />
        <Route path="/quotations/edit/:id" element={<QuotationForm />} />
        <Route path="/quotations/view/:id" element={<QuotationView />} />
      </Route>

      {/* Admin-only */}
      <Route element={<ProtectedRoute requiredRole="admin" />}>
        <Route path="/admin" element={<AdminDashboard />} />
        <Route path="/vendors" element={<VendorList />} />
        <Route path="/vendors/add" element={<VendorForm />} />
        <Route path="/vendors/edit/:id" element={<VendorForm />} />
        <Route path="/purchase-orders" element={<PurchaseOrderList />} />
        <Route path="/purchase-orders/add" element={<PurchaseOrderForm />} />
        <Route path="/purchase-orders/edit/:id" element={<PurchaseOrderForm />} />
      </Route>

      {/* Cashier-only */}
      <Route element={<ProtectedRoute requiredRole="cashier" />}>
        <Route path="/cashier" element={<CashierDashboard />} />
      </Route>

      {/* Fallback */}
      <Route path="/unauthorized" element={<Unauthorized />} />
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>


  );
}

export default App;
