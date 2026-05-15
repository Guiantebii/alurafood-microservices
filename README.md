#  AluraFood Microservices

Microservices-based backend application developed during Alura Spring Boot courses.

The project simulates a restaurant ecosystem using a distributed architecture with Spring Cloud, RabbitMQ, API Gateway, and Service Discovery.

---

##  Architecture

The system is composed of multiple independent microservices:

- **Server** → Eureka Service Discovery
- **Gateway** → API Gateway and request routing
- **Pedidos** → Order management service
- **Pagamentos** → Payment processing service
- **Avaliacao** → Asynchronous payment evaluation service

---


##  Communication Flow

- REST communication between services
- Asynchronous communication using RabbitMQ
- Event-driven architecture for payment processing
- Service registration and discovery with Eureka

---

##  Technologies Used

- Java
- Spring Boot
- Spring Cloud
- Spring Cloud Gateway
- Eureka Server
- RabbitMQ
- Spring Data JPA
- Flyway
- Maven
- Docker
- MySQL

---

##  Microservices Concepts Practiced

- API Gateway Pattern
- Service Discovery
- Distributed Systems
- Event-Driven Architecture
- Asynchronous Messaging
- Retry and Dead Letter Queue concepts
- Database migration with Flyway
- Inter-service communication

---

##  Running the Project

Clone the repository:

```bash
git clone git@github.com:Guiantebii/alurafood-microservices.git
```

Navigate to the project folder:

```bash
cd alurafood-microservices
```

### Start RabbitMQ

```bash
docker run -d --rm \
--net alura \
--hostname rabbit3 \
--name rabbit3 \
-p 8087:15672 \
-e RABBITMQ_ERLANG_COOKIE=alura_secret \
rabbitmq:3.10-management
```

### Run the Microservices

The services should be started in the following order:

1. **Server (Eureka Service Discovery)**
2. **Gateway**
3. **Pedidos**
4. **Pagamentos**
5. **Avaliacao**

Navigate to each microservice folder and run:

```bash
./mvnw spring-boot:run
```

Example:

```bash
cd server
./mvnw spring-boot:run
```

Then repeat the process for the remaining services.

---

##  API Reference

### Pedidos Service

#### Create Order

```http
POST /pedidos
```

#### Get Order By Id

```http
GET /pedidos/{id}
```

---

### Pagamentos Service

#### Create Payment

```http
POST /pagamentos
```

#### Confirm Payment

```http
PUT /pagamentos/{id}/confirmar
```

---

### Gateway

All requests are routed through the API Gateway using Spring Cloud Gateway.

---

### RabbitMQ Events

The system also uses asynchronous communication between services through RabbitMQ events for payment processing and evaluation.

---

##  Learning Goals

This project was created to study and practice:

- Microservices architecture
- Cloud-native applications
- Messaging systems with RabbitMQ
- Spring ecosystem tools
- Distributed communication patterns

---
