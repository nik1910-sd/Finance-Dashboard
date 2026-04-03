# Finance Dashboard Backend (Spring Boot)

A RESTful backend for a finance dashboard system with JWT authentication, role-based access control, and CRUD APIs for financial records. Includes an H2 in-memory database for fast local development.

## Tech Stack

- **Java**: 17  
- **Framework**: Spring Boot 3 (Web, Security, Validation)
- **Persistence**: Spring Data JPA (Hibernate)
- **Database**: H2 (in-memory)
- **Auth**: JWT (Bearer tokens) via `jjwt`
- **Build**: Maven

## Key Features

- **Authentication**: `POST /api/auth/login` returns a JWT
- **Role-based access control** (method-level authorization)
- **Users (Admin only)**: create and list users
- **Financial Records**: CRUD (admin for write; admin+analyst for read)
- **Dashboard Summary**: totals + category breakdown + recent activity
- **Centralized error handling** via `@ControllerAdvice`

## Project Structure

```
src/main/java/com/finance/dashboard/
  controller/   # REST endpoints
  service/      # business logic
  repository/   # JPA repositories
  entity/       # JPA entities + enums
  dto/          # request/response DTOs (login, jwt response)
  security/     # JWT filter + security config
  exception/    # global exception handler
```

## Getting Started (Local)

### Prerequisites

- **Java 17**
- **Maven** (or use the Maven wrapper if you add one later)

### Run the application

From the project root:

```bash
mvn spring-boot:run
```

Default server: `http://localhost:8080`

## Authentication

All endpoints under `/api/**` require authentication **except**:

- `/api/auth/**` (login)
- `/h2-console/**` (dev-only database console)

Send the JWT in every request:

```
Authorization: Bearer <token>
```

### Default users (auto-seeded)

On startup, the app creates these users if they don’t exist:

- **admin / admin** → `ROLE_ADMIN`
- **analyst / analyst** → `ROLE_ANALYST`
- **viewer / viewer** → `ROLE_VIEWER`

## API Overview

Base path: `/api`

### Auth

- **POST** `/api/auth/login`

Request body:

```json
{ "username": "admin", "password": "admin" }
```

Response:

```json
{ "token": "<jwt>", "type": "Bearer" }
```

### Users (Admin only)

- **POST** `/api/users`
- **GET** `/api/users`

### Financial Records

- **POST** `/api/records` (Admin only)
- **PUT** `/api/records/{id}` (Admin only)
- **DELETE** `/api/records/{id}` (Admin only)
- **GET** `/api/records` (Admin or Analyst)
- **GET** `/api/records/{id}` (Admin or Analyst)

### Dashboard

- **GET** `/api/dashboard/summary` (Admin, Analyst, Viewer)

For full request/response examples, see `API.md`.

## Configuration

Config file: `src/main/resources/application.properties`

Notable settings:

- **H2**:
  - `spring.datasource.url=jdbc:h2:mem:financedb`
  - H2 console: `/h2-console`
- **JWT**:
  - `jwt.secret=...`
  - `jwt.expirationMs=86400000`

## Database (H2 Console)

The H2 console is enabled for development:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:financedb`
- Username: `sa`
- Password: `password`

## Testing

```bash
mvn test
```

## Notes / Trade-offs

- The default database is **in-memory** (data resets on restart). For production, switch to a persistent DB (e.g., Postgres/MySQL) and use migrations (Flyway/Liquibase).
- JWT is **stateless**; token revocation/rotation is not implemented by default.

