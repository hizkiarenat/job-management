# 💼 Job Management System

A microservices-based job management platform built with **Spring Boot**, leveraging modern infrastructure including service discovery, message queuing, distributed tracing, and caching.

---

## 🏗️ Architecture Overview

```
                        ┌─────────────┐
                        │   Gateway   │  :9000
                        └──────┬──────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
   ┌──────▼──────┐    ┌────────▼───────┐   ┌───────▼──────┐
   │ job-service │    │company-service │   │review-service│
   │   :1000     │    │    :2000       │   │    :3000     │
   └──────┬──────┘    └────────┬───────┘   └───────┬──────┘
          │                    │                    │
          └────────────────────┼────────────────────┘
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
 ┌──────▼──────┐       ┌───────▼──────┐      ┌───────▼──────┐
 │  PostgreSQL │       │   RabbitMQ   │      │    Redis     │
 │   :5432     │       │  :5672/15672 │      │    :6379     │
 └─────────────┘       └─────────────┘      └─────────────┘

        ┌───────────────────────────────────┐
        │  service-regis (Eureka)  :1234    │
        └───────────────────────────────────┘

        ┌───────────────────────────────────┐
        │       Zipkin Tracing  :9411       │
        └───────────────────────────────────┘
```

---

## 🧩 Services

| Service | Port | Description |
|---|---|---|
| `gateway` | `9000` | API Gateway — single entry point for all client requests |
| `job-service` | `1000` | Manages job listings and related operations |
| `company-service` | `2000` | Manages company profiles and data |
| `review-service` | `3000` | Handles company/job reviews and ratings |
| `service-regis` | `1234` | Service registry (Eureka) for service discovery |
| `zipkin` | `9411` | Distributed tracing UI |
| `rabbitmq` | `5672` / `15672` | Message broker for async communication |
| `postgres` | `5432` | Relational database |
| `redis` | `6379` | Caching layer for job-service |

---

## 🛠️ Tech Stack

- **Java Spring Boot** — Core framework for all microservices
- **Spring Cloud Gateway** — API Gateway
- **Spring Cloud Netflix Eureka** — Service discovery & registration
- **PostgreSQL 15** — Primary relational database
- **Redis 7** — In-memory caching
- **RabbitMQ 3** — Async messaging between services
- **Zipkin** — Distributed tracing
- **Docker & Docker Compose** — Containerization and orchestration

---

## 📋 Prerequisites

Make sure you have the following installed:

- [Docker](https://www.docker.com/get-started) (v20+)
- [Docker Compose](https://docs.docker.com/compose/) (v2+)
- [Java 17+](https://adoptium.net/) *(for local development)*
- [Maven](https://maven.apache.org/) *(for local development)*

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/hizkiarenat/job-management.git
cd job-management
```

### 2. Run with Docker Compose

```bash
docker-compose up --build
```

To run in detached mode:

```bash
docker-compose up --build -d
```

### 3. Verify services are running

```bash
docker-compose ps
```

---

## 🌐 Accessing the Services

| Service | URL |
|---|---|
| API Gateway | http://localhost:9000 |
| Job Service | http://localhost:1000 |
| Company Service | http://localhost:2000 |
| Review Service | http://localhost:3000 |
| Eureka Dashboard | http://localhost:1234 |
| RabbitMQ Management UI | http://localhost:15672 |
| Zipkin Tracing UI | http://localhost:9411 |

### RabbitMQ Credentials
```
Username: admin
Password: admin123
```

### PostgreSQL Credentials
```
Username: admin
Password: admin
```

---

## 📁 Project Structure

```
job-management/
├── docker-compose.yaml
├── init-db/                  # SQL scripts for database initialization
├── gateway/                  # API Gateway service
├── service-regis/            # Eureka service registry
├── job-service/              # Job management microservice
├── company-service/          # Company management microservice
└── review-service/           # Review microservice
```

---

## ⚙️ Environment Variables

### job-service
| Variable | Value | Description |
|---|---|---|
| `SPRING_RABBITMQ_HOST` | `rabbitmq` | RabbitMQ host |
| `SPRING_RABBITMQ_PORT` | `5672` | RabbitMQ AMQP port |
| `SPRING_REDIS_HOST` | `redis` | Redis host |
| `SPRING_REDIS_PORT` | `6379` | Redis port |
| `MANAGEMENT_ZIPKIN_TRACING_ENDPOINT` | `http://zipkin:9411/api/v2/spans` | Zipkin endpoint |

### company-service & review-service
| Variable | Value | Description |
|---|---|---|
| `SPRING_RABBITMQ_HOST` | `rabbitmq` | RabbitMQ host |
| `SPRING_RABBITMQ_PORT` | `5672` | RabbitMQ AMQP port |
| `MANAGEMENT_ZIPKIN_TRACING_ENDPOINT` | `http://zipkin:9411/api/v2/spans` | Zipkin endpoint |

---

## 🔄 Service Dependencies & Startup Order

The services start in the following order to ensure all dependencies are ready:

```
postgres, redis, rabbitmq, zipkin
        ↓
   service-regis
        ↓
  job-service, company-service, review-service
        ↓
      gateway
```

> **Note:** `rabbitmq` and `redis` have health checks configured. Dependent services will wait until these are healthy before starting.

---

## 🛑 Stopping the Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clears all data)
docker-compose down -v
```

---

## 📊 Monitoring & Observability

### Distributed Tracing (Zipkin)
All services are configured with 100% tracing sampling (`MANAGEMENT_TRACING_SAMPLING_PROBABILITY=1.0`). Visit [http://localhost:9411](http://localhost:9411) to view traces across services.

### RabbitMQ Management
Monitor queues, exchanges, and message rates at [http://localhost:15672](http://localhost:15672).

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'feat: add your feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## 📄 License

This project is open source. See the [LICENSE](LICENSE) file for details.
