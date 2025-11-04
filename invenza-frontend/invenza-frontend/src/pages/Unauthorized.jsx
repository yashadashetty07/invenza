import { useNavigate } from "react-router-dom";


const Unauthorized = () => {
  const navigate = useNavigate();
  return (
    <div style={styles.container} >
      <div style={styles.card}>
        <h1 style={styles.heading}>Access Denied </h1>
        <p style={styles.message}>You do not have permission to view this page.</p>
        <button style={styles.button} onClick={() => navigate(-1)}>Go Back</button>
      </div>

    </div >
  );
}
const styles = {
  container: {
    height: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: "linear-gradient(to right, #2b5876, #4e4376)",
  },
  card: {
    backgroundColor: "#fff",
    padding: "2rem 3rem",
    borderRadius: "1rem",
    boxShadow: "0 4px 12px rgba(0,0,0,0.2)",
    textAlign: "center",
    maxWidth: "400px",
  },
  heading: {
    fontSize: "1.8rem",
    color: "#e63946",
  },
  message: {
    fontSize: "1rem",
    color: "#333",
    margin: "1rem 0 2rem",
  },
  button: {
    backgroundColor: "#2b5876",
    color: "#fff",
    border: "none",
    padding: "0.8rem 1.5rem",
    borderRadius: "8px",
    cursor: "pointer",
    fontWeight: "bold",
  },
};

export default Unauthorized;