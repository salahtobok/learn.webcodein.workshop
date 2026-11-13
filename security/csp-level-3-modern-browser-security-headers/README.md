# Content Security Policy (CSP) Level 3: Strict Nonce and Hash Defense

Companion workshop project for the article: **Content Security Policy (CSP) Level 3: Modern Defense Against XSS and Data Exfiltration**.

## Tech Stack
- **Language**: Java 25
- **Architecture**: Domain Model, Policy Builder, Nonce Generator, Hash Evaluator, Reporting Parser, ArchUnit
- **Testing**: JUnit 5, AssertJ, ArchUnit 1.4.1
- **Spring Boot**: None (pure Java 25 runtime)

## Architecture Overview
The project models production-grade Content Security Policy Level 3 mechanisms:
1. **Cryptographic Nonces**: 128-bit secure random nonces injected per HTTP response for script execution.
2. **Strict-Dynamic Propagation**: Allowing trusted root scripts to load sub-dependencies dynamically without fragile domain allowlists.
3. **Subresource Integrity & Hashes**: Cryptographic hashing (SHA-256, SHA-384, SHA-512) for inline scripts.
4. **Violation Reporting Engine**: Normalized parser supporting both legacy `csp-report` and modern W3C Reporting API JSON payloads.

## Running Tests
```bash
mvn clean test
```
