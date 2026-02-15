# 🏗️ Backend Architecture Documentation - TaskFlow API

This document details the internal architecture, security flows, and data management strategies of the TaskFlow API.

---

## 🏛️ 1. Architectural Pattern: Layered Design

The API follows the standard **N-Tier Architecture** to separate concerns and ensure scalability:

- **Controllers (`.controller`)**: The entry point for HTTP requests. Handles request mapping and response encapsulation.
- **Service Layer (`.service`)**: Contains the core business logic, such as task status cycling and password verification.
- **Repository Layer (`.repository`)**: Interfaces with the MySQL database using Spring Data JPA.
- **Models (`.model`)**: Entity classes representing the database schema.
- **DTOs (`.dto`)**: Data Transfer Objects for decoupled request/response communication.

---

## 🔒 2. Security & Authentication Flow

TaskFlow API implements **Stateless JWT Authentication**:

1.  **Identity Verification**: When a user logs in, the `AuthService` verifies credentials stored in the `UserRepository`.
2.  **Token Generation**: `JwtUtils` generates a cryptographically signed token.
3.  **Request Decoration**: The frontend includes this token in the `Authorization: Bearer <token>` header.
4.  **Filter Interception**: `AuthTokenFilter` intercepts every request, validates the token, and populates the `SecurityContextHolder`.
5.  **Access Control**: Endpoints are protected via `SecurityConfig`, ensuring only authenticated users can access task data.

---

## 🗄️ 3. Data Management

### 📊 Database Schema

The system manages two primary entities:

- **Users**:
  - `id`, `username`, `password` (BCrypt encoded).
- **Tasks**:
  - `id`, `title`, `description`, `status` (Open, In Progress, Done).
  - **Audit Metadata**: `createdBy`, `modifiedBy`, `createdOn`, `modifiedOn`.

### 🔄 Persistence Strategy

- **Hibernate DDL-Auto**: Set to `update` for seamless schema evolution during development.
- **JPA Specifications**: Enables dynamic filtering of tasks by status and title.
- **Auto-Timestamps**: Managed at the service/model level to ensure accurate audit trails.

---

## ⚙️ 4. Operational Workflow (How It Works)

### Task Lifecycle Processing:

1.  **Request Reception**: `TaskController` receives a `POST` or `PUT` request with task details.
2.  **Validation**: Ensures required fields like `title` are present.
3.  **Service Processing**:
    - For new tasks, the system assigns the initial status and sets the creator's identity.
    - For updates, it updates the "Modified By" timestamp and the current operational phase.
4.  **Persistence**: The `TaskRepository` saves the entity to MySQL.
5.  **Synchronization**: A `201 Created` or `200 OK` response is sent back to the frontend to trigger a UI refresh.

---

## 🚀 5. Performance Enhancements

- **Connection Pooling**: Uses HikariCP (default in Spring Boot) for efficient database connection management.
- **Pagination**: The `GET /api/tasks` endpoint utilizes `Pageable` to prevent large data loads, improving response times.
- **Eager vs Lazy Loading**: Optimized relationships between entities to avoid N+1 query problems.

---

**TaskFlow API Backend - Built for Reliability and Speed.**
