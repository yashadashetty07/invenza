import Navbar from "./Navbar";
import Sidebar from "./Sidebar";

const Layout = ({ children }) => {
  return (
    <div
      className="d-flex flex-column"
      style={{
        minHeight: "100vh",
        backgroundColor: "#f8f9fa",
      }}
    >
      <Navbar />
      <div className="d-flex flex-grow-1" style={{ overflow: "hidden" }}>
        <Sidebar />
        <main
          className="flex-grow-1 px-4 py-4"
          style={{
            backgroundColor: "#ffffff",
            borderLeft: "1px solid #dee2e6",
            overflowY: "auto",
          }}
        >
          {children}
        </main>
      </div>
    </div>
  );
};

export default Layout;
