# Greenfield Scenario Evidence

## What we built
This project is a greenfield Java 21 + Spring Boot 3 URL shortener service created from scratch. The implementation includes a new REST API, persistence layer, event-driven analytics flow, and supporting documentation.

## Project-specific use case
A customer wants to shorten a production URL such as:

- `https://www.example.com/products/sku-9876?source=campaign` 

The system should:
1. Accept the long URL via `POST /api/v1/urls`
2. Validate it as a proper URL
3. Generate a unique short code such as `abC12x`
4. Store it in PostgreSQL
5. Return a shortened URL response
6. Allow later redirect resolution via `GET /api/v1/urls/{shortCode}/redirect`

## Implementation evidence in the project
The greenfield build is visible in the actual codebase:

- `src/main/java/com/company/shortener/ShortenerApplication.java` starts the new Spring Boot application.
- `src/main/java/com/company/shortener/api/controller/UrlShortenerController.java` contains the API entry points.
- `src/main/java/com/company/shortener/application/service/impl/UrlShortenerServiceImpl.java` implements the create, update, redirect, delete, and validation behavior.
- `src/main/java/com/company/shortener/domain/model/ShortUrl.java` defines the domain object for the shortened URL.
- `src/main/java/com/company/shortener/infrastructure/kafka/producer/UrlRedirectEventProducer.java` emits redirect events for analytics processing.
- `src/main/java/com/company/shortener/infrastructure/kafka/consumer/UrlRedirectEventConsumer.java` consumes the events and updates analytics.

## Why this qualifies as greenfield
This is clearly a new system, not a patch to an existing service. We created:
- a new project structure
- new package boundaries (`api`, `application`, `domain`, `infrastructure`)
- a fresh REST API contract
- new persistence and Kafka integration logic
- a new review and governance model for the project

## Review and approval evidence
The project records this as a greenfield implementation in:
- `docs/engineering/project-overview.md`
- `docs/engineering/phase-plan.md`
- `docs/traceability/approval-log.md`

This means the solution was not assumed to be a refactor or patch; it was intentionally designed as a new build from a documented and approved scope.
