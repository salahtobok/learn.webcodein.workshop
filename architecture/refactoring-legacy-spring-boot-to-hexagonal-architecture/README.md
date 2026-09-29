# Refactoring a Legacy Spring Boot Monolith to Hexagonal Architecture

This workshop project accompanies the article: [Refactoring a Legacy Spring Boot Monolith to Hexagonal Architecture](https://learn.webcodein.com/post/refactoring-legacy-spring-boot-to-hexagonal-architecture/)

## Structure

*   `src/main/java/com/webcodein/workshop/legacy`: Contains the legacy, database-driven (MVC) implementation.
*   `src/main/java/com/webcodein/workshop/refactored`: Contains the target Hexagonal Architecture (Ports and Adapters) implementation.

## How to run the tests

```bash
mvn verify
```
