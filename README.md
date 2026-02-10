# PayFlow

## What it is
A wallet-based payment backend built with Spring Boot.

## Features
- User onboarding with auto wallet creation
- Wallet credit & debit
- Transaction ledger
- Optimistic locking
- Idempotent APIs
- Global exception handling

## Tech Stack
- Java 17
- Spring Boot
- Spring Data JPA
- MySqlWorkBench
- Maven

## Design Decisions
- Ledger-based design
- Transactions as source of truth
- Optimistic locking for concurrency
- Idempotency for safe retries

## How to run
mvn spring-boot:run
