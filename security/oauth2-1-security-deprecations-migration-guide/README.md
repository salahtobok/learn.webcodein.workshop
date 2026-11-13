# OAuth 2.1 Security Deprecations and Migration Guide

Companion workshop project for the article: **OAuth 2.1 Security Deprecations and Migration Guide: Securing Modern Backend APIs**.

## Tech Stack
- **Language**: Java 25
- **Architecture**: Domain Model, PKCE Engine, OAuth 2.1 Strict Validator, ArchUnit
- **Testing**: JUnit 5, AssertJ, ArchUnit 1.4.1
- **Spring Boot**: None (pure Java 25 runtime)

## Architecture Overview
The project models the core architectural hardening requirements standardized in OAuth 2.1 (draft-ietf-oauth-v2-1):
1. **Implicit Grant Prohibited**: Rejects `response_type=token` to prevent token leakage via referrer and browser history.
2. **Resource Owner Password Credentials Prohibited**: Rejects `grant_type=password` to eliminate credential forwarding antipatterns.
3. **Mandatory PKCE for All Clients**: Enforces SHA-256 (`S256`) code challenge and verifier on all authorization code redemptions.
4. **Exact Redirect URI Matching**: Strictly rejects wildcards and directory traversals in redirect URIs.
5. **Sender-Constrained Tokens**: Architectural support for DPoP (RFC 9449) proof verification.

## Running Tests
```bash
mvn clean test
```
