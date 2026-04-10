# 🗄️ Backend Architecture Documentation - Task Management System

This document details the backend architecture of the Task Management System, a secure RESTful API built with **Spring Boot 3** and **MySQL 8**. It serves as the operational backend for the Task Management React frontend.

---

## 🏗️ 1. Core Architecture: Layered MVC Pattern

The application follows a standard layered architecture for separation of concerns, testability, and maintainability.

### 📂 Package Structure

```
com.example.task_management_system/
├── controller/              # REST API endpoints
│   ├── AuthController.java
│   └── TaskController.java
├── service/                # Business logic layer
│   ├── UserDetailsServiceImpl.java
│   └── TaskService.java
├── repository/             # Data access layer
│   ├── UserRepository.java
│   └── TaskRepository.java
├── model/                  # JPA entities
│   ├── User.java
│   └── Task.java
├── dto/                    # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── JwtResponse.java
├── security/               # Security configuration
│   ├── JwtUtils.java
│   ├── JwtAuthTokenFilter.java
│   ├── AuthEntryPointJwt.java
│   └── WebSecurityConfig.java
└── exception/              # Custom exception handling
    └── ResourceNotFoundException.java
```

### 📂 Layer Responsibilities

- **Controller Layer**: Validates incoming DTOs, handles HTTP requests/responses, delegates to service layer
- **Service Layer**: Implements business logic, handles transactions, performs security checks
- **Repository Layer**: Extends `JpaRepository`, provides type-safe database queries
- **Model Layer**: JPA entities that map to database tables

---

## 🔒 2. Security & Authentication

### 🛡️ Spring Security Implementation

**JWT (JSON Web Token) Authentication:**

- **Stateless Design**: No server-side session storage
- **Bearer Token**: Carried in `Authorization` header for all requests
- **Token Expiration**: 24 hours (configurable via `app.jwtExpirationMs`)

**Authentication Flow:**

```
1. User submits credentials → AuthController.login()
2. Spring Security validates credentials
3. JwtUtils generates signed JWT token
4. Token + user details returned to client
5. Client includes token in all subsequent requests
6. JwtAuthTokenFilter validates token on every request
7. Security context populated with authenticated user
```

**Password Security:**

- **Hashing Algorithm**: BCrypt with 10 rounds
- **Password Encoder**: `BCryptPasswordEncoder` bean
- **No Plaintext Storage**: Passwords immediately hashed before saving

### 👤 Role-Based Access Control (RBAC)

**User Roles:**

- **`USER`** (default): Can manage their own tasks, standard operations
- **`ADMIN`**: Can manage users, view system-wide data (future enhancement)

**Role Assignment:**

```java
// During registration
user.setRole(signUpRequest.getRole() != null ? signUpRequest.getRole() : "USER");

// Returned in login response
return new JwtResponse(jwt, username, user.getRole());
```

---

## 💾 3. Data Schema & Persistence

### 🗃️ Database: MySQL 8.0

#### **Users Table**

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,         -- BCrypt hashed
    created_by VARCHAR(255),
    created_on DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    modified_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    role VARCHAR(50) DEFAULT 'USER'
);
```

**Indexes:**

- Primary Key: `id`
- Unique Index: `username`

#### **Tasks Table**

```sql
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'Open',
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    due_date DATETIME,
    created_by VARCHAR(255),
    created_on DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    modified_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Fields:**

- **status**: `Open`, `In Progress`, `In Review`, `On Hold`, `Done`, `Canceled`
- **priority**: `HIGH`, `MEDIUM`, `LOW`
- **due_date**: Optional deadline for task completion

### 🔄 JPA Entity Lifecycle Hooks

**Automatic Timestamp Management:**

```java
@PrePersist
protected void onCreate() {
    createdOn = LocalDateTime.now();
    modifiedOn = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    modifiedOn = LocalDateTime.now();
}
```

---

## ⚡ 4. Business Logic & Advanced Features

### 📝 Task Service Logic

#### **Task Creation**

```java
public Task createTask(Task task) {
    // Set defaults if not provided
    if (task.getCreatedBy() == null || task.getCreatedBy().isEmpty())
        task.setCreatedBy("SYSTEM");
    if (task.getStatus() == null || task.getStatus().isEmpty())
        task.setStatus("Open");
    if (task.getPriority() == null || task.getPriority().isEmpty())
        task.setPriority("MEDIUM");

    return taskRepository.save(task);
}
```

**Default Values:**

- `createdBy`: "SYSTEM" (if not provided)
- `status`: "Open"
- `priority`: "MEDIUM"

#### **Task Update (Patch-Style)**

```java
public Task updateTask(Long id, Task updatedTask) {
    return taskRepository.findById(id).map(task -> {
        // Only update fields that are provided (not null)
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus() != null ? updatedTask.getStatus() : task.getStatus());
        task.setPriority(updatedTask.getPriority() != null ? updatedTask.getPriority() : task.getPriority());
        task.setDueDate(updatedTask.getDueDate());
        task.setModifiedBy("SYSTEM");
        // modifiedOn auto-updated by @PreUpdate
        return taskRepository.save(task);
    }).orElseThrow(() -> new RuntimeException("Task not found with id " + id));
}
```

**Key Features:**

