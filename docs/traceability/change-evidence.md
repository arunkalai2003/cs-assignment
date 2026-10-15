# Change Evidence

## Purpose
This file records the engineering evidence for non-trivial project changes, showing what changed, why it changed, how it was validated, and who reviewed it.

## Change log

### Change CE-001
- Change ID: CE-001
- Date: 2026-10-14
- Area: Project governance and engineering process
- Description: Added governance and traceability documentation for requirements, phase planning, review checklists, approval records, and change evidence.
- Why it changed: To satisfy the engineer-led review model, ensure explicit approval ownership, and improve traceability for AI-assisted implementation work.
- Validation evidence: Documentation reviewed for scope alignment, review ownership, and approval flow; project requirements mapped to engineering artifacts.
- Reviewer: Arun Gangatharan

### Change CE-002
- Change ID: CE-002
- Date: 2026-10-14
- Area: URL shortener domain model and DTOs
- Description: Created core domain models and DTOs for short URLs, analytics, geo stats, create/update requests, and error payloads.
- Why it changed: To define a stable API contract and business data model before implementation of service logic.
- Validation evidence: Model structure reviewed against required behaviors such as short-code generation, analytics initialization, and expiration handling.
- Reviewer: Arun Gangatharan

### Change CE-003
- Change ID: CE-003
- Date: 2026-10-14
- Area: Business service layer
- Description: Implemented service logic for create, read, update, delete, redirect validation, and analytics publication behavior.
- Why it changed: To enforce domain rules, protect expired/missing records, and keep controller code thin.
- Validation evidence: Maven test suite passed for service and controller behaviors; invalid and expired redirect scenarios reviewed.
- Reviewer: Arun Gangatharan

### Change CE-004
- Change ID: CE-004
- Date: 2026-10-14
- Area: Controller and REST API
- Description: Added REST endpoints for create, fetch, update, delete, redirect, and analytics retrieval with header-based analytics capture.
- Why it changed: To provide a clean external API and to standardize analytics context capture from HTTP metadata.
- Validation evidence: API tests reviewed for status codes, payload structure, and redirect behavior.
- Reviewer: Arun Gangatharan

### Change CE-005
- Change ID: CE-005
- Date: 2026-10-14
- Area: Kafka analytics processing
- Description: Added Kafka event producer, consumer, deduplication repository, and aggregation of redirect analytics and geo counts.
- Why it changed: To track user interactions without double-counting duplicate events and to keep analytics processing decoupled from the core redirect flow.
- Validation evidence: Duplicate-event behavior tested and successful compile/test validation completed via Maven.
- Reviewer: Arun Gangatharan

### Change CE-006
- Change ID: CE-006
- Date: 2026-10-14
- Area: Error handling and validation
- Description: Added global exception handling and structured API error responses for invalid input, not found cases, expired records, and processing failures.
- Why it changed: To make the API predictable, explicit, and safe under failure conditions.
- Validation evidence: Exception paths reviewed; relevant tests pass under Maven validation.
- Reviewer: Arun Gangatharan

### Change CE-007
- Change ID: CE-007
- Date: 2026-10-14
- Area: Runtime packaging and deployment configuration
- Description: Added Dockerfile, docker-compose.yml, .env.example, and environment-variable-based configuration for PostgreSQL and Kafka; enabled Spring Actuator and OpenAPI support.
- Why it changed: To support consistent local and containerized setup and align the service with runtime and operational observability requirements.
- Validation evidence: Maven tests passed; Docker executable is unavailable in this environment, so runtime validation remains pending on a Docker-enabled host.
- Reviewer: Arun Gangatharan

### Change CE-008
- Change ID: CE-008
- Date: 2026-10-14
- Area: Analytics retention policy
- Description: Implemented scheduled analytics retention cleanup for 60-day retention and repository methods for purging expired summary, geo, and processed-event records.
- Why it changed: To satisfy the 60-day retention requirement and reduce stale operational data.
- Validation evidence: Retention service unit test added; project-level Maven validation completed successfully.
- Reviewer: Arun Gangatharan

## Rule
All non-trivial changes must have documented rationale, validation evidence, and an explicit reviewer. This file is the project’s engineering evidence ledger for change control.
