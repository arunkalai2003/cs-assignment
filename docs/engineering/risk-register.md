# Risk Register

## Risk: invalid URL input
Owner: Engineer
Mitigation: URL validation in the service layer and request validation on API boundaries
Status: Covered

## Risk: duplicate short codes
Owner: Engineer
Mitigation: unique constraint and short code existence check before insert
Status: Covered

## Risk: expired link resolution
Owner: Engineer
Mitigation: block redirect and summary access when expiry is reached
Status: Covered

## Risk: duplicate Kafka analytics processing
Owner: Engineer
Mitigation: deduplicated event repository keyed by event ID
Status: Covered

## Risk: internal failure visibility
Owner: Engineer
Mitigation: explicit exception handler and structured error payloads
Status: Covered

## Risk: untracked AI-generated changes
Owner: Engineer
Mitigation: prompt log, change evidence, and approval log
Status: Planned

## Risk: undocumented assumptions
Owner: Engineer
Mitigation: requirement mapping and final engineering summary
Status: Planned
