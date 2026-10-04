# TDD Zero-Mocking Demo

This project demonstrates Test-Driven Development (TDD) using a zero-mocking approach with Spring Boot 4, Java 25, and Testcontainers.

## Prerequisites
- Java 25
- Docker

## Running Tests
Tests use Testcontainers to spin up a real PostgreSQL database:
`mvn clean verify`

## Running Locally
Start the database:
`docker-compose up -d`

Start the app:
`mvn spring-boot:run`
