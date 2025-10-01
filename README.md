🚀 Invenza – Billing & Inventory Management System

A complete **Employee & Billing Management System** built with **Spring Boot (Backend)** and **React (Frontend in progress)**.
This project allows admins to manage vendors, products, bills, and quotations — all with secure authentication and automated PDF generation.

💡 Features

🔐 User Authentication & Security
- JWT-based session login
- Role-based access (Admin / User)
- Passwords stored securely using encryption

🏢 Employee & Department Management
- CRUD operations for employees and departments
- Assign employees to departments
- View employee details

📦 Product & Vendor Management
- CRUD operations for products and vendors
- Manage vendor details and products supplied

🧾 Billing & Quotation System
- Generate bills and quotations
- Auto PDF generation for bills and quotations
- Supports MRP Price, Selling Price, Discount, GST, and Grand Total
- Track bill and quotation history

🛠️ Technologies Used
_______________________________________________________________________
| Tech             | Description                                      |
|------------------|--------------------------------------------------|
| Java             | Core backend language                            |
| Spring Boot      | Backend framework                                |
| Spring Security  | Authentication & JWT-based security              |
| MySQL            | Database                                         |
| iText / Lowagie  | PDF generation                                   |
| React.js         | Frontend (in progress)                           |
| TailwindCSS      | Styling                                          |
| Axios            | API calls                                        |
_______________________________________________________________________

📁 Project Structure


invenza/
├── backend/
│   ├── src/main/java/com/invenza/
│   │   ├── controllers/
│   │   ├── entities/
│   │   ├── repositories/
│   │   ├── services/
│   │   └── security/
│   └── src/main/resources/
│       └── application.properties
├── frontend/
│   └── (React + TailwindCSS - in progress)
└── README.md

▶️ How to Run

1. Clone the repository:  

    git clone https://github.com/yashadashetty07/Invenza

2. Open backend in IntelliJ or any Java-supported IDE

3. Configure MySQL database in `application.properties`

4. Run Spring Boot application

5. Frontend (React) setup coming soon

🚀 Future Enhancements

* Complete **React frontend** with responsive UI
* Employee leave and attendance management
* Advanced analytics and reports
* Multi-branch support
* WhatsApp/email notifications for bills and quotations

📧 Contact
Developed by Yash Adashetty ([LinkedIn](https://www.linkedin.com/in/yashadashetty07))
Open for suggestions, feedback, and collaboration
Do you want me to do that?
```
