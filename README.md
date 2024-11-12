# GrooveConductor API

GrooveConductor is a Java-based API for managing musical tracks, users, and playlists, developed with Spring Boot.

[![pipeline status](https://gitlab.com/groove-conductor-eip/api/badges/main/pipeline.svg)](https://gitlab.com/groove-conductor-eip/api/-/commits/main)
[![coverage report](https://gitlab.com/groove-conductor-eip/api/badges/main/coverage.svg)](https://gitlab.com/groove-conductor-eip/api/-/commits/main) 

## Getting Started

### Prerequisites

Make sure you have the following installed:

- Java 21 or later
- Maven 3.8+
- Docker
- Docker Compose

### Setup Environment

#### 1. Clone the repository
```bash
git clone git@gitlab.com:groove-conductor-eip/api.git && cd api
```

#### 2. Install the project dependencies

To install the project dependencies without running tests, use:

```bash
mvn clean install -DskipTests
```

#### 3. Run tests

Run the unit and integration tests with the following commands:

```bash
mvn test  
```

```bash
mvn integration-test
```

### Run application

#### \> Running in dev mode (hot-reload)

```bash
mvn spring-boot:run
```

#### \> Running with Docker

You can orchestrate the API and the database using Docker Compose:

```bash
docker compose up --build -d
```

#### \> Compile application

```bash
mvn verify
```

JAR can be found at `rest/target/rest-*.jar`

### API Documentation

The API is documented using OpenAPI (Swagger).   
Once the service is running, you can access the API docs at: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

