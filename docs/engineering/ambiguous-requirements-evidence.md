# Ambiguous Requirement Evidence

## What this means in this project
Not every requirement is perfectly explicit at the beginning. This project deliberately captured ambiguous areas and resolved them through engineer review and documented assumptions.

## Project-specific use cases
The following items were clarified before implementation proceeded:

1. Auth is out of scope
   - This was explicitly confirmed to avoid adding authentication features to a URL shortener that was being built under a narrower scope.

2. Custom short-alias support is not included
   - The team agreed that short codes would be randomly generated only.

3. Analytics retention should be 60 days
   - This was fixed as a requirement and implemented in the retention cleanup logic.

4. Delete behavior is hard delete
   - The system does not use soft delete; it removes the record directly.

5. Expired links must not resolve
   - Redirect resolution checks expiration before returning the redirect target.

6. Redirect should return a URL string from controller layer
   - This was confirmed as an implementation constraint during review.

7. Analytics should initialize to zero on first click
   - The summary logic was defined to initialize analytics values on the first valid redirect event.

## Implementation evidence in the project
These clarifications are reflected in:

- `docs/engineering/project-overview.md`
- `docs/traceability/prompt-log.md`
- `docs/engineering/requirement-mapping.md`
- `src/main/java/com/company/shortener/application/service/impl/UrlShortenerServiceImpl.java`
- `src/main/java/com/company/shortener/infrastructure/kafka/consumer/UrlRedirectEventConsumer.java`
- `src/main/java/com/company/shortener/infrastructure/analytics/AnalyticsRetentionService.java`

## Why this matters
Without these clarifications, the team could have introduced scope creep or inconsistent behavior. For example:
- adding auth would have changed the project scope
- allowing custom aliases would have changed generation logic
- soft delete would have changed lifecycle semantics
- tracking analytics incorrectly would have affected business trust in the metrics

## Review-based resolution model
The project does not rely on implicit assumptions. Instead, it captures assumptions and resolved decisions in review records and project docs, then uses those as implementation constraints for the remainder of the work.

This is a critical part of engineer-led delivery: ambiguous requirements are normalized into fixed implementation rules before development proceeds.
