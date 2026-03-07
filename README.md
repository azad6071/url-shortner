# url-shortner

## Local dev (PostgreSQL via Docker)

Start Postgres:

```bash
docker compose up -d
```

Run the app:

```bash
./mvnw spring-boot:run
```

DB connection details (match `src/main/resources/application.properties`):
- host: `localhost`
- port: `5432`
- database: `url_shortener`
- username: `url_shortener`
- password: `url_shortener`
