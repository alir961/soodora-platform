# Soodora API

The Soodora REST API: Java 21, Spring Boot 3.5, MariaDB with Flyway migrations, multi-module Maven project (`api`, `service`, `domain`, `infrastructure`, `test`, `coverage`). See the [root README](../README.md) for the whole project, supported platforms and the front ends.

## Requirements

- Java 21 (JDK)
- Docker with the Compose plugin, for MariaDB and the Mailpit mail catcher (or for the whole stack)
- Maven is not needed: use the wrapper, `./mvnw` (`mvnw.cmd` on Windows)

## Configuration

```bash
cp .env.template .env
```

Set `JWT_SECRET` to a base64 key of at least 512 bits (`openssl rand -base64 64`). The other keys are optional: Google (sign in with Google), Stripe (marketplace checkout), Cloudinary (uploads). SMTP defaults to the Mailpit container; the comments in `.env.template` show a real provider. The database connection defaults to `jdbc:mariadb://localhost:3306/database` with user `user` / `password`, and can be changed with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD`.

## Run

With Docker (API, MariaDB and Mailpit):

```bash
docker compose up --build -d     # API on http://localhost:8080, mail inbox on http://localhost:8025
docker compose logs -f api
docker compose down              # add -v to delete the database volume
```

Locally with hot reload, the database and Mailpit in Docker:

```bash
docker compose up -d database mailpit
./mvnw install -DskipTests
set -a; . ./.env; set +a
SMTP_HOST=localhost ./mvnw -pl api spring-boot:run
```

## Test and build

```bash
./mvnw test        # unit tests
./mvnw verify      # unit + integration tests (in-memory H2), JaCoCo coverage, Spotless check; builds api/target/api-0.0.1-SNAPSHOT.jar
./mvnw spotless:apply
```

`scripts/drop-local-database.sh` drops every table of the local database (needs the `mariadb` client); Flyway recreates them at the next start.

## API documentation

Swagger UI: http://localhost:8080/swagger-ui.html, OpenAPI JSON: http://localhost:8080/v3/api-docs
