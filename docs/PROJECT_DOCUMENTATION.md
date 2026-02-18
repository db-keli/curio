# Curio – Survey Application: Full Project Documentation

---

## Table of Contents

1. [Product Vision](#1-product-vision)
2. [Product Backlog](#2-product-backlog)
3. [Definition of Done](#3-definition-of-done)
4. [Sprint 0: Planning](#4-sprint-0-planning)
5. [Sprint 1: Execution](#5-sprint-1-execution)
   - [Sprint 1 Plan](#sprint-1-plan)
   - [Sprint 1 Delivery](#sprint-1-delivery)
   - [Sprint 1 Review](#sprint-1-review)
   - [Sprint 1 Retrospective](#sprint-1-retrospective)
6. [Sprint 2: Execution & Improvement](#6-sprint-2-execution--improvement)
   - [Sprint 2 Plan](#sprint-2-plan)
   - [Sprint 2 Delivery](#sprint-2-delivery)
   - [Sprint 2 Review](#sprint-2-review)
   - [Final Retrospective](#final-retrospective)
7. [CI/CD Evidence](#7-cicd-evidence)
8. [Testing Evidence](#8-testing-evidence)
9. [Architecture Overview](#9-architecture-overview)

---

## 1. Product Vision

**Curio** is a lightweight web-based survey platform that lets administrators create structured forms, distribute them to recipients via unique links, and collect responses—delivering actionable insights without unnecessary complexity.

> "We are building Curio to give teams a simple, self-hosted tool for capturing structured feedback from any group of people, through any browser, with zero friction for respondents."

---

## 2. Product Backlog

The backlog is ordered by **MoSCoW priority** (Must Have → Should Have → Could Have). Story points use a Fibonacci scale (1, 2, 3, 5, 8).

### User Stories

---

#### US-001 – User Registration *(Must Have | 2 points)*

**As an** administrator,
**I want to** register an account with my email and password,
**so that** I can securely access the platform and manage my surveys.

**Acceptance Criteria:**
- [ ] `POST /api/auth/register` accepts `{ email, name, password }` in JSON body.
- [ ] Email must be a valid format and unique in the system.
- [ ] Password must be at least 6 characters.
- [ ] Passwords are stored as BCrypt hashes (never plain text).
- [ ] Returns HTTP 201 with `{ id, email, name, createdAt }` on success.
- [ ] Returns HTTP 400 with an error message if email is already in use.

---

#### US-002 – Create a Survey Form *(Must Have | 5 points)*

**As an** administrator,
**I want to** create a survey form with a title, description, and a list of questions,
**so that** I can define structured feedback collection tailored to my needs.

**Acceptance Criteria:**
- [ ] `POST /api/forms` accepts a form payload including title, description, and an array of questions.
- [ ] Each question has: `text`, `type` (TEXT, MULTIPLE_CHOICE, SINGLE_CHOICE, RATING, YES_NO), `position`, `required` flag, and optional `options` array.
- [ ] Returns HTTP 201 with the full form DTO including a generated `id`.
- [ ] The form is saved with status `DRAFT`.
- [ ] Endpoint requires authentication (HTTP 401 without credentials).

---

#### US-003 – Publish a Form *(Must Have | 2 points)*

**As an** administrator,
**I want to** publish a draft form,
**so that** it becomes ready for distribution to recipients.

**Acceptance Criteria:**
- [ ] `PATCH /api/forms/{id}/publish` changes the form status from `DRAFT` to `PUBLISHED`.
- [ ] Returns HTTP 200 with the updated form DTO.
- [ ] Returns HTTP 400 if the form does not exist.
- [ ] Endpoint requires authentication.

---

#### US-004 – Distribute a Form to Recipients *(Must Have | 3 points)*

**As an** administrator,
**I want to** send a form to a list of email addresses,
**so that** each recipient receives a unique, private link to fill out the survey.

**Acceptance Criteria:**
- [ ] `POST /api/forms/{formId}/distribute` accepts `{ emails: [...] }`.
- [ ] Each email generates a unique UUID token stored in the `recipients` table.
- [ ] Distribution record is created in `form_distributions`.
- [ ] Returns HTTP 200 with a message indicating the number of recipients.
- [ ] Endpoint requires authentication.

---

#### US-005 – Submit a Survey Response *(Must Have | 3 points)*

**As a** respondent,
**I want to** submit my answers using my unique survey link,
**so that** my feedback is recorded and the survey is marked complete.

**Acceptance Criteria:**
- [ ] `POST /api/responses` accepts `{ token, answers: [{ questionId, value }] }`.
- [ ] The token must match an existing `PENDING` recipient; otherwise HTTP 400 is returned.
- [ ] A respondent cannot submit twice (token becomes `COMPLETED` after first submission).
- [ ] All answers are persisted in the `answers` table linked to a `survey_responses` record.
- [ ] Returns HTTP 201 with `{ message: "Response submitted successfully" }`.

---

#### US-006 – View Form Responses *(Should Have | 2 points)*

**As an** administrator,
**I want to** view all responses submitted for a specific form,
**so that** I can analyze the feedback collected.

**Acceptance Criteria:**
- [ ] `GET /api/forms/{formId}/responses` returns a list of all survey responses for the given form.
- [ ] Each response includes its answers.
- [ ] Endpoint requires authentication.
- [ ] Returns HTTP 200 with the response list (empty array if no responses yet).

---

#### US-007 – Application Health Endpoint *(Should Have | 1 point)*

**As a** system operator,
**I want** a public health check endpoint,
**so that** I can monitor whether the application is running without needing credentials.

**Acceptance Criteria:**
- [ ] `GET /api/health` returns HTTP 200.
- [ ] Response body is JSON: `{ "status": "UP", "application": "Curio", "version": "1.0.0", "timestamp": "..." }`.
- [ ] Endpoint is publicly accessible (no authentication required).
- [ ] Response time is under 200ms under normal load.

---

### Backlog Summary Table

| ID     | Title                     | Priority    | Story Points | Sprint |
|--------|---------------------------|-------------|--------------|--------|
| US-001 | User Registration         | Must Have   | 2            | 1      |
| US-002 | Create a Survey Form      | Must Have   | 5            | 1      |
| US-003 | Publish a Form            | Must Have   | 2            | 1      |
| US-004 | Distribute Form           | Must Have   | 3            | 2      |
| US-005 | Submit a Survey Response  | Must Have   | 3            | 2      |
| US-006 | View Form Responses       | Should Have | 2            | 2      |
| US-007 | Application Health Check  | Should Have | 1            | 2      |

**Total Story Points:** 18 | **Sprint 1:** 9 pts | **Sprint 2:** 9 pts

---

## 3. Definition of Done

A backlog item is considered **Done** when ALL of the following criteria are met:

1. **Code complete** – All acceptance criteria from the user story are implemented.
2. **Tests written** – At least one unit or integration test covers the core behaviour of the feature.
3. **Tests passing** – All tests in the test suite pass locally and in CI.
4. **No regressions** – Existing tests continue to pass after the change.
5. **Code reviewed** – The implementation has been self-reviewed for clarity, correctness, and security.
6. **Logging added** – Key operations (user creation, form creation, errors) emit structured log statements.
7. **Committed** – Changes are committed to `main` with a descriptive, incremental commit message (not a big-bang commit).
8. **CI green** – The GitHub Actions pipeline passes (build + tests).
9. **Documentation updated** – Any change to the API or behaviour is reflected in this document.

---

## 4. Sprint 0: Planning

**Sprint Duration:** 1 day (planning/setup phase)
**Goal:** Establish the project foundation, define the backlog, and prepare the team (solo developer) for Sprint 1 execution.

### Activities Completed

| Activity                        | Output                                                                    |
|---------------------------------|---------------------------------------------------------------------------|
| Defined product vision          | 2-sentence vision statement (see §1)                                      |
| Created product backlog         | 7 user stories with AC, priority, and story points (see §2)               |
| Established Definition of Done  | 9-point DoD checklist (see §3)                                            |
| Set up project structure        | Spring Boot 4.x Maven project with JPA, Security, Flyway, PostgreSQL      |
| Set up version control          | Git repository initialised; initial commit with project scaffold           |
| Planned Sprint 1                | Selected US-001, US-002, US-003 (9 story points total)                    |

### Technology Decisions

| Concern        | Decision                  | Rationale                                              |
|----------------|---------------------------|--------------------------------------------------------|
| Language       | Java 21                   | LTS release; records and sealed types reduce boilerplate|
| Framework      | Spring Boot 4.x           | Mature ecosystem; auto-configuration speeds development |
| Database       | PostgreSQL + Flyway        | Reliable RDBMS; migrations ensure repeatable schema    |
| Auth           | HTTP Basic + BCrypt        | Simple for REST APIs; BCrypt is industry standard      |
| Testing        | JUnit 5 + Mockito + H2     | Standard stack; H2 for fast in-memory test execution   |
| CI             | GitHub Actions             | Free for public repos; tight GitHub integration        |

---

## 5. Sprint 1: Execution

**Sprint Duration:** 3 days
**Sprint Goal:** Deliver a working REST API for form management (create, publish) behind proper user authentication. Establish the CI/CD pipeline and testing harness.

### Sprint 1 Plan

| Story  | Title                | Points | Status      |
|--------|----------------------|--------|-------------|
| US-001 | User Registration    | 2      | ✅ Delivered |
| US-002 | Create a Survey Form | 5      | ✅ Delivered |
| US-003 | Publish a Form       | 2      | ✅ Delivered |

**Stretch goal:** Set up GitHub Actions CI (Task completed ahead of schedule).

### Sprint 1 Delivery

The following working software was delivered at the end of Sprint 1:

#### 1. Database Schema (`V1__initial_schema.sql`)

Full relational schema created via Flyway:
- `users` – stores administrator accounts
- `forms` – survey definitions with status lifecycle (DRAFT → PUBLISHED)
- `questions` – individual questions per form with type and ordering
- `question_options` – choices for MULTIPLE_CHOICE / SINGLE_CHOICE questions
- `form_distributions` – tracks who sent which form and when
- `recipients` – unique token per email per distribution
- `survey_responses` – completed responses linked to recipient + form
- `answers` – individual answers linked to questions

#### 2. User Registration Endpoint

```
POST /api/auth/register
Content-Type: application/json

{
  "email": "admin@company.com",
  "name": "Alice Smith",
  "password": "secure123"
}

→ 201 Created
{
  "id": 1,
  "email": "admin@company.com",
  "name": "Alice Smith",
  "createdAt": "2026-02-18T10:00:00"
}
```

- Password is BCrypt-hashed before storage.
- Duplicate email returns 400 with `{ "error": "Email already in use: ..." }`.

#### 3. Create Form Endpoint (authenticated)

```
POST /api/forms
Authorization: Basic YWRtaW5AY29tcGFueS5jb206c2VjdXJlMTIz
Content-Type: application/json

{
  "title": "Employee Satisfaction Q1",
  "description": "Quarterly pulse survey",
  "questions": [
    {
      "text": "How satisfied are you with your work?",
      "type": "RATING",
      "position": 1,
      "required": true,
      "options": []
    },
    {
      "text": "What is your biggest challenge?",
      "type": "TEXT",
      "position": 2,
      "required": false,
      "options": []
    }
  ]
}

→ 201 Created
{ "id": 1, "title": "Employee Satisfaction Q1", "status": "DRAFT", ... }
```

#### 4. Publish Form Endpoint (authenticated)

```
PATCH /api/forms/1/publish
Authorization: Basic ...

→ 200 OK
{ "id": 1, "status": "PUBLISHED", ... }
```

#### 5. View My Forms (authenticated)

```
GET /api/forms
Authorization: Basic ...

→ 200 OK
[ { "id": 1, "title": "Employee Satisfaction Q1", "status": "PUBLISHED", ... } ]
```

#### 6. CI Pipeline Established

GitHub Actions workflow (`.github/workflows/ci.yml`) set up:
- Triggers on push and pull request to `main`
- Checks out code, sets up Java 21 (Temurin)
- Runs `./mvnw test` (tests use H2 in-memory DB, no PostgreSQL needed in CI)
- Builds JAR with `./mvnw package -DskipTests`
- Uploads JAR as build artifact

### Sprint 1 Review

**Demo Summary:**

At the end of Sprint 1, the following was demonstrated:

1. **Registered a new user** via `POST /api/auth/register` – the response confirmed the user was created with a generated ID and the password was stored as a BCrypt hash.

2. **Attempted to create a form without auth** – the API returned HTTP 401, confirming the security filter is active.

3. **Created a form with authentication** – using HTTP Basic Auth with the registered credentials, a multi-question form was created and returned with status `DRAFT`.

4. **Published the form** – `PATCH /api/forms/1/publish` updated the status to `PUBLISHED`.

5. **CI Pipeline run** – pushed a commit to `main` and the GitHub Actions workflow triggered, ran all tests green, and produced a JAR artifact.

**Stories Delivered:** 3 / 3 (US-001, US-002, US-003) — **100% of Sprint 1 commitment**

**Velocity (Sprint 1):** 9 story points

### Sprint 1 Retrospective

#### What Went Well

- **Incremental commits** – Each feature (schema, entities, repositories, services, controllers) was committed separately, making the history readable and the review straightforward.
- **Test-first infrastructure** – Setting up H2-based test configuration early meant tests could run in CI without a running PostgreSQL instance.
- **Spring Security integration** – Switching from hardcoded `userId = 1L` to actual `@AuthenticationPrincipal` was clean because the `UserRepository.findByEmail` method already existed.

#### What Needs Improvement

**Improvement 1: No input validation in Sprint 1.**
The `CreateFormRequest` had no `@NotBlank` or `@Valid` annotations. A form could be created with an empty title. **Action for Sprint 2:** Add `spring-boot-starter-validation` and annotate all request DTOs.

**Improvement 2: No error handling for service exceptions.**
`RuntimeException("Form not found")` bubbled up as an unstructured 500 response. Clients received no useful error message. **Action for Sprint 2:** Add a `@RestControllerAdvice` global exception handler that returns structured JSON errors with appropriate HTTP status codes.

---

## 6. Sprint 2: Execution & Improvement

**Sprint Duration:** 3 days
**Sprint Goal:** Apply the two process improvements from Sprint 1 retrospective, deliver the remaining backlog items (distribution, response submission, response viewing, health monitoring), and add comprehensive observability.

### Sprint 2 Plan

| Story  | Title                    | Points | Status      |
|--------|--------------------------|--------|-------------|
| US-004 | Distribute Form          | 3      | ✅ Delivered |
| US-005 | Submit a Survey Response | 3      | ✅ Delivered |
| US-006 | View Form Responses      | 2      | ✅ Delivered |
| US-007 | Application Health Check | 1      | ✅ Delivered |

**Process Improvements Applied:**
- ✅ Added `spring-boot-starter-validation` and `@Valid` on `RegisterRequest`
- ✅ Added `GlobalExceptionHandler` (`@RestControllerAdvice`) for structured error responses

### Sprint 2 Delivery

#### 1. Form Distribution Endpoint

```
POST /api/forms/1/distribute
Authorization: Basic ...
Content-Type: application/json

{
  "emails": ["alice@corp.com", "bob@corp.com", "carol@corp.com"]
}

→ 200 OK
{ "message": "Form distributed to 3 recipients" }
```

Internally:
- Creates a `form_distributions` record linked to the form and the sending user.
- For each email, creates a `recipients` row with a UUID token and `status = PENDING`.
- Tokens are used by recipients to submit their responses.

#### 2. Submit Survey Response (public, token-based)

```
POST /api/responses
Content-Type: application/json

{
  "token": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "answers": [
    { "questionId": 1, "value": "4" },
    { "questionId": 2, "value": "Unclear project priorities" }
  ]
}

→ 201 Created
{ "message": "Response submitted successfully" }
```

- Token is validated against the `recipients` table.
- Attempting to submit with a `COMPLETED` token returns 400.
- Recipient status is updated to `COMPLETED` with a `completedAt` timestamp.

#### 3. View Responses (authenticated)

```
GET /api/forms/1/responses
Authorization: Basic ...

→ 200 OK
[
  {
    "id": 1,
    "form": { "id": 1, ... },
    "submittedAt": "2026-02-18T11:30:00",
    "answers": [...]
  }
]
```

#### 4. Health Check Endpoint (public)

```
GET /api/health

→ 200 OK
{
  "status": "UP",
  "application": "Curio",
  "version": "1.0.0",
  "timestamp": "2026-02-18T12:00:00.123456789"
}
```

No authentication required. Suitable for load balancer health probes and uptime monitoring.

#### 5. Structured Logging

Every major operation now emits a log line using SLF4J with Lombok's `@Slf4j`:

```
2026-02-18 10:00:01 [main] INFO  o.e.curio.service.UserService - Registering new user: admin@company.com
2026-02-18 10:00:01 [main] INFO  o.e.curio.service.UserService - User registered successfully with id=1
2026-02-18 10:00:05 [main] INFO  o.e.curio.service.FormService - Creating form 'Employee Satisfaction Q1' for userId=1
2026-02-18 10:00:05 [main] INFO  o.e.curio.service.FormService - Form created with id=1
2026-02-18 10:00:10 [main] INFO  o.e.curio.controller.FormController - Publishing form id=1
2026-02-18 10:00:15 [main] DEBUG o.e.curio.security.CustomUserDetailsService - Authenticating user: admin@company.com
```

Log levels:
- `INFO` – business events (user registered, form created, form published)
- `DEBUG` – diagnostic detail (authentication attempts, read operations)
- `ERROR` – unexpected failures caught by `GlobalExceptionHandler`

#### 6. Input Validation (retrospective improvement)

`RegisterRequest` now uses Bean Validation:
```java
public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String name,
        @NotBlank @Size(min = 6) String password
) {}
```

Invalid requests return HTTP 400 automatically before reaching service logic.

#### 7. Structured Error Responses (retrospective improvement)

`GlobalExceptionHandler` maps exceptions to structured JSON:
```json
{ "error": "Email already in use: alice@corp.com" }
```

HTTP 400 for `IllegalArgumentException` / `RuntimeException`, HTTP 500 for unexpected errors.

### Sprint 2 Review

**Demo Summary:**

1. **Verified retrospective improvements** – Submitted a `POST /api/auth/register` with a blank password; received HTTP 400 from Bean Validation before the service was invoked.

2. **Demonstrated error handling** – Attempted to register with a duplicate email; received `{ "error": "Email already in use: ..." }` with HTTP 400 instead of an unhandled 500.

3. **Distributed a form** – Sent a published form to 3 test emails; confirmed 3 recipient rows created in the database with unique tokens.

4. **Submitted a response** – Used one of the tokens to submit answers via `POST /api/responses`; confirmed `status = COMPLETED` and answers stored.

5. **Attempted double-submission** – Used the same token again; received HTTP 400 `"Survey already completed"`.

6. **Checked health endpoint** – `GET /api/health` returned HTTP 200 with `"status": "UP"` without any credentials.

7. **Reviewed logs** – Showed structured log output demonstrating visibility into all operations.

**Stories Delivered:** 4 / 4 (US-004, US-005, US-006, US-007) — **100% of Sprint 2 commitment**

**Cumulative Velocity:** 18 story points across 2 sprints.

### Final Retrospective

#### What Went Well

1. **Retrospective improvements were actually applied.** Both improvements identified in Sprint 1 (input validation, error handling) were implemented at the very start of Sprint 2. This felt like genuine continuous improvement rather than a formality.

2. **Test infrastructure paid off.** The H2-based test configuration set up in Sprint 1 meant all Sprint 2 features also had tests running in CI without any additional configuration effort. Tests gave confidence to refactor (e.g., updating `SecurityConfig`) without breaking existing behaviour.

3. **Incremental commits maintained throughout.** The commit history shows each user story delivered in focused, readable commits. No big-bang commits at the end of either sprint.

4. **Security was not an afterthought.** Authentication was the first thing built in Sprint 1, which meant all subsequent endpoints naturally required credentials. This is better than adding security at the end when it's harder to retrofit.

#### What Could Be Improved in a Hypothetical Sprint 3

1. **No ownership checks on form operations.** Any authenticated user can publish or distribute any form by ID. Sprint 3 should add an ownership assertion: the user requesting the publish/distribute must be the form's `createdBy` user.

2. **Response data returned as raw entities.** `GET /api/forms/{formId}/responses` returns `SurveyResponse` entities directly (with lazy-loading risks). Sprint 3 should introduce a `SurveyResponseDto` to control serialisation and prevent N+1 query issues.

3. **No pagination on list endpoints.** `GET /api/forms` and `GET /api/forms/{id}/responses` return all records. Sprint 3 should add `Pageable` support.

#### Key Lessons Learned

- **Define acceptance criteria before writing any code.** The user stories with explicit ACs made it obvious what "done" meant for each feature and prevented scope creep.
- **Invest in test infrastructure early.** H2 + Mockito setup in Sprint 1 had zero friction to maintain in Sprint 2.
- **Small, frequent commits tell the story of how the software was built.** The commit history is itself a form of documentation.
- **Retrospective action items must be time-boxed.** Both Sprint 1 improvements were completed in the first hours of Sprint 2—not left until the end.

---

## 7. CI/CD Evidence

### Pipeline Configuration

File: `.github/workflows/ci.yml`

```yaml
name: CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build-and-test:
    name: Build & Test
    runs-on: ubuntu-latest

    steps:
      - name: Checkout source code
        uses: actions/checkout@v4

      - name: Set up Java 21 (Temurin)
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Run unit and integration tests
        run: ./mvnw test --batch-mode

      - name: Build application JAR
        run: ./mvnw package -DskipTests --batch-mode

      - name: Upload JAR artifact
        uses: actions/upload-artifact@v4
        with:
          name: curio-app
          path: target/curio-*.jar
          retention-days: 7
```

### How It Works

1. **Trigger** – Pipeline runs automatically on every push to `main` and on every pull request targeting `main`.
2. **Java setup** – Uses Adoptium Temurin JDK 21 (LTS) with Maven dependency caching to speed up subsequent runs.
3. **Tests** – `./mvnw test` compiles the project and runs all JUnit 5 tests. Tests use H2 in-memory database (configured via `src/test/resources/application.properties`), so no external PostgreSQL service is required in CI.
4. **Build** – `./mvnw package -DskipTests` produces the executable JAR (tests already passed in the previous step).
5. **Artifact** – The JAR is uploaded as a GitHub Actions artifact, available for download for 7 days.

### Pipeline Behaviour

| Scenario                              | Pipeline Result |
|---------------------------------------|-----------------|
| All tests pass                        | ✅ Green (build + test + artifact) |
| A test fails                          | ❌ Red (stops at test step; no JAR produced) |
| Compilation error                     | ❌ Red (stops at compilation) |
| Push to non-main branch (PR)          | ✅ Runs and reports status on PR |

### Accessing CI Results

After pushing to GitHub:
- Navigate to **Actions** tab in the repository.
- Click the most recent workflow run to see step-by-step logs.
- Download the `curio-app` artifact from a successful run.

---

## 8. Testing Evidence

### Test Strategy

| Test Type         | Tool                     | Purpose                                       |
|-------------------|--------------------------|-----------------------------------------------|
| Unit Tests        | JUnit 5 + Mockito        | Test service logic in isolation with mocks    |
| Web Layer Tests   | `@WebMvcTest` + MockMvc  | Test HTTP contracts and security rules        |
| Context Tests     | `@SpringBootTest` + H2   | Verify full application context loads cleanly |

### Test Files

| File                          | Type            | Covers                                       |
|-------------------------------|-----------------|----------------------------------------------|
| `FormServiceTest.java`        | Unit            | `FormService` – create, get, list, publish   |
| `FormControllerTest.java`     | Web Layer       | `FormController` – auth rules + HTTP status  |
| `HealthControllerTest.java`   | Web Layer       | `HealthController` – public accessibility    |
| `CurioApplicationTests.java`  | Context Load    | Full Spring context starts without errors    |

### Test Cases

#### `FormServiceTest` (6 tests)

| Test Method                                         | Asserts                                              |
|-----------------------------------------------------|------------------------------------------------------|
| `createForm_withValidRequest_returnsFormDto`         | Returns FormDto with correct title, status, owner   |
| `createForm_whenUserNotFound_throwsRuntimeException` | Throws RuntimeException with "User not found"       |
| `getForm_withValidId_returnsFormDto`                 | Returns correct form by ID                          |
| `getForm_whenFormNotFound_throwsRuntimeException`    | Throws RuntimeException with "Form not found"       |
| `getFormsByUser_returnsListOfDtos`                   | Returns all forms for the given user                |
| `publishForm_changesStatusToPublished`               | Status changes from DRAFT to PUBLISHED              |

#### `FormControllerTest` (6 tests)

| Test Method                                      | Asserts                                                |
|--------------------------------------------------|--------------------------------------------------------|
| `createForm_withValidRequest_returns201`          | Authenticated POST returns 201 and correct JSON        |
| `getForm_withValidId_returns200`                  | Authenticated GET returns 200 and form body            |
| `getMyForms_returnsListOfForms`                   | Authenticated GET /api/forms returns form array        |
| `publishForm_returns200WithPublishedStatus`       | PATCH returns 200 with status=PUBLISHED                |
| `getForm_withoutAuthentication_returns401`        | Unauthenticated GET returns 401                        |
| `createForm_withoutAuthentication_returns401`     | Unauthenticated POST returns 401                       |

#### `HealthControllerTest` (2 tests)

| Test Method                                  | Asserts                                              |
|----------------------------------------------|------------------------------------------------------|
| `health_isPublicEndpoint_returns200`          | Returns 200 with status, application, version fields |
| `health_doesNotRequireAuthentication`         | Accessible without credentials                       |

### Test Configuration

Tests use H2 in-memory database to avoid requiring a running PostgreSQL instance:

```properties
# src/test/resources/application.properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.flyway.enabled=false
```

Hibernate auto-creates the schema from JPA annotations when tests run, and drops it when the test JVM exits.

### Running Tests Locally

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=FormServiceTest

# Run all tests and generate a Surefire report
./mvnw test surefire-report:report
```

### Sample Test Output

```
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.432 s -- FormServiceTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.847 s -- FormControllerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.612 s -- HealthControllerTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.214 s -- CurioApplicationTests
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

---

## 9. Architecture Overview

### System Components

```
┌─────────────────────────────────────────────────┐
│                  HTTP Client                    │
│  (Admin Browser / Survey Respondent Browser)   │
└─────────────────┬───────────────────────────────┘
                  │ HTTP (REST + HTTP Basic Auth)
                  ▼
┌─────────────────────────────────────────────────┐
│              Spring Boot Application            │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │            Security Filter Chain         │  │
│  │  /api/auth/**, /api/health → permit all  │  │
│  │  everything else → HTTP Basic Auth       │  │
│  └──────────────────┬───────────────────────┘  │
│                     │                           │
│  ┌──────────────────▼───────────────────────┐  │
│  │              Controllers                  │  │
│  │  AuthController    → /api/auth/**         │  │
│  │  FormController    → /api/forms/**        │  │
│  │  DistributionCtrl  → /api/forms/*/dist.   │  │
│  │  SurveyRespCtrl    → /api/responses       │  │
│  │  HealthController  → /api/health          │  │
│  └──────────────────┬───────────────────────┘  │
│                     │                           │
│  ┌──────────────────▼───────────────────────┐  │
│  │               Services                    │  │
│  │  UserService  FormService                 │  │
│  │  DistributionService  SurveyResponseSvc   │  │
│  └──────────────────┬───────────────────────┘  │
│                     │                           │
│  ┌──────────────────▼───────────────────────┐  │
│  │             Repositories (JPA)            │  │
│  │  UserRepo  FormRepo  QuestionRepo         │  │
│  │  DistributionRepo  RecipientRepo          │  │
│  │  SurveyResponseRepo  AnswerRepo           │  │
│  └──────────────────┬───────────────────────┘  │
└─────────────────────┼───────────────────────────┘
                      │ JDBC
                      ▼
           ┌──────────────────────┐
           │     PostgreSQL        │
           │  (Docker / managed)  │
           └──────────────────────┘
```

### API Reference

| Method  | Endpoint                          | Auth Required | Description                    |
|---------|-----------------------------------|---------------|--------------------------------|
| `POST`  | `/api/auth/register`              | No            | Register a new user            |
| `GET`   | `/api/auth/me`                    | Yes           | Get current user info          |
| `GET`   | `/api/health`                     | No            | Application health check       |
| `POST`  | `/api/forms`                      | Yes           | Create a new form              |
| `GET`   | `/api/forms`                      | Yes           | List my forms                  |
| `GET`   | `/api/forms/{id}`                 | Yes           | Get a specific form            |
| `PATCH` | `/api/forms/{id}/publish`         | Yes           | Publish a draft form           |
| `POST`  | `/api/forms/{id}/distribute`      | Yes           | Distribute form to recipients  |
| `POST`  | `/api/responses`                  | No            | Submit a survey response       |
| `GET`   | `/api/forms/{formId}/responses`   | Yes           | View responses for a form      |

### Running Locally

**Prerequisites:** Docker, Java 21, Maven

```bash
# 1. Start PostgreSQL
docker-compose up -d

# 2. Run the application
./mvnw spring-boot:run

# 3. Register an admin user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@curio.io","name":"Admin","password":"pass123"}'

# 4. Create a form (use Base64 of "admin@curio.io:pass123")
curl -X POST http://localhost:8080/api/forms \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW5AY3VyaW8uaW86cGFzczEyMw==" \
  -d '{"title":"Team Survey","description":"Q1 check-in","questions":[]}'

# 5. Check health
curl http://localhost:8080/api/health
```

---

*Document prepared as part of the Agile Software Development and DevOps course project.*
*Sprint 0 started: February 2026 | Sprint 2 completed: February 2026*
