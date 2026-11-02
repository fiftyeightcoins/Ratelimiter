# Rate Limiter Service (`myRateLimiter`)

A flexible, tiered API rate-limiting and throttling microservice built with **Spring Boot** and **Redis**.

It supports hierarchical throttling policies allowing limits at three different granularities:
1. **DEFAULT**: Global rate limit applied to all requests by a client.
2. **METHOD**: Rate limit scoped to a specific HTTP method (e.g., `GET`, `POST`).
3. **API**: Rate limit scoped to a specific endpoint (e.g., `/orders`, `/payments`).

State and client configurations are persisted directly in **Redis** via **Jedis**.

---

## Tech Stack & Prerequisites

- **Java**: 21+
- **Spring Boot**: 3.2.x (Web, Jackson)
- **Database / Cache**: Redis (Cloud or Local instance)
- **Redis Client**: Jedis (`redis.clients:jedis:5.1.2`)
- **Build Tool**: Maven

---

## Configuration

Redis connection settings are configured in [`src/main/resources/application.properties`](file:///home/harshal/projects/Rate-Limiter/src/main/resources/application.properties):

```properties
server.port=8081

# Redis Configuration
redis.host=redis-....-1.ec2.cloud.redislabs.com
redis.port=15026
redis.user=default
redis.password=t8zJVZrehw.....
```

---

## Running the Application

Run the application with Maven:
```bash
mvn spring-boot:run
```
The server starts on port `8081` by default.

---

## How to Use the Rate Limiter

### 1. Configure Client Rate Limits

Register a client and define its limits using `POST /throttling/configure-client`.

**Endpoint:** `POST /throttling/configure-client`  
**Headers:** `Content-Type: application/json`

#### Request Body Example
```json
{
  "clientId": "client-101",
  "limits": [
    {
      "limitType": "DEFAULT",
      "limitName": "default",
      "timeIntervalLimits": [
        {
          "timeUnit": "MIN",
          "maxRequests": 100
        }
      ]
    },
    {
      "limitType": "METHOD",
      "limitName": "POST",
      "timeIntervalLimits": [
        {
          "timeUnit": "SEC",
          "maxRequests": 5
        }
      ]
    },
    {
      "limitType": "API",
      "limitName": "/checkout",
      "timeIntervalLimits": [
        {
          "timeUnit": "MIN",
          "maxRequests": 10
        }
      ]
    }
  ]
}
```

#### Response
```json
{
  "message": "Client configured successfully",
  "status": "SUCCESS"
}
```

---

### 2. Verify an API Request (Throttling Check)

Before serving an incoming request, downstream gateways or services invoke `/throttling/verify-api-limit`.

**Endpoint:** `POST /throttling/verify-api-limit`  
**Headers:** `Content-Type: application/json`

#### Request Body
```json
{
  "clientId": "client-101",
  "methodname": "POST",
  "apiName": "/checkout"
}
```

#### Responses

- **Allowed (Permit granted):**
  ```json
  {
    "message": "success",
    "status": "Success"
  }
  ```

- **Throttled (Rate limit breached):**
  ```json
  {
    "message": "Limit: /checkout, limitType: API, timeUnit: MIN breached for the client",
    "status": "FAILURE"
  }
  ```

- **No limits applicable:**
  ```json
  {
    "message": "nolimitapplicable",
    "status": "SUCCESS"
  }
  ```

---

### 3. View Configured Limits

View all rate-limit rules currently stored in Redis:

**Endpoint:** `GET /throttling/configured-limits`

---

## Redis Storage Scheme

The data is saved directly in Redis:
- `clients`: Redis Set storing all client IDs (`SADD clients <clientId>`).
- `client_limits:<clientId>`: Redis Set storing rule IDs associated with a specific client.
- `ratelimit:<clientId>:<limitId>`: Redis String key storing the serialized JSON of `ClientRateLimitData`.
