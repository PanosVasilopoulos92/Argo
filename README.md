# Argo

> **Note:** Development of Argo has continued in the company's private repository. This public repository reflects an earlier snapshot of the project.

**A maritime / shipping ERP** built to address the operational needs of ship management — vessel and crew registries, certificate tracking, and a full procure-to-pay chain (requisitions → quotations → purchase orders → goods receipt → invoicing with three-way match).

> Backend service. Frontend (Angular) is planned but not yet started.

---

## Tech stack

| Concern | Choice |
|---|---|
| Language / runtime | Java 25 |
| Framework | Spring Boot 4.0.5 |
| Build | Maven (`spring-boot-starter-parent`) |
| Persistence | Spring Data JPA + Hibernate, PostgreSQL 16 |
| AuthN / AuthZ | Keycloak 26 (OAuth2 / OIDC), Spring Security OAuth2 Resource Server (JWT) |
| File type detection | Apache Tika (`tika-core`) |
| Validation | `spring-boot-starter-validation` |
| Partial updates | `jackson-databind-nullable` (JSON-nullable for PATCH semantics) |
| Boilerplate | Lombok, Hibernate static metamodel processor |
| Ops | Spring Boot Actuator |
| Error model | RFC 7807 `ProblemDetail` (`application/problem+json`) |

---

## Architecture at a glance

A modular monolith. Each business domain is a self-contained package under `org.viators.argo`, typically following an entity → repository → service → controller layering with `dto/request` and `dto/response` sub-packages, plus `enums`, and a `sequence` package where the domain issues human-readable reference numbers.

```
org.viators.argo
├── vessel              Vessel registry
├── person              People (officeemployee, seafarer)
├── assignment          Crew-to-vessel assignments
├── certificate         Person & vessel certificates
├── item                Item catalog (+ reference-number sequences)
├── requisition         Requisitions (+ lines, sequences)
├── supplier            Suppliers
├── quotation           Quotations
├── purchaseorder       Purchase orders (+ lines, sequences)
├── goodsreceipt        Goods receipt (+ lines, sequences)
├── invoice             Invoicing & three-way match (+ lines, config, sequences)
├── docs                Documentation library (doccategory, files)  ← in progress
├── user / auth         User provisioning & authentication
├── config              App-wide configuration
└── common              Shared base entity, exception handling, validation,
                        converters, enums, utilities
```

Each domain maps to a delivered epic — vessel/crew/assignments/certificates (Epics 1–5), procurement (Epics 6–8), invoicing (Epic 9), and the documentation library (Epic 10, in progress).

---

## Prerequisites

- **JDK 25**
- **Docker** & **Docker Compose** (the recommended way to run everything)
- **Maven** (only needed for running the app directly from an IDE; the Docker build is self-contained)

---

## Configuration

Secrets and environment-specific values are injected via environment variables — **nothing sensitive is committed**. Compose reads them from a `.env` file in the project root. Create one based on the table below:

| Variable | Used by | Notes |
|---|---|---|
| `POSTGRES_USER` | Postgres, Keycloak, backend | DB superuser |
| `POSTGRES_PASSWORD` | Postgres, Keycloak, backend | |
| `KEYCLOAK_ADMIN` | Keycloak | bootstrap admin username |
| `KEYCLOAK_ADMIN_PASSWORD` | Keycloak | bootstrap admin password |
| `KC_DB_URL` | Keycloak | JDBC URL for Keycloak's own schema |
| `KEYCLOAK_CLIENT_SECRET` | backend | secret for the `argo-api` confidential client |
| `ARGO_DOC_STORAGE_PATH` | backend | filesystem root for the documentation library (defaults to `/var/argo/documents`) |

Application configuration lives in `src/main/resources`:
- `application.yaml` — shared config (OAuth2 issuer, Keycloak admin client, multipart limits, pagination defaults, invoice match tolerances, documentation storage root).
- `application-dev.yml` — dev profile (verbose logging, `ddl-auto: create`, runs on port **8888** from the IDE).
- `application-prod.yml` — prod profile (`ddl-auto: validate`, quiet logging).

> **Heads-up on dev:** the `dev` profile uses `ddl-auto: create`, so the schema is **recreated on every startup** — expect dev data to be wiped between runs. Prod uses `validate` and never mutates the schema.

---

## Running with Docker (recommended)

The base `docker-compose.yml` defines three services — `postgres_db`, `keycloak`, and `backend` — on a shared network, with named volumes for Postgres data (`postgres_data`) and the documentation library (`argo_documents` → `/var/argo/documents`). On first start, `init-db.sql` creates the `argo-dev`, `argo-prod`, and `keycloak` databases.

**Development** (publishes Postgres `5432` to the host for IDE/PgAdmin access, points the backend at `argo-dev`):

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build
```

**Production** (Postgres stays internal to the Docker network, backend points at `argo-prod`):

```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up --build
```

### Service endpoints

| Service | URL / port |
|---|---|
| Backend API | `http://localhost:8080` |
| Keycloak | `http://localhost:8181` (admin console) |
| Postgres | `localhost:5432` (dev override only) |

---

## Running the backend from an IDE

Run the infrastructure (Postgres + Keycloak) via Compose, then start the Spring Boot application from IntelliJ with the `dev` profile. In this mode the app listens on **port 8888** and reaches Postgres/Keycloak through the host-published ports (`5432`, `8181`). Devtools hot-restart is enabled.

---

## Authentication & authorization

Keycloak is the identity provider. The backend is a **stateless OAuth2 resource server**: it validates incoming JWTs against the realm's issuer (`/.well-known/openid-configuration` → JWKS), with no session state and no secret keys stored in the app.

- **Realm:** `argo-realm`
- **API client:** `argo-api` (confidential). Used via the `client_credentials` grant so the backend can provision users in Keycloak through the Keycloak Admin Client during registration.
- **Authorization** is role-based; endpoints are guarded by realm roles (e.g. `FINANCE_APPROVER`, `FOM`, and `doc_admin` for the documentation library).

---

## API conventions

- **Errors:** RFC 7807 `ProblemDetail` responses (`application/problem+json`) across both custom and built-in Spring MVC exceptions.
- **Pagination:** Spring Data `Pageable` — default page size **20**, max **100**.
- **Partial updates:** PATCH endpoints use JSON-nullable to distinguish "field omitted" from "field explicitly set to null."
- **Audit fields** follow the Argo convention of storing the acting **username as a string** (not a foreign key to the user table).
- **Reference numbers** for procurement documents are issued via per-domain sequence tables (e.g. `category_sequences`, seeded by `init-db.sql`).

---

## Project layout

```
.
├── Dockerfile                  Multi-stage build (Maven 3.9 + Temurin 25 → Temurin 25 JRE alpine)
├── docker-compose.yml          Base stack: postgres_db, keycloak, backend
├── docker-compose.dev.yml      Dev override (publishes 5432, dev profile, argo-dev DB)
├── docker-compose.prod.yml     Prod override (internal Postgres, prod profile, argo-prod DB)
├── init-db.sql                 Creates databases + seeds reference-number sequences
├── pom.xml
└── src/main
    ├── java/org/viators/argo    Domain packages (see Architecture)
    └── resources
        ├── application.yaml      Shared config
        ├── application-dev.yml   Dev profile
        ├── application-prod.yml  Prod profile
        └── business_rules        Project business-rules register
```

---

## Business rules

The project maintains a living business-rules register at `src/main/resources/business_rules/argo-business-rules-register.md`, documenting every cross-cutting rule (BR-NNN) with its rationale and the features it applies to.

---

## License

See `LICENCE`.
