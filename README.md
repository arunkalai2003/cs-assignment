# URL Shortener Service

A Java 21 Spring Boot 3 application that creates, stores, resolves, and monitors short URLs. The system uses PostgreSQL for persistence, Kafka for redirect analytics events, and Spring Boot Actuator for health and readiness monitoring.

## Project overview

This project implements a URL shortener REST service with the following core capabilities:

- Create short links from long URLs
- Validate and reject invalid or unsafe URLs
- Resolve short links through redirect behavior
- Block expired and missing records from redirect resolution
- Delete links with a hard delete
- Track redirect analytics (click totals and geo distribution)
- Deduplicate Kafka analytics events to prevent double counting
- Apply 60-day analytics retention cleanup
- Expose OpenAPI Swagger documentation and actuator health endpoints

## Current implementation status

The project includes the following completed functional areas:

- REST API layer for create/read/update/delete/redirect/analytics endpoints
- Service-layer validation and domain logic
- JPA persistence for short URL metadata and analytics state
- Kafka redirect event producer and consumer
- Duplicate event handling for analytics processing
- Structured API error responses and global exception handling
- Docker and Docker Compose runtime configuration
- Environment-variable-driven configuration for PostgreSQL and Kafka
- Actuator health endpoint and OpenAPI documentation support

## Tech stack

- Java 21
- Spring Boot 3.3.x
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Kafka
- Spring Actuator
- Springdoc OpenAPI
- PostgreSQL
- Kafka
- Maven
- Docker / Docker Compose

## Architecture

The project follows a layered Spring Boot design:

- API layer: controllers, DTOs, request validation, HTTP response logic
- Application layer: business services, validation, transformations, domain orchestration
- Domain layer: entity models, repository contracts, rules around expiry and redirect behavior
- Infrastructure layer: Kafka integration, persistence adapters, and analytics processing

## Configuration

The application reads configuration from `src/main/resources/application.properties` and supports environment-variable overrides.

Typical variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/url_shortener
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
APP_BASE_URL=http://localhost:8080/
APP_KAFKA_TOPICS_URL_REDIRECT_EVENTS=url-shortener.redirect-events
APP_ANALYTICS_RETENTION_DAYS=60
APP_ANALYTICS_RETENTION_CRON=0 0 2 * * ?
```

A sample `.env.example` file is provided at the project root.

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL running locally or via Docker Compose
- Kafka running locally or via Docker Compose
- Docker and Docker Compose for container-based local setup

## Build and run

### Build the application

```bash
mvn clean install
```

### Run locally

```bash
mvn spring-boot:run
```

### Run with Docker Compose

```bash
docker compose up --build
```

This starts the application, PostgreSQL, and Kafka together.

### Run single container image

```bash
docker build -t url-shortener-service .
docker run -p 8080:8080 --env-file .env.example url-shortener-service
```

## API documentation and health

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health endpoint: `http://localhost:8080/actuator/health`
- Readiness/liveness: exposed via actuator health endpoint

## Example endpoints

```http
POST /api/v1/urls
GET /api/v1/urls/{shortCode}
PUT /api/v1/urls/{shortCode}
DELETE /api/v1/urls/{shortCode}
GET /api/v1/urls/{shortCode}/redirect
GET /api/v1/urls/{shortCode}/analytics
```

## Analytics retention policy

The system retains analytics data for 60 days and runs a scheduled cleanup job to remove expired analytics records.

## Notes

This project is intentionally engineer-led and review-controlled. Each implementation phase requires explicit human review before progression, and the generated artifacts are tracked in the project governance and traceability documentation.
