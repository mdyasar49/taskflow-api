# Task Management Service - Backend

A robust, enterprise-grade RESTful API built with **Spring Boot 3**, **Spring Security**, and **MySQL**. This service provides a secure and scalable backbone for the Task Management System, featuring JWT-based authentication and a comprehensive task lifecycle engine.

## 🏗️ Architecture: Layered MVC Pattern

The application follows a standard layered architecture to ensure separation of concerns, high testability, and maintainability.

- **Controller Layer**: Handles REST endpoints and DTO validation.
- **Service Layer**: Implements core business logic and transaction management.
- **Repository Layer**: Provides type-safe data access via Spring Data JPA.
- **Model Layer**: Persistent JPA entities mapping to MySQL schema.

## 🚀 Key Features

- **JWT Authentication**: Stateless session management with 24h token expiration.
- **Role-Based Access (RBAC)**: Secure endpoints based on `USER` and `ADMIN` roles.
- **Task Lifecycle Management**: Advanced CRUD operations with automatic timestamping (`createdOn`, `modifiedOn`).
- **Pagination & Sorting**: Optimized data retrieval for high-performance dashboard views.
- **Global Exception Handling**: Standardized error responses across all endpoints.
- **Interactive Documentation**: Built-in Swagger/OpenAPI support for API exploration.

## 🔒 Security Implementation

- **Password Hashing**: BCrypt algorithm with a strength of 10.
- **Access Control**: Stateless JWT filter on every request.
- **CORS Support**: Pre-configured for seamless frontend integration.

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.4.2
- **Security**: Spring Security 6, JJWT (io.jsonwebtoken)
- **DB & Persistence**: MySQL 8.0, Spring Data JPA, Hibernate
- **API Documentation**: Springdoc OpenAPI v2.3
- **Tooling**: Maven, Lombok

## 📁 Project Structure

```text
com.example.task_management_system/
├── controller/    # Auth and Task REST Controllers
├── service/       # Business logic implementations
├── repository/    # JPA repositories for Data Access
├── model/         # User and Task entities
├── dto/           # Request/Response payloads
├── security/      # JWT and Security configurations
└── exception/     # Custom exceptions and Global Handler
```

## ⚙️ Getting Started

### Prerequisites

- JDK 17+
- Maven
- MySQL Server 8.0

### Database Setup

1. Create a database: `CREATE DATABASE task_management_db;`
2. Update `src/main/resources/application.properties` with your credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/task_management_db
   spring.datasource.username=your_user
   spring.datasource.password=your_password
   ```

### Execution

```bash
# Using Maven Wrapper
mvnw spring-boot:run
```

## 📖 API Reference

Access the interactive Swagger UI at: `http://localhost:8080/swagger-ui/index.html`

### Primary Endpoints

- `POST /api/auth/register` - User signup
- `POST /api/auth/login` - User authentication (returns JWT)
- `GET /api/tasks` - Fetch paginated tasks (Requires Token)
- `POST /api/tasks` - Create new task (Requires Token)

---

_Powering efficient task orchestration._
