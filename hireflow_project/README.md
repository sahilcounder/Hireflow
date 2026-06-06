# HireFlow — AI-Powered Hiring Platform

> Microservices-based hiring platform with AI resume screening via Groq (free tier).

## Architecture
- **Auth Service** (8081) — JWT auth, BCrypt passwords
- **Job Service** (8082) — Job CRUD with role-based access
- **Application Service** (8083) — Apply with resume upload, Kafka events
- **AI Screening Service** (8084) — Groq LLM scores resumes vs JDs
- **Notification Service** (8085) — Email alerts via MailHog
- **API Gateway** (8080) — Single entry point, JWT validation
- **Eureka** (8761) — Service discovery

## Quick Start

```bash
# 1. Clone and create .env
cp .env.example .env
# Edit .env: add your Groq API key from https://console.groq.com

# 2. Start everything
docker compose up --build

# 3. View emails (MailHog UI)
open http://localhost:8025

# 4. Service registry
open http://localhost:8761
```

## API Flow

```
1. POST /api/auth/register   -> register as RECRUITER
2. POST /api/auth/register   -> register as CANDIDATE
3. POST /api/jobs            -> recruiter creates job (Bearer token)
4. POST /api/applications    -> candidate applies with resume.pdf
5. GET  /api/applications/job/{jobId}  -> recruiter sees ranked list
6. GET  /api/screening/{applicationId} -> full AI analysis
7. PUT  /api/applications/{id}/status  -> shortlist/reject/interview
```

## Tech Stack
Spring Boot 3.2 · Spring Cloud · Kafka · PostgreSQL · MongoDB · Groq LLaMA · Docker
