# Grocery Store Web App (Microservices + API Gateway)

A distributed Spring Boot project that simulates a grocery store system. Each service manages a different domain (Customer, Product, Payment, and Order) while an API Gateway routes requests between them.

---

## Overview

This project demonstrates a simple microservice architecture built with Spring Boot, MySQL, and Docker. It uses an API Gateway to centralize access, route traffic, and aggregate responses from multiple backend services.

---

## Features

* Individual Spring Boot microservices for each module
* REST APIs for CRUD operations
* API Gateway for routing and unified access
* Docker Compose for containerized deployment
* MySQL and MongoDB databases
* Unit and integration tests with JUnit

---

## Tech Stack

* **Language:** Java (Spring Boot 3.x)
* **Build Tool:** Gradle 8.x
* **Databases:** MySQL, MongoDB
* **Tools:** Docker Compose, MapStruct, Lombok, JaCoCo, JUnit

---

## Setup Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/CunninghamLi/grocerystorewebapp.git
   ```
2. Open the project in IntelliJ IDEA or VS Code.
3. Ensure Docker is installed and running.
4. Build and start all services:

   ```bash
   docker compose up --build
   ```
5. Access the API Gateway at:

   ```
   http://localhost:8080
   ```

---

## Microservices Overview

| Service          | Description                                 | Port |
| ---------------- | ------------------------------------------- | ---- |
| Customer-Service | Handles customer registration and retrieval | 8081 |
| Product-Service  | Manages product catalog and inventory       | 8082 |
| Payment-Service  | Simulates payment processing                | 8083 |
| Order-Service    | Aggregates data from other services         | 8084 |
| API-Gateway      | Central routing point for all APIs          | 8080 |

---

## Folder Structure

```
grocery-store-microservices/
├── api-gateway/
├── customer-service/
├── product-service/
├── payment-service/
├── order-service/
├── docker-compose.yml
```

---

## Example Endpoints

* **Customer Service:** `GET /api/customers`
* **Product Service:** `GET /api/products`
* **Order Service:** `POST /api/orders`
* **Payment Service:** `POST /api/payments`
* **Gateway:** `GET /api/orders/{id}`

---

## How It Works

* Each service runs independently with its own database.
* The API Gateway routes requests to specific services.
* The Order Service collects customer, product, and payment info from other APIs.
* Docker Compose manages container orchestration.

---


## License

Created for educational purposes — free to use and modify.
