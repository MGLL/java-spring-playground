# Spring Boot 4

Best-practice examples for building clean, maintainable **Spring Boot 4** applications (Spring Framework 7, Jakarta EE 11, `jakarta.*` namespace).

Each folder isolates one concern and explains the *why* in its own README, not just the code.

## Requirements

- **Java 17 minimum, Java 25 recommended** (Spring Boot 4 has first-class Java 25 support)
- A **Servlet 6.1** compatible container (embedded Tomcat/Jetty; note: Undertow support was removed)
- **Maven**

> Spring Boot 3.x reached end of life in June 2026, so 4.x is the right baseline for new work.

## Contents

### Core best practices

| Folder | Topic | What it shows |
|--------|-------|---------------|
| `project-structure/` | Layout | Layered organisation (controller / service / repository / model) and feature-based packaging |
| `rest-api/` | REST controllers | DTOs instead of exposing entities, correct HTTP status codes, validation with `@Valid` |
| `data-jpa/` | Persistence | Repositories, pagination with `Pageable`, and the N+1 query pitfall with its fix |
| `config-management/` | Configuration | Profiles (`dev` / `prod`), typed `@ConfigurationProperties`, secrets via environment variables |
| `error-handling/` | Error responses | Centralised `@RestControllerAdvice` with a consistent error payload |
| `testing/` | Tests | Unit tests with mocks, `@WebMvcTest` for the web layer, integration tests (bonus: Testcontainers) |
| `security/` | Security | Modern `SecurityFilterChain` config (Spring Security 6), protecting endpoints |

### New in Spring Boot 4

| Folder | Topic | What it shows |
|--------|-------|---------------|
| `api-versioning/` | API Versioning | First-class support for versioning REST endpoints |
| `http-service-clients/` | HTTP Service Clients | Declaring a plain Java interface and letting Spring generate the HTTP client implementation |
| `resilience/` | Resilience | Built-in fault tolerance with `@Retryable`, no external library needed |

## Getting started

Every example is generated from [Spring Initializr](https://start.spring.io) with Spring Boot 4 and the minimal dependencies for its topic. Each folder lists its dependencies and how to run it (`./mvnw spring-boot:run`).

## Conventions

- Targets **Spring Boot 4 / Spring Framework 7** throughout, `jakarta.*` only, never `javax.*`.
- **No secrets in source.** Credentials come from environment variables; example config files use placeholders only.
- DTOs at the API boundary; entities never leak into controllers.
- Lean on the new **JSpecify null-safety** annotations where they add clarity.

## Suggested order

Start with `rest-api/` for a quick, satisfying result, then layer in `data-jpa/`, `error-handling/`, and `config-management/`. Explore the Spring Boot 4 folders (`http-service-clients/`, `api-versioning/`, `resilience/`) once the basics are solid. Leave `security/` for last: it's the densest.

## References

- [Spring Boot 4.0 reference docs](https://docs.spring.io/spring-boot/index.html)
- [Spring Boot 4.0 release announcement](https://spring.io/blog/2025/11/20/spring-boot-4-0-0-available-now/)
- [Spring Boot 4.0 release notes (wiki)](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [Spring guides](https://spring.io/guides)