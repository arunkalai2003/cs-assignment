# AI Governance and Execution Model

## Purpose
This project uses AI as an execution accelerator, not as an autonomous owner of correctness or release decisions.

## Rules
- The engineer defines the task, constraints, acceptance criteria, and technical context.
- AI may generate code, tests, documentation, and review notes within the task boundary.
- The engineer is responsible for correctness, maintainability, and production readiness.
- High-impact changes require explicit human sign-off before merging or shipping.
- Generated output must be reviewed for requirement alignment, risk, and technical correctness.

## Required task structure
Each AI-assisted task should include:
- goal
- constraints
- files to be edited or reviewed
- validation command
- acceptance criteria
- reviewer

## Traceability expectation
The project keeps a record of:
- generation requests
- resulting code or docs
- whether the output was accepted, revised, or rejected
- rationale for each decision

## Rejection policy
Any AI-generated output that is incomplete, unsafe, misaligned with requirements, or unvalidated must be rejected with rationale and revised by the engineer.

## Human approval gates
- Phase transitions require approval
- High-impact changes require explicit sign-off
- Final completion requires engineer review of the full work product
