# ShortLink - URL Shortening Service

## Features
- Encode long URLs into short URLs
- Decode short URLs back to original
- Configurable concurrency limit

## Requirements
- Java 17+
- Maven

## Run the Application
```bash
mvn clean install
java -jar target/shortlink-0.0.1-SNAPSHOT.jar --shortlink.concurrency.encode.request-limit=10 --shortlink.concurrency.decode.request-limit=10
