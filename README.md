# 🔧 Task Management System - Backend (Spring Boot)

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring)
![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql)

## 🌟 Overview

The **Task Management System Backend** is a RESTful API built with Spring Boot, designed for high-availability enterprise task management. It features JWT authentication, audit trails, and data resilience patterns.

## ⚡ Key Features

### 1. **Layered Architecture**

```
src/main/java/com/example/task_management_system/
├── controller/       # REST endpoints
├── service/         # Business logic
├── repository/      # Data access layer
├── model/          # JPA entities
├── dto/            # Data transfer objects
├── security/       # JWT & authentication
└── exception/      # Custom exception handling
```

### 2. **Security**

- **JWT Authentication**: Stateless token-based auth
- **Password Encryption**: BCrypt hashing
- **CORS Configuration**: Cross-origin support for React frontend
- **Role-Based Access**: User roles (USER, ADMIN)

### 3. **Data Integrity**

- **Audit Fields**: `createdBy`, `createdOn`, `modifiedBy`, `modifiedOn`
- **Automatic Timestamps**: JPA lifecycle hooks (`@PrePersist`, `@PreUpdate`)
- **Default Values**: Status = "Open", Priority = "MEDIUM"

### 4. **API Design**

- **RESTful Conventions**: Standard HTTP methods (GET, POST, PUT, DELETE)
- **Pagination Support**: Page-based task retrieval
- **Status Filtering**: Filter tasks by operational phase
- **Sorting**: Default sort by `modifiedOn` descending

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MySQL 8.0 (Production) / H2 (Development)
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security + JWT
- **Build Tool**: Maven
- **Utilities**: Lombok (reducing boilerplate)

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+ (or use H2 for testing)

### Installation

1. **Clone the repository**:

   ```bash
   git clone <repository-url>
   cd task-management-service
   ```

2. **Configure Database**:
   Edit `src/main/resources/application.properties`:

   ```properties
   # MySQL Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/task_management_db
   spring.datasource.username=root
   spring.datasource.password=YourPassword

   # JPA/Hibernate
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   # JWT Configuration
   app.jwtSecret=YourSecretKeyHere
   app.jwtExpirationMs=86400000
   ```

3. **Initialize Database**:
   Run `src/main/resources/schema.sql` to create tables:

   ```bash
   mysql -u root -p task_management_db < schema.sql
   ```

4. **Run the application**:

   ```bash
   # Windows
   mvnw.cmd spring-boot:run

   # Linux/Mac
   ./mvnw spring-boot:run
   ```

   Application will start on `http://localhost:8080`

5. **Build JAR**:
   ```bash
   mvnw.cmd clean package
   # Output: target/task-management-service-0.0.1-SNAPSHOT.jar
   ```

## 📂 Project Structure

```
task-management-service/
├── src/main/java/com/example/task_management_system/
│   ├── controller/
│   │   ├── AuthController.java      # Login & Register
│   │   └── TaskController.java      # Task CRUD operations
│   ├── service/
│   │   ├── UserDetailsServiceImpl.java
│   │   └── TaskService.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   └── TaskRepository.java
│   ├── model/
│   │   ├── User.java               # User entity
│   │   └── Task.java               # Task entity
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── JwtResponse.java
│   ├── security/
│   │   ├── JwtUtils.java           # JWT token generation
│   │   ├── JwtAuthTokenFilter.java # JWT validation filter
│   │   ├── AuthEntryPointJwt.java  # 401 handler
│   │   └── WebSecurityConfig.java  # Security configuration
│   └── TaskManagementServiceApplication.java
├── src/main/resources/
│   ├── application.properties      # App configuration
│   └── schema.sql                 # Database schema
└── pom.xml                        # Maven dependencies
```

## 📊 Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_by VARCHAR(255),
    created_on DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    modified_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    role VARCHAR(50) DEFAULT 'USER'
);
```

### Tasks Table

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

## 🔌 API Endpoints

### Authentication

#### Register

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "role": "ADMIN"
}
```

**Response:**

```json
{
  "message": "User registered successfully!"
}
```

#### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "admin",
  "role": "ADMIN"
}
```

### Task Management

#### Get All Tasks (with pagination & filtering)

```http
GET /api/tasks?page=0&size=10&status=Open
Authorization: Bearer <token>
```

**Response:**

```json
{
  "content": [
    {
      "id": 1,
      "title": "Deploy Backend API v2",
      "description": "Refactor key auth endpoints",
      "status": "In Progress",
      "priority": "HIGH",
      "dueDate": "2026-02-18T14:30:00",
      "createdBy": "admin",
      "createdOn": "2026-02-15T10:00:00",
      "modifiedBy": "admin",
      "modifiedOn": "2026-02-15T15:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

#### Create Task

```http
POST /api/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Security Audit Level 1",
  "description": "Initial penetration testing",
  "status": "Open",
  "priority": "HIGH",
  "dueDate": "2026-02-20T12:00:00"
}
```

**Response:** Created task object

#### Update Task

```http
PUT /api/tasks/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Title",
  "status": "Done",
  "priority": "MEDIUM"
}
```

**Response:** Updated task object

#### Delete Task

```http
DELETE /api/tasks/{id}
Authorization: Bearer <token>
```

**Response:** 200 OK

## 🔐 Security Configuration

### JWT Token Structure

```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "admin",
  "iat": 1708012800,
  "exp": 1708099200
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### Password Hashing

- **Algorithm**: BCrypt
- **Strength**: 10 rounds (default)

### CORS Configuration

```java
@CrossOrigin(origins = "*", maxAge = 3600)
```

Allows frontend (React) at any origin to access the API.

## 🛡️ Error Handling

### Standard Error Response

```json
{
  "timestamp": "2026-02-15T23:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Username is already taken!",
  "path": "/api/auth/register"
}
```

### HTTP Status Codes

- **200**: Success
- **201**: Created
- **400**: Bad Request (validation error)
- **401**: Unauthorized (invalid/missing JWT)
- **404**: Not Found
- **500**: Internal Server Error

## 📝 Business Logic

### Task Creation (TaskService.java)

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

### Task Update (Patch-Style)

```java
public Task updateTask(Long id, Task updatedTask) {
    return taskRepository.findById(id).map(task -> {
        // Only update fields that are provided
        if (updatedTask.getTitle() != null)
            task.setTitle(updatedTask.getTitle());
        if (updatedTask.getStatus() != null)
            task.setStatus(updatedTask.getStatus());
        // ... other fields

        task.setModifiedBy("SYSTEM");
        // modifiedOn is auto-updated by @PreUpdate
        return taskRepository.save(task);
    }).orElseThrow(() -> new RuntimeException("Task not found with id " + id));
}
```

## 🧪 Testing

### Manual API Testing (Postman/cURL)

**1. Register User:**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123","role":"USER"}'
```

**2. Login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

**3. Create Task:**

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Task","priority":"HIGH"}'
```

### Database Verification

```sql
-- Check if user was created
SELECT * FROM users WHERE username = 'testuser';

-- Check if task was created
SELECT * FROM tasks WHERE created_by = 'testuser';
```

## 🔧 Configuration Options

### Application Properties

```properties
# Server Configuration
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/task_management_db
spring.datasource.username=root
spring.datasource.password=Root123456789@

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update  # Options: create, update, validate, none
spring.jpa.show-sql=true             # Show SQL queries in console
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT
app.jwtSecret=SecretKeyToGenJWTsSecretKeyToGenJWTsSecretKeyToGenJWTs
app.jwtExpirationMs=86400000  # 24 hours
```

## 📦 Dependencies (pom.xml)

```xml
<dependencies>
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

    <!-- MySQL Connector -->
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

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

## 🚨 Common Issues & Solutions

### Issue: "Table doesn't exist"

**Solution:** Ensure `spring.jpa.hibernate.ddl-auto=update` and restart the application.

### Issue: "Access Denied for User"

**Solution:** Check MySQL credentials in `application.properties`.

### Issue: "401 Unauthorized on all requests"

**Solution:** Verify JWT token is valid and not expired.

### Issue: "CORS Error from Frontend"

**Solution:** Ensure `@CrossOrigin(origins = "*")` is present on controllers.

## 🎯 Performance Optimizations

1. **Connection Pooling**: HikariCP (default in Spring Boot)
2. **Query Optimization**: Pagination reduces data transfer
3. **Index Creation**: Automatic indexes on primary/foreign keys
4. **Lazy Loading**: Hibernate fetches data on-demand

## 🌐 Deployment

### Build Production JAR

```bash
mvnw.cmd clean package -DskipTests
```

### Run JAR

```bash
java -jar target/task-management-service-0.0.1-SNAPSHOT.jar
```

### Docker Deployment

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📊 Monitoring

### Actuator Endpoints (if enabled)

- `/actuator/health` - Application health status
- `/actuator/metrics` - Performance metrics
- `/actuator/info` - Application info

## 🤝 Contributing

1. Create a feature branch
2. Write clean, documented code
3. Test all endpoints
4. Submit pull request

## 📄 License

MIT License

Copyright (c) 2026 Mohamed Yasar A.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

---

**Developed by**: Mohamed Yasar A.
**Last Updated**: February 15, 2026
