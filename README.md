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
