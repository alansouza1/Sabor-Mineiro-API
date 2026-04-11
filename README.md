# Sabor Mineiro - Backend API

Production-ready REST API for the Sabor Mineiro delivery application, built with Java Spring Boot and PostgreSQL.

## 🚀 Technologies
- Java 17
- Spring Boot 3.4.2
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

## 📋 Requirements
- JDK 17 or higher
- PostgreSQL database named `sabormineiro`
- Maven

## ⚙️ Configuration
Update `src/main/resources/application.yml` with your PostgreSQL credentials:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sabormineiro
    username: your_username
    password: your_password
```

## 🛠️ How to Run
1. Ensure PostgreSQL is running and the database `sabormineiro` is created.
2. Navigate to the `sabor-mineiro-api` directory.
3. Run the application:
```bash
mvn spring-boot:run
```
The API will be available at `http://localhost:8080/api`.

## 🔌 API Endpoints

### Products
- `GET /api/products` - List all products (Seeded automatically from `import.sql`).

### Orders
- `POST /api/orders` - Create a new order.
- `GET /api/orders` - List all orders.
- `PATCH /api/orders/{id}/status` - Update order status.

## 🧪 Testing
Run unit tests:
```bash
mvn test
```
