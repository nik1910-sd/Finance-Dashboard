# Finance Dashboard API Documentation

Backend: Spring Boot 3 (Java 17)  
Base path: `/api`  
Default server: `http://localhost:8080`

## Authentication

This API uses **JWT Bearer tokens**.

- **Login endpoint**: `POST /api/auth/login` (no auth required)
- **Authenticated endpoints**: everything else under `/api/**` requires a valid token
- **Auth header**:

```
Authorization: Bearer <jwt>
```

### Roles

The system uses the following roles:

- `ROLE_ADMIN`
- `ROLE_ANALYST`
- `ROLE_VIEWER`

Many endpoints use method-level authorization:

- **Admin-only**: create/update/delete records; create/list users
- **Admin + Analyst**: read records
- **Admin + Analyst + Viewer**: dashboard summary

### Seed (default) users (dev)

On app startup, three users are created if missing:

- `admin` / `admin` → `ROLE_ADMIN`
- `analyst` / `analyst` → `ROLE_ANALYST`
- `viewer` / `viewer` → `ROLE_VIEWER`

## Common Response Codes & Error Format

This project returns JSON error bodies like:

```json
{ "error": "message" }
```

Or, for some cases:

```json
{ "error": "Method Not Allowed", "details": "..." }
```

Validation failures (e.g. missing login fields) return a JSON object keyed by field name:

```json
{ "username": "Username cannot be blank" }
```

Typical status codes:

- `200 OK`: successful reads/writes (except deletes)
- `204 No Content`: successful delete
- `400 Bad Request`: validation failures, illegal arguments (e.g. duplicate username)
- `401 Unauthorized`: invalid credentials or auth failure (see note below)
- `403 Forbidden`: authenticated but not enough permissions
- `404 Not Found`: entity not found (e.g. record not found)
- `405 Method Not Allowed`: wrong HTTP method
- `500 Internal Server Error`: unexpected errors

**Note on invalid/expired JWTs**: the JWT filter swallows token parsing errors and continues the chain; downstream security may respond with `401` when the request requires authentication.

## Data Models (JSON)

### `LoginRequest`

```json
{
  "username": "admin",
  "password": "admin"
}
```

### `JwtResponse`

```json
{
  "token": "<jwt>",
  "type": "Bearer"
}
```

### `User`

When creating a user, you send a `User` object. Password is stored encoded.

```json
{
  "id": 1,
  "username": "admin",
  "password": "admin",
  "role": "ROLE_ADMIN",
  "active": true
}
```

### `FinancialRecord`

```json
{
  "id": 123,
  "amount": 1000.50,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-03T10:15:30",
  "description": "March salary",
  "createdBy": {
    "id": 1,
    "username": "admin",
    "role": "ROLE_ADMIN",
    "active": true
  }
}
```

Enums:

- `RecordType`: `INCOME` | `EXPENSE`

## Endpoints

### Auth

#### `POST /api/auth/login`

Authenticate and receive a JWT.

- **Auth**: none
- **Body**: `LoginRequest`
- **Response**: `JwtResponse`

Example:

```bash
curl -s -X POST "http://localhost:8080/api/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin\"}"
```

### Users (Admin only)

All `/api/users/**` endpoints require `ROLE_ADMIN`.

#### `POST /api/users`

Create a new user.

- **Auth**: Bearer JWT (`ROLE_ADMIN`)
- **Body**: `User`
- **Response**: created `User` (as persisted)

Example:

```bash
curl -s -X POST "http://localhost:8080/api/users" ^
  -H "Authorization: Bearer <jwt>" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"newuser\",\"password\":\"secret\",\"role\":\"ROLE_VIEWER\",\"active\":true}"
```

#### `GET /api/users`

List all users.

- **Auth**: Bearer JWT (`ROLE_ADMIN`)
- **Response**: `User[]`

### Financial Records

Base path: `/api/records`

#### `POST /api/records` (Admin only)

Create a financial record.

- **Auth**: Bearer JWT (`ROLE_ADMIN`)
- **Body**: `FinancialRecord`
- **Response**: created `FinancialRecord`

#### `PUT /api/records/{id}` (Admin only)

Update an existing record by id.

- **Auth**: Bearer JWT (`ROLE_ADMIN`)
- **Path params**:
  - `id` (number)
- **Body**: `FinancialRecord` (fields copied onto existing record)
- **Response**: updated `FinancialRecord`

#### `DELETE /api/records/{id}` (Admin only)

Delete an existing record by id.

- **Auth**: Bearer JWT (`ROLE_ADMIN`)
- **Response**: `204 No Content`

#### `GET /api/records` (Admin or Analyst)

List all records.

- **Auth**: Bearer JWT (`ROLE_ADMIN` or `ROLE_ANALYST`)
- **Response**: `FinancialRecord[]`

#### `GET /api/records/{id}` (Admin or Analyst)

Get a record by id.

- **Auth**: Bearer JWT (`ROLE_ADMIN` or `ROLE_ANALYST`)
- **Response**: `FinancialRecord`

### Dashboard

Base path: `/api/dashboard`

#### `GET /api/dashboard/summary` (Admin, Analyst, or Viewer)

Returns aggregated metrics over all records.

- **Auth**: Bearer JWT (`ROLE_ADMIN` or `ROLE_ANALYST` or `ROLE_VIEWER`)
- **Response**: `Map<String, Object>` with the following keys:
  - `totalIncome` (number / decimal)
  - `totalExpenses` (number / decimal)
  - `netBalance` (number / decimal)
  - `categoryTotals` (object: `{ [category: string]: number }`) for **expense** records only
  - `recentActivity` (`FinancialRecord[]`) sorted by `date` desc, limited to 5

## Development / Debugging

### H2 console

- **Path**: `/h2-console`
- **Security**: permitted without authentication
- **JDBC URL**: `jdbc:h2:mem:financedb`

