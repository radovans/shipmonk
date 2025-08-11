# Shipmonk Application

A Spring Boot application for Shipmonk.

## Database Setup

### Start PostgreSQL Database

```bash
# Start the PostgreSQL database using Docker Compose
docker-compose up -d
```

### Stop Database

```bash
# Stop and remove volumes
docker-compose down -v
```

### Recreate Database

```bash
# Stop the database and remove volumes, then start it again
docker-compose down -v && docker-compose up -d
```

### Run Flyway Migrations

```bash
# Run Flyway migrations to set up the database schema
mvn clean flyway:migrate
```

### Reset Database

```bash
# Reset the database by dropping and recreating it
docker-compose down -v && docker-compose up -d && mvn clean flyway:migrate
```

## Additional Information

- Run tests with `mvn clean test`
- Run checkstyle with `mvn checkstyle:checkstyle` - report will be generated in `/target/reports/checkstyle.html`
    ```bash
    open target/reports/checkstyle.html
    ```
- Run Spotbugs with `mvn spotbugs:spotbugs` - report will be generated in `/target/site/spotbugs.html`
    ```bash
    open target/site/spotbugs.html
    ```