- Preserves existing values if update doesn't provide them
- Automatic `modifiedOn` timestamp via JPA hook
- Returns updated entity in response

### 🔍 Task Filtering & Pagination

```java
@GetMapping
public ResponseEntity<Page<Task>> getAllTasks(
    @RequestParam(required = false) String status,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedOn").descending());

    Page<Task> tasks = (status != null && !status.equalsIgnoreCase("All"))
        ? taskService.getTasksByStatus(status, pageable)
        : taskService.getAllTasks(pageable);

    return ResponseEntity.ok(tasks);
}
```

**Features:**

- Default sort: Most recently modified first
- Optional status filtering
- Pagination: 10 tasks per page (configurable)

---

## 🚀 5. API Reference

### 🔐 Authentication Endpoints

#### **POST /api/auth/register**

Create a new user account.

**Request:**

```json
{
  "username": "john.doe",
  "password": "SecurePass123",
  "role": "USER" // Optional, defaults to "USER"
}
```

**Response (200 OK):**

```json
{
  "message": "User registered successfully!"
}
```

**Error (400 Bad Request):**

```json
{
  "error": "Username is already taken!"
}
```

---

#### **POST /api/auth/login**

Authenticate user and receive JWT token.

**Request:**

```json
{
  "username": "john.doe",
  "password": "SecurePass123"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john.doe",
  "role": "USER"
}
```

---

### 📋 Task Endpoints

**All task endpoints require JWT authentication:**

```http
Authorization: Bearer <your_jwt_token>
```

#### **GET /api/tasks**

Retrieve paginated and filtered tasks.

**Query Parameters:**

- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `status` (optional): Filter by status ("Open", "In Progress", "Done", etc.)

**Example:**

```http
GET /api/tasks?page=0&size=10&status=Open
```

**Response (200 OK):**

```json
{
  "content": [
    {
      "id": 1,
      "title": "Deploy Backend API v2",
      "description": "Refactor authentication endpoints",
      "status": "In Progress",
      "priority": "HIGH",
      "dueDate": "2026-02-18T14:30:00",
      "createdBy": "john.doe",
      "createdOn": "2026-02-15T10:00:00",
      "modifiedBy": "SYSTEM",
      "modifiedOn": "2026-02-15T15:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

---

#### **POST /api/tasks**

Create a new task.

**Request:**

```json
{
  "title": "Database Migration",
  "description": "Migrate to PostgreSQL 15",
  "status": "Open",
  "priority": "MEDIUM",
  "dueDate": "2026-02-20T09:00:00"
}
```

**Response (200 OK):**

```json
{
  "id": 5,
  "title": "Database Migration",
  "status": "Open",
  "priority": "MEDIUM",
  "createdOn": "2026-02-15T23:30:00",
  ...
}
```

---

#### **PUT /api/tasks/{id}**

Update an existing task.

**Request:**

```json
{
  "status": "Done",
  "priority": "HIGH"
}
```

**Response (200 OK):**

```json
{
  "id": 5,
  "status": "Done",
  "priority": "HIGH",
  "modifiedOn": "2026-02-16T10:00:00",
  ...
}
```

---

#### **DELETE /api/tasks/{id}**

Delete a task permanently.

**Response (200 OK):**
No content.

---

## 🛠️ 6. Configuration & Setup

### 📝 Application Properties

**File:** `src/main/resources/application.properties`

```properties
# Application Name
spring.application.name=task-management-service

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/task_management_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=Root123456789@
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update  # Auto-create/update schema
spring.jpa.show-sql=true             # Log SQL queries
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Server
server.port=8080

# JWT Configuration
app.jwtSecret=SecretKeyToGenJWTsSecretKeyToGenJWTsSecretKeyToGenJWTs
app.jwtExpirationMs=86400000  # 24 hours in milliseconds
```

### 🚀 Running the Application

**Development Mode:**

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

**Production Build:**

```bash
mvnw.cmd clean package
java -jar target/task-management-service-0.0.1-SNAPSHOT.jar
```

---

## 🔧 7. Dependencies (pom.xml)

```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Database -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>

<!-- Lombok (Code Generation) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 🧪 8. Testing & Validation

### cURL Examples

**Register:**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

**Login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

**Create Task:**

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"API Testing","priority":"HIGH"}'
```

---

## 🚨 9. Error Handling & Status Codes

**Standard HTTP Codes:**

- **200 OK**: Successful operation
- **201 Created**: Resource created
- **400 Bad Request**: Invalid input/validation error
- **401 Unauthorized**: Missing/invalid JWT token
- **404 Not Found**: Resource doesn't exist
- **500 Internal Server Error**: Unexpected server error

**Example Error Response:**

```json
{
  "timestamp": "2026-02-15T23:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/tasks"
}
```

---

## 🎯 10. Performance & Optimization

**Connection Pooling:**

- HikariCP (default in Spring Boot)
- Automatically manages database connections

**Query Optimization:**

- Pagination reduces data transfer
- Indexes on `username` (unique) and primary keys

**Caching (Future Enhancement):**

- Spring Cache abstraction
- Redis integration for session management

---

**Task Management System Backend - Robust, Secure, and Scalable.**

**Developed by**: Mohamed Yasar A.
**Last Updated**: February 15, 2026
