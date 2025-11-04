# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CAB System (Change Advisory Board Automation) - a system for managing IT infrastructure changes through RFC (Request for Change) workflows. The system consists of a Java Spring Boot backend, React frontend, PostgreSQL database, and Keycloak authentication.

## Architecture

### Technology Stack
- **Backend**: Java 21, Spring Boot 3.5.5, Gradle
- **Frontend**: React 19, TypeScript 5.8, Vite 7, TailwindCSS 4
- **Database**: PostgreSQL 16 with Liquibase migrations
- **Authentication**: Keycloak 24 with OAuth2/JWT
- **State Management**: Zustand, React Query (@tanstack/react-query)
- **Build & Deploy**: Docker Compose

### Key Components

**Backend** (`backend/rfc-service/`):
- Standard Spring Boot layered architecture: Controllers → Services → Repositories
- **Contract-first approach**: OpenAPI specs in `src/main/resources/openapi/` are split into modular YAML files by resource (user, team, system). The main `openapi.yaml` references these via `$ref`. Controllers are generated from these specs using `openapi-generator` Gradle plugin (Spring generator).
- Generated code location: `build/generated/openapi/src/main/java/` with packages `ru.c21501.rfcservice.openapi.api` (interfaces) and `ru.c21501.rfcservice.openapi.model` (DTOs)
- Entity relationships: RFC (central entity) has many-to-many with Users (executors/reviewers), belongs to System, has StatusHistory
- Security: Two modes - full Keycloak OAuth2 (SecurityConfig) or dummy mode (DummySecurityConfig), toggled via `APP_SECURITY_ENABLED` env var
- Role-based access: REQUESTER, EXECUTOR, CAB_MANAGER, ADMIN
- MapStruct for DTO mapping, Lombok for boilerplate reduction
- OpenAPI/Swagger UI available at `/swagger-ui.html`

**Frontend** (`frontend/`):
- Path aliases configured: `@/components`, `@/api`, `@/types`, etc. (see tsconfig.app.json)
- Page structure: Dashboard, Kanban board, AllRfcs, MyRfcs, RfcDetail, References
- API layer: axios with interceptors in `src/api/axiosConfig.ts`
- Authentication: Keycloak-js integration via AuthProvider context
- Protected routes via ProtectedRoute component

**Database**:
- Core entities: rfc, user, system, team, status_history, rfc_executor, rfc_reviewer, rfc_attachment
- RFC workflow statuses: DRAFT → SUBMITTED → UNDER_REVIEW → APPROVED/REJECTED → IMPLEMENTED/CANCELLED
- Liquibase changelogs in `backend/rfc-service/src/main/resources/db/changelog/`

**Authentication**:
- Keycloak realm: `cab-realm`
- Test users available (see backend/rfc-service/README.md for credentials)
- Temporary test endpoint: `POST /api/auth/login` (returns JWT tokens, will be removed in production)
- Keycloak Admin Console: http://localhost:8081 (admin/admin)

## Development Commands

### Full Stack Development

Start all services (infrastructure + backend + frontend):
```bash
docker-compose up -d
```

Stop all services:
```bash
docker-compose down
```

Stop and remove volumes:
```bash
docker-compose down -v
```

### Backend Development

Start only infrastructure (DB + Keycloak) without backend:
```bash
docker compose -f backend/rfc-service/docker-compose-infra.yml up -d
```

Start backend with dependencies (from root):
```bash
# With security enabled (default)
APP_SECURITY_ENABLED=true docker compose -f backend/rfc-service/docker-compose.yml up --build

# Without security (development mode)
APP_SECURITY_ENABLED=false docker compose -f backend/rfc-service/docker-compose.yml up --build

# Windows PowerShell syntax
$env:APP_SECURITY_ENABLED="true"
docker compose -f backend/rfc-service/docker-compose.yml up --build
```

Run backend locally (requires infrastructure running):
```bash
cd backend/rfc-service
./gradlew bootRun
```

Build backend:
```bash
cd backend/rfc-service
./gradlew build
```

Run backend tests:
```bash
cd backend/rfc-service
./gradlew test
```

Regenerate OpenAPI code after spec changes:
```bash
cd backend/rfc-service
./gradlew openApiGenerate
# Or simply build (openApiGenerate runs automatically before compilation)
./gradlew build
```

