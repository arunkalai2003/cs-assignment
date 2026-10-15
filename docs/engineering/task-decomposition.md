# Task Decomposition

## Phase structure
The implementation is organized into reviewable phases to preserve dependency ordering and reduce risk.

### Phase 1: Project foundation
- Confirm app configuration and project structure
- Define package naming and standard conventions
- Establish review and approval workflow

### Phase 2: Domain models and DTOs
- Create ShortUrl entities and analytics models
- Define request/response DTOs and error models
- Add JPA mapping and validation annotations

### Phase 3: Repository and service interfaces
- Define repository contracts
- Define service contracts for create/read/update/delete/redirect/analytics
- Document expected behavior for not found, expired, and invalid cases

### Phase 4: Service implementation
- Validate URL format and expiry constraints
- Generate unique short codes
- Enforce short code stability across updates
- Implement redirect logic and structured responses

### Phase 5: REST controller
- Implement all required endpoints
- Return proper HTTP status codes
- Extract headers needed for analytics context
- Handle custom exception flow

### Phase 6: Kafka analytics integration
- Add event producer and consumer
- Add deduplication logic
- Aggregate summary and geo analytics

### Phase 7: Global exception handling and validation
- Add service and controller exception mapping
- Standardize error response structure
- Validate error scenarios against API contract

### Phase 8: Testing and validation
- Unit and integration tests
- Maven verification
- Final risk review and sign-off

## Dependencies
- Domain models must exist before service interfaces and service logic
- Service logic must exist before controllers
- Kafka and controller logic must be in place before validation completes

## Sign-off requirement
Each phase requires explicit engineer review before work can continue.
