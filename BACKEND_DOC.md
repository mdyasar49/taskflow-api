# 🗄️ Backend Architecture Documentation - TaskFlow Core Service

This document details the backend architecture of TaskFlow Core, a secure, RESTful microservice built with **Spring Boot 3** and **PostgreSQL**. Its primary purpose is to serve as the operational backend for the TaskFlow Pro frontend.

---

## 🏗️ 1. Core Architecture: Layered MVC Pattern

The application follows a standard layered architecture for separation of concerns and testability.

### 📂 Layers

- **Controller (`com.taskflow.controller`)**: The REST API surface. Validates incoming DTOs and delegates business logic.
- **Service (`com.taskflow.service`)**: The business logic processing zone. Handles transactions, security checks, and data transformation.
- **Repository (`com.taskflow.repository`)**: The data access layer. Uses Spring Data JPA for type-safe database queries.
- **Entity (`com.taskflow.model`)**: The core domain objects mapping to database tables.

---

## 🔒 2. Security & Authentication

### 🛡️ Spring Security Implementation

- **JWT (JSON Web Token)**: The service is stateless. Every request carries a Bearer token in the `Authorization` header.
- **`JwtRequestFilter`**: A custom filter chain that:
  1. Intercepts every HTTP request.
  2. Extracts and validates the JWT.
  3. Populates the `SecurityContext` with the authenticated user.
- **BCrypt**: Passwords are hashed with BCrypt (10 rounds) before storage.

### 👤 Role-Based Access Control (RBAC)

- **`ADMIN`**: Can manage users and view all system tasks.
- **`USER`**: Can manage their own tasks and view public operational data.

---

## 💾 3. Data Schema & Persistence

### 🗃️ Database: PostgreSQL (Production) / H2 (Dev)

- **`User` Table**: Stores authentication details.
  - `id` (Primary Key)
  - `username` (Unique Index)
  - `password` (Hashed)
  - `role` (Enum)
- **`Task` Table**: Stores operational tasks.
  - `id` (Primary Key)
  - `title`, `description`
  - `status` (Enum: OPEN, IN_PROGRESS, DONE, CANCELED)
  - `priority` (Enum: HIGH, MEDIUM, LOW)
  - `dueDate` (Timestamp)
  - `createdBy`, `lastModifiedBy` (Audit Fields)

---

## ⚡ 4. Advanced Features

### 🔄 Task Cloning Logic

In operational environments, a "Canceled" task is a historical record. It should not be simply "reopened".

- **The Solution**: The backend implements a specific `/clone` endpoint.
- **How it works**:
  1. Fetches the source task by ID.
  2. Creates a _new_ task entity.
  3. Copies relevant fields (Title, Description).
  4. Resets status to `OPEN`.
  5. Prefixes title with `(RESTARTED)` for traceability.

### 🕵️ Auditing

The system uses **Spring Data JPA Auditing** to automatically populate metadata:

- `@CreatedBy`: Who created the record.
- `@CreatedDate`: When it was created.
- `@LastModifiedBy`: Who last touched it.
- `@LastModifiedDate`: When it was last updated.

---

## 🚀 5. API Reference

| Method | Endpoint                | Description                                | Access |
| :----- | :---------------------- | :----------------------------------------- | :----- |
| POST   | `/api/auth/login`       | Returns JWT token                          | Public |
| GET    | `/api/tasks`            | List tasks (supports pagination/search)    | User   |
| POST   | `/api/tasks`            | Create new task                            | User   |
| PUT    | `/api/tasks/{id}`       | Update task status/details                 | User   |
| POST   | `/api/tasks/{id}/clone` | Clone a canceled task into a new open task | User   |

---

## 🛠️ 6. Setup & Deployment

1.  **Configure Database**:
    Edit `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/taskflow_db
    spring.datasource.username=postgres
    spring.datasource.password=secret
    ```
2.  **Build**: `./mvnw clean package`
3.  **Run**: `java -jar target/task-management-service-0.0.1-SNAPSHOT.jar`

---

**TaskFlow Core Service - Engineered for Reliability.**
