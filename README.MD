# SIFPI - Sistem Informasi Fasilitator Projek IPFO

## Project Structure

This is a Spring Boot application built with Java 21 and Gradle. The project follows a **feature-based architecture** where code is organized by business domain rather than technical layers.

```
sifpi-be/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── id/go/kemenkoinfra/ipfo/sifpi/
│   │   │       ├── SifpiApplication.java          # Main application entry point
│   │   │       ├── common/                        # Shared/common components
│   │   │       │   ├── config/                    # Configuration classes
│   │   │       │   │   ├── CacheConfig.java       # Cache configuration (Caffeine)
│   │   │       │   │   ├── CorsConfig.java        # CORS configuration
│   │   │       │   │   └── SecurityConfig.java    # Security configuration
│   │   │       │   ├── constants/                 # Application constants
│   │   │       │   ├── dto/                       # Shared DTOs
│   │   │       │   ├── enums/                     # Shared enumerations
│   │   │       │   ├── exception/                 # Exception handling
│   │   │       │   ├── security/                  # Security components
│   │   │       │   └── utils/                     # Utility classes
│   │   │       └── example/                       # Example feature module
│   │   │           ├── controller/                # REST controllers
│   │   │           │   └── ExampleController.java
│   │   │           ├── service/                   # Business logic
│   │   │           │   └── ExampleService.java
│   │   │           ├── repository/                # Data access layer
│   │   │           │   └── ExampleRepository.java
│   │   │           ├── model/                     # Domain entities
│   │   │           │   └── Example.java
│   │   │           └── dto/                       # Feature-specific DTOs
│   │   │               └── ExampleDTO.java
│   │   └── resources/
│   │       ├── application.yaml                   # Main configuration file
│   │       ├── application-dev.yaml               # Development profile config
│   │       ├── application-prod.yaml              # Production profile config
│   │       ├── logback.xml                        # Logging configuration
│   │       └── static/                            # Static resources
│   └── test/
│       └── java/                                  # Test classes
├── build.gradle                                   # Gradle build configuration
├── docker-compose.yaml                            # Docker Compose setup
├── Dockerfile                                     # Docker image definition
├── .gitlab-ci.yml                                 # CI/CD pipeline configuration
└── .env.example                                   # Environment variables template
```

### Architecture: Feature-Based Organization

This project uses **feature-based packaging** instead of the traditional layer-based approach. Each feature/domain module contains its own controllers, services, repositories, models, and DTOs.

**Why this approach?**

- **High Cohesion**: Related code stays together, making it easier to understand and modify a specific feature without navigating multiple directories
- **Low Coupling**: Features are isolated from each other, reducing unintended dependencies between different parts of the application
- **Team Scalability**: Multiple developers can work on different features simultaneously with minimal merge conflicts
- **Better Encapsulation**: Each feature can hide its implementation details, exposing only what's necessary through well-defined interfaces
- **Easier Maintenance**: When a feature needs changes or removal, all related code is in one place rather than scattered across controller/, service/, and repository/ packages

**Common package**: Contains shared components used across multiple features (security, utilities, base DTOs, global exception handlers, configurations)

## Running locally

Below are the steps to run this project locally

### Clone this repository

Clone this repository with `git clone {PROJECT_URL}`. You can view the project URL in Code dropdown at the top of this repository.

### Creating `.env` files

Make sure to create a .env file at the root of this project. Follow the example in `.env.example`, we have already setup the needed configurations there for development purposes. You can adjust it with your preferred environtment if needed.

### With Docker

- Make sure that Docker is installed and is currently running on your device
- Run `docker compose up -d` in your terminal.
- Project should be running and you can do a healthcheck at `http://localhost:8080/actuator/health`

### With Gradle

- Run `./gradlew bootRun` in your terminal.
- Project should be running and you can do a healthcheck at `http://localhost:8080/actuator/health`