### Frontend Development

Install dependencies:
```bash
cd frontend
npm install
```

Run development server with HMR:
```bash
cd frontend
npm run dev
```

Build for production:
```bash
cd frontend
npm run build
```

Run linting:
```bash
cd frontend
npm run lint
```

Preview production build:
```bash
cd frontend
npm run preview
```

## Service URLs (Default)

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Keycloak Admin**: http://localhost:8081 (admin/admin)
- **PostgreSQL**: localhost:5050 (postgres/postgres)

## Environment Configuration

Main environment variables (create `.env` file in root):
- `POSTGRES_USERNAME` - PostgreSQL username (default: postgres)
- `POSTGRES_PASSWORD` - PostgreSQL password (default: postgres)
- `APP_SECURITY_ENABLED` - Enable/disable Keycloak security (true/false)

## Important Notes

### Security Modes
The backend has two security configurations that are mutually exclusive:
- When `APP_SECURITY_ENABLED=true`: Uses full Keycloak OAuth2 with JWT validation (SecurityConfig)
- When `APP_SECURITY_ENABLED=false`: Uses dummy security with no auth (DummySecurityConfig)
- The active configuration is controlled via `@ConditionalOnProperty` annotations

### Frontend-Backend Status Mapping
There's a status enum mismatch between frontend and backend:
- **Backend**: DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, IMPLEMENTED, CANCELLED
- **Frontend** (`src/types/api.ts`): REQUESTED_NEW, WAITING, APPROVED, WAITING_FOR_CAB, DECLINED, DONE, CANCELLED
- When modifying status-related features, ensure proper mapping between these two sets

### File Uploads
RFC attachments supported via multipart/form-data:
- Max file size: 10MB
- Storage path configurable via `app.file.storage-path` (default: /tmp/uploads)
- See RfcAttachmentController and RfcAttachmentService

### Database Migrations
- Use Liquibase for all schema changes
- Never set `spring.jpa.hibernate.ddl-auto` to anything other than `validate` in production
- Add changesets to `db/changelog/` directory

### OpenAPI Contract-First Development
When adding or modifying API endpoints:
1. Edit OpenAPI specs in `backend/rfc-service/src/main/resources/openapi/api/` (organized by resource)
2. Run `./gradlew openApiGenerate` or `./gradlew build` to regenerate controller interfaces and DTOs
3. Implement the generated interface in your controller (e.g., `@RestController implements UserApi`)
4. OpenAPI spec structure: `openapi.yaml` (main) → references split files in `api/{resource}/` subdirectories
5. Generated interfaces use Spring Boot 3 conventions with `@RestController` and return types (not `ResponseEntity`)

## Project Structure Highlights

- `backend/rfc-service/src/main/java/ru/c21501/rfcservice/`:
  - `controller/` - REST endpoints (implement generated OpenAPI interfaces)
  - `service/` + `service/impl/` - Business logic
  - `repository/` - Spring Data JPA repositories
  - `model/entity/` - JPA entities
  - `model/enums/` - Enums (RfcStatus, Priority, RiskLevel, UserRole, etc.)
  - `dto/request/` and `dto/response/` - API contracts
  - `config/` - Spring configuration (security, swagger, etc.)
  - `specification/` - JPA Specifications for dynamic queries

- `backend/rfc-service/src/main/resources/`:
  - `openapi/` - OpenAPI 3.0 specifications (contract-first)
    - `openapi.yaml` - Main spec file that references all endpoints
    - `api/{resource}/` - Split YAML files per resource (User.yaml, UserById.yaml, model/User.yaml, etc.)
  - `db/changelog/` - Liquibase database migration scripts
  - `application*.yaml` - Spring Boot configuration profiles (default, local, docker)

- `frontend/src/`:
  - `pages/` - Top-level route components
  - `components/` - Organized by feature (dashboard, kanban, rfcs, references, layout)
  - `api/` - API client layer
  - `auth/` - Keycloak authentication integration
  - `hooks/` - Custom React hooks
  - `types/` - TypeScript type definitions
  - `providers/` - React context providers

## Testing Authentication

Use test endpoint for quick JWT token retrieval:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

Available test users with different roles are documented in `backend/rfc-service/README.md`.
