# 🚀 Task Management Service - Backend

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)

## 🌟 Overview

The **Task Management Service** is a robust REST API designed to power the "Cyber Intelligence" frontend. It serves as the operational contract for task tracking, user authentication, and system auditing.

## 🛠️ Key Architectural Decisions

### 1. **Modular Architecture**

- Organized by domain (Auth, Task, User) rather than technical type (Controller/Service).
- Ensures cleaner separation of concerns and easier microservices extraction if needed.

### 2. **Authentication & Security**

- **JWT Implementation**: Custom `JwtRequestFilter` for parsing Bearer tokens.
- **Role-Based Access Control (RBAC)**: Supports `ADMIN`, `MANAGER`, and `USER` roles.
- **Password Hashing**: BCrypt with salt rounds for enhanced security.

### 3. **Data Resilience & Auditing**

- **Cloning Logic**: Instead of "Reopening" a Canceled task, the system creates a _new cloned entity_ via `/api/tasks/clone/{id}`.
- **Historical Tracking**: Maintains original failure records while creating fresh operational tasks.
- **JPA Auditing**: Automatically populates `createdBy`, `createdDate`, `lastModifiedBy`, and `lastModifiedDate`.

## ⚡ API Endpoints

### **Authentication**

- `POST /api/auth/register`: Create new user accounts (Admin/User).
- `POST /api/auth/login`: Issue JWT access tokens.

### **Task Management**

- `GET /api/tasks`: Retrieve all tasks with pagination (`page`, `size`) and filtering (`status`, `search`).
- `POST /api/tasks`: Create new operational tasks.
- `PUT /api/tasks/{id}`: Update task details or status.
- `DELETE /api/tasks/{id}`: Soft delete tasks (audit compliant).

### **User Profile**

- `GET /api/users/me`: Retrieve current user details.
- `PUT /api/users/me`: Update profile information.

## 🚀 Getting Started

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/task-management-service.git
    ```
2.  **Database Configuration**:
    Update `src/main/resources/application.properties` with your PostgreSQL credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
    spring.datasource.username=postgres
    spring.datasource.password=yourpassword
    ```
3.  **Run Application**:
    ```bash
    ./mvnw spring-boot:run
    ```
4.  **Swagger UI**:
    Access API documentation at `http://localhost:8080/swagger-ui.html`.

---

**Developed by**: Mohamed Yasar A.
