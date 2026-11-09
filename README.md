# Fair Price

Fair Price is an AI-powered recommerce platform that enables users to buy and sell second-hand products with confidence. Every listing is priced using AI-generated fairness scores, giving sellers a data-driven starting price and buyers assurance that they are paying a fair market rate.

## Overview

Traditional second-hand marketplaces rely on sellers to guess a price and buyers to negotiate blindly. Fair Price removes the guesswork by analyzing product condition, category, market trends, and historical sale data to generate an objective price recommendation for every item. Users can operate as both buyers and sellers within the same platform.

## Key Features

- **AI-Generated Pricing** — Every product listing receives a fairness score and suggested price powered by an AI pricing engine.
- **Dual-Role Users** — A single account can list products for sale and purchase products from other users.
- **Product Listings** — Sellers can create, update, and manage listings for their second-hand items.
- **Transaction Management** — Track the lifecycle of a purchase from offer to completion.
- **Transparent Marketplace** — Buyers and sellers see consistent, AI-backed pricing signals to support fair negotiation.

## Tech Stack

- **Backend:** Java, Spring Boot
- **Build Tool:** Maven
- **Architecture:** Modular, domain-oriented REST API

## Project Structure

The backend follows a domain-oriented module structure (`user`, `product`, `listing`, `pricing`, `transaction`) alongside shared `common` components (configuration, security, exception handling, and utilities). See [`backend/`](./backend) for details.

## Getting Started

### Prerequisites

- Java 25+
- Maven 3.9+

### Build

```bash
cd backend
mvn clean install
```

### Run

```bash
cd backend
mvn spring-boot:run
```

## Status

This project is under active development.
