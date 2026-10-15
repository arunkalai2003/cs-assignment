# Project Overview

## Objective
This project delivers a Java 21 + Spring Boot 3 URL shortener service that accepts long URLs, generates stable short codes, resolves them safely, and tracks redirect analytics asynchronously through Kafka.

## Scope
- Greenfield implementation of a URL shortener REST API
- Brownfield-ready design for future enhancements, refactors, and bug-fix work
- Persistence in PostgreSQL
- Kafka-based analytics eventing
- Service validation, error handling, and API documentation
- Test coverage for core service and API behavior
- Documentation improvements and governance artifacts to support review and traceability
- Coverage of both well-defined and ambiguous requirements through explicit assumptions and review decisions

## Non-goals
- User authentication and authorization are out of scope for now
- Custom short alias support is not included
- Multi-tenant enterprise admin panels are out of scope
- Full autonomous AI execution is not allowed; the engineer remains the decision owner

## Engineering ownership
The engineer leads execution, validates all implementation decisions, and approves all changes before they reach a production-ready state. AI assists with implementation, testing, and documentation within bounded tasks, but does not own correctness, risk, or final sign-off.

## Assumptions
- PostgreSQL and Kafka are available in the local development environment
- Short codes are random-generated and unique per record
- Delete is a hard delete
- Expired links are blocked from redirect resolution
- A redirect event should be emitted for each valid redirect request
- Duplicate analytics events must be ignored to prevent double counting
- Analytics retention policy is 60 days
- Deployment/runtime environment is assumed to be a standard local or non-production Spring Boot runtime with PostgreSQL and Kafka available at localhost, using environment variables or application.properties for configuration; production deployment details are out of scope for this phase

## Review gate
This document is subject to engineer review and approval before implementation proceeds beyond a phase boundary. Current reviewer and approval owner: Arun Gangatharan. Decision status: approved.
