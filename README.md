# Sabor Mineiro - Backend API ⚙️

A production-like RESTful API powering the Sabor Mineiro delivery platform. Built with Java Spring Boot, this project demonstrates advanced architectural patterns, stateless security, and robust data persistence.

---

## 🌐 Live Integration

The API is publicly available and integrated with the frontend application:

Link: https://sabormineiro.alansouza.tech

---

## 🚀 Live Documentation (Swagger)
The API is fully documented using **OpenAPI 3 / Swagger**.
*   **Interactive UI:** `http://localhost:8080/api/swagger-ui/index.html`
*   **API Specs (JSON):** `http://localhost:8080/api/v3/api-docs`

> **Pro Tip:** Use the **"Authorize"** button in Swagger UI to paste your JWT token and test protected endpoints directly.

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
*   **Controller Layer:** REST endpoints with request validation using JSR-303 and Swagger annotations.
*   **Service Layer:** Business logic orchestration and transaction management.
*   **Collaborators:** Extracted responsibilities for specialized logic (Mappers, Calculators, Customer resolution).
*   **Repository Layer:** Data access abstraction using JPA.
*   **DTO Layer:** Decoupled data transfer objects for API contracts.

---

## 🔐 Authentication & Authorization

*   **JWT Implementation:** Stateless authentication using cryptographically signed tokens (HS256).
*   **RBAC (Role-Based Access Control):** Granular permissions managed through Spring Security configuration and JWT filters.
    *   **ADMIN:** Full access to manage the catalog and orders.
    *   **DEMO:** Specialized role for reviewers with read-only dashboard access. 
    *   **Brute-Force Protection:** Implements IP-based **Rate Limiting** on the login endpoint using **Bucket4j**. This prevents automated attacks and preserves server CPU by blocking excessive failed attempts (10 requests per minute).
    *   **Session Isolation:** Implements a `visitor_id` strategy.
 Demo users only see orders created in their specific browser session (identified via `X-Visitor-Id` header).

> **🔒 Privacy Notice:** This is a public demo. The system implements **Session Isolation** to prevent users from seeing each other's data, but users are still encouraged to use fictional information.

---

## 🔌 API Endpoints

### Authentication
*   `POST /api/auth/login` - Authenticate and receive a JWT token.
*   `POST /api/auth/signup` - Register a new client.

### Products
*   `GET /api/products` - List the active menu (Public).
*   `POST /api/products` - Create new dish (ADMIN role only).
*   `PUT /api/products/{id}` - Update dish details (ADMIN role only).
*   `DELETE /api/products/{id}` - Remove dish (ADMIN role only).

### Orders
*   `POST /api/orders` - Place a new order (Supports both guest checkout and authenticated users).
*   `GET /api/orders` - List all orders (ADMIN role only, filtered by visitor if DEMO).
*   `PATCH /api/orders/{id}/status` - Update order lifecycle (ADMIN role only).

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
├── config/         # Security, JWT, CORS, and Swagger configurations
├── controller/     # REST Controllers (API Endpoints)
├── dto/            # Data Transfer Objects & Validation
├── entity/         # Database Entities (JPA)
├── exception/      # Global Exception Handling
├── repository/     # Data Access Layers
└── service/        # Business Logic & Orchestrators
```

---

## 🔗 Frontend Repository

https://github.com/alansouza1/sabor-mineiro-app

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.
