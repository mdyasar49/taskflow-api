# ⚙️ TaskFlow API - Robust Backend Orchestrator

TaskFlow API is the backbone of the TaskFlow Pro ecosystem. Built with **Spring Boot**, it provides a secure, scalable, and high-performance RESTful API for task management, user authentication, and data persistence.

![Framework](https://img.shields.io/badge/Framework-Spring%20Boot-6db33f?style=for-the-badge&logo=springboot)
![Database](https://img.shields.io/badge/Database-MySQL-4479a1?style=for-the-badge&logo=mysql)
![Security](https://img.shields.io/badge/Security-JWT%20%26%20Spring%20Security-black?style=for-the-badge)

---

## 🔥 Key Features

- **Secure Authentication**: JWT-based login and registration system with Spring Security.
- **Task Orchestration**: Full CRUD operations for managing system initiatives.
- **Persistent Storage**: Automated database schema management with Hibernate/JPA.
- **Audit Logging**: Tracks "Created By" and "Modified By" metadata for every task record.
- **Global Exception Handling**: Unified error response system for API consistency.

---

## 🛠️ Technology Stack

- **Java**: Version 17+
- **Framework**: Spring Boot 3.x
- **Build Tool**: Maven
- **Security**: Spring Security & JSON Web Token (JWT)
- **Persistence**: Spring Data JPA / Hibernate
- **Database**: MySQL 8.0+

---

## 🚀 Getting Started

### Prerequisites

- JDK 17 or higher
- MySQL Server
- Maven

### Installation & Setup

1. **Clone the repository**:

   ```bash
   git clone https://github.com/mdyasar49/taskflow-api.git
   cd taskflow-api
   ```

2. **Configure Database**:
   Open `src/main/resources/application.properties` and update your MySQL credentials:

   ```properties
   database.host=localhost
   database.dbname=task_management_db
   database.user=your_username
   database.password=your_password
   ```

3. **Build the project**:

   ```bash
   mvn clean install
   ```

4. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```
   The API will be available at `http://localhost:8080`.

---

## 📡 API Endpoints Summary

### Authentication

- `POST /api/auth/register` - Create a new user account.
- `POST /api/auth/login` - Authenticate and receive a JWT.

### Task Management

- `GET /api/tasks` - Retrieve tasks (Supports pagination and status filtering).
- `POST /api/tasks` - Deploy a new task initiative.
- `PUT /api/tasks/{id}` - Update existing task specifications.
- `DELETE /api/tasks/{id}` - Terminate a task record.

---

**Developed with ❤️ by [mdyasar49](https://github.com/mdyasar49)**
