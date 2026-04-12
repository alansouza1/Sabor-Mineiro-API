# Sabor Mineiro - Backend API ⚙️

A production-ready RESTful API for the Sabor Mineiro delivery system. Built with Java Spring Boot, this project demonstrates advanced architectural patterns, stateless security, and robust data persistence.

---

## 🧱 Tech Stack

*   **Java 17:** Modern Java features for performance and readability.
*   **Spring Boot 3.4:** Core framework for rapid, stable development.
*   **Spring Security:** Comprehensive security layer with JWT and RBAC.
*   **Spring Data JPA (Hibernate):** Object-Relational Mapping for PostgreSQL.
*   **PostgreSQL:** Reliable relational database for persistent storage.
*   **Lombok:** Reduced boilerplate for cleaner domain models.
*   **Maven:** Dependency management and build automation.

---

## 🏗️ Architecture

The project follows a **Multi-layered Clean Architecture** to ensure scalability and ease of testing:
*   **Controller Layer:** REST endpoints with request validation using JSR-303.
*   **Service Layer:** Business logic orchestration and transaction management.
*   **Collaborators:** Extracted responsibilities for specialized logic (Mappers, Calculators).
*   **Repository Layer:** Data access abstraction using JPA.
*   **DTO Layer:** Decoupled data transfer objects for API contracts.

---

## 🔐 Authentication & Authorization

*   **JWT Implementation:** Stateless authentication using cryptographically signed tokens.
*   **RBAC (Role-Based Access Control):** Granular permissions managed via custom Security Filter Chains.
    *   **ADMIN:** Full access to manage the catalog and orders.
    *   **DEMO:** Specialized role for reviewers with read-only dashboard access.
*   **Security Features:** BCrypt password hashing and global CORS configuration.

---

## 🔌 API Endpoints

### Authentication
*   `POST /api/auth/login` - Authenticate and receive a JWT token.
*   `POST /api/auth/signup` - Register a new client.

### Products
*   `GET /api/products` - List the active menu (Public).
*   `POST /api/products` - Create new dish (Admin only).
*   `PUT /api/products/{id}` - Update dish details (Admin only).
*   `DELETE /api/products/{id}` - Remove dish (Admin only).

### Orders
*   `POST /api/orders` - Place a new order (Supports Guest & Registered flows).
*   `GET /api/orders` - List all orders (Admin/Staff only).
*   `PATCH /api/orders/{id}/status` - Update order lifecycle (Staff only).

---

## ⚙️ Configuration

The application uses **Externalized Configuration** via environment variables.

**Database example:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sabormineiro
    username: your_username
    password: your_password
```

---

## 🧪 Testing

*   **Unit Testing:** Comprehensive tests for Services and Collaborators using JUnit 5 and Mockito.
*   **Integration Testing:** REST endpoint verification using MockMvc.
*   **Command:** `mvn test`

---

## 🚀 How to Run

1.  **Database:** Ensure PostgreSQL is running and a database named `sabormineiro` exists.
2.  **Start API:**
    ```bash
    mvn spring-boot:run
    ```
The API will be available at `http://localhost:8080/api`.

---

## 📁 Project Structure

```text
src/main/java/com/sabormineiro/api/
├── config/         # Security, JWT, and CORS configurations
├── controller/     # REST Controllers (API Endpoints)
├── dto/            # Data Transfer Objects & Validation
├── entity/         # Database Entities (JPA)
├── exception/      # Global Exception Handling
├── repository/     # Data Access Layers
└── service/        # Business Logic & Orchestrators
```

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.
