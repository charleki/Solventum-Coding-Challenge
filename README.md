# ShortLink - URL Shortening Service

## Features

- Encode long URLs into short URLs
- Decode short URLs back to original
- Configurable concurrency limit per endpoint

## API Endpoints

### `POST /encode`

**Request:**

```json
{
  "originalUrl": "https://example.com/some/long/url"
}
```

**Response:**

```json
{
  "shortUrl": "http://short.est/abc123"
}
```

### `POST /decode`

**Request:**

```json
{
  "shortUrl": "http://short.est/abc123"
}
```

**Response:**

```json
{
  "originalUrl": "https://example.com/some/long/url"
}
```

## Requirements

- Java 17+
- Maven

## Build

```bash
mvn clean install
```

## Run the Application

You can configure concurrency limits at runtime using command-line arguments:

```bash
java -jar target/shortlink-0.0.1-SNAPSHOT.jar \
  --shortlink.concurrency.encode.request-limit=10 \
  --shortlink.concurrency.decode.request-limit=10
```

## Notes

Concurrency limits are enforced using a Semaphore directly in the controller for simplicity and clarity. In a production
environment, this logic could be moved to an interceptor for better scalability and separation of concerns.
