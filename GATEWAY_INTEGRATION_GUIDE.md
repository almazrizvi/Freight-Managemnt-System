# User Service Integration with API Gateway - Setup Guide

## Overview
The API Gateway now routes all `/api/users/**` requests to the User Service through Eureka service discovery.

## Architecture

```
┌─────────────┐
│  Angular    │
│  Frontend   │
│ :4201       │
└──────┬──────┘
       │ HTTP /api/users requests
       ▼
┌──────────────────────┐
│  API Gateway         │
│  :9010               │
│  (Route: /api/users) │
└──────┬───────────────┘
       │ Rewrites to /users
       ▼
┌──────────────────────┐
│  User Service        │
│  :9110               │
│  (@RequestMapping    │
│   "/users")          │
└──────┬───────────────┘
       │
       ▼
┌──────────────────┐
│  PostgreSQL DB   │
└──────────────────┘

   ↓ Both registered with ↓

┌──────────────────┐
│  Eureka Server   │
│  :9020           │
└──────────────────┘
```

## Configuration Details

### Gateway Service (gateway-service)
**File**: `src/main/resources/application.properties`

```properties
# Gateway runs on port 9010
spring.application.name=gateway-service
server.port=9010
eureka.client.service-url.defaultZone=http://localhost:9020/eureka/

# Route /api/users to user-service with load balancing
spring.cloud.gateway.routes[0].id=user-service
spring.cloud.gateway.routes[0].uri=lb://user-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/users/**
spring.cloud.gateway.routes[0].filters[0]=RewritePath=/api/(?<segment>.*),/$\\{segment}

# CORS enabled for all origins
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedOrigins=*
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedMethods=GET,POST,PUT,DELETE,OPTIONS,PATCH
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedHeaders=*
```

### User Service
**File**: `src/main/resources/application.properties`

```properties
spring.application.name=user-service
server.port=9110
eureka.client.service-url.defaultZone=http://localhost:9020/eureka/
```

**File**: `src/main/java/.../UserServiceApplication.java`

```java
@SpringBootApplication
@EnableDiscoveryClient  // ← Registers with Eureka
public class UserServiceApplication { ... }
```

**Controller**: `src/main/java/.../UserController.java`

```java
@RestController
@RequestMapping("/users")  // ← Mapped to /users (not /api/users)
public class UserController { ... }
```

### Angular Frontend
**File**: `src/app/core/user.service.ts`

```typescript
export class UserService {
  // Now points to Gateway (previously pointed to localhost:8080)
  private apiBaseUrl = 'http://localhost:9010/api';
  private userServiceUrl = `${this.apiBaseUrl}/users`;
}
```

## Request Flow Example

**User wants to create a user:**

1. **Angular calls**: `POST http://localhost:9010/api/users`
   ```
   {
     "email": "user@example.com",
     "fullName": "John Doe",
     "passwordHash": "...",
     "userType": "INTERNAL"
   }
   ```

2. **Gateway receives** at `:9010`
   - Matches route: `Path=/api/users/**` ✓
   - Discovers user-service via Eureka
   - Rewrites path: `/api/users` → `/users`
   - Forwards to `http://user-service:9110/users`

3. **User Service receives** at `:9110`
   - UserController handles: `POST /users`
   - Creates user in PostgreSQL database
   - Returns 200 OK with created user

4. **Gateway returns** response to Angular
   - Angular receives user object
   - Shows success snackbar

## Startup Order (Important!)

1. **Start Eureka Discovery Server** (first)
   ```bash
   cd discovery-service
   mvn spring-boot:run
   ```
   → Runs on `:9020`

2. **Start User Service** (second)
   ```bash
   cd user-service
   mvn spring-boot:run
   ```
   → Runs on `:9110`
   → Registers with Eureka
   → Connects to PostgreSQL

3. **Start API Gateway** (third)
   ```bash
   cd gateway-service
   mvn spring-boot:run
   ```
   → Runs on `:9010`
   → Discovers user-service via Eureka
   → Ready to route requests

4. **Start Angular Frontend** (fourth)
   ```bash
   npm start
   ```
   → Runs on `:4201`
   → Makes requests to Gateway `:9010/api`

## All Gateway Routes

| Route | Service | Purpose |
|-------|---------|---------|
| `/api/users/**` | user-service | User CRUD operations |
| `/api/shipments/**` | shipments-service | (Future) |
| `/api/vehicles/**` | vehicles-service | (Future) |
| `/api/customers/**` | customers-service | (Future) |

## Adding New Services to Gateway

To route another service through the gateway:

**In gateway-service `application.properties`:**
```properties
spring.cloud.gateway.routes[1].id=shipments-service
spring.cloud.gateway.routes[1].uri=lb://shipments-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/shipments/**
spring.cloud.gateway.routes[1].filters[0]=RewritePath=/api/(?<segment>.*),/$\\{segment}
```

## Benefits of this Setup

✅ **Single Entry Point**: Frontend talks to one address (Gateway:9010)
✅ **Service Discovery**: Automatic load balancing via Eureka
✅ **Microservices**: Each service independent (User, Shipments, Vehicles, etc.)
✅ **Scalability**: Can run multiple instances of each service
✅ **CORS Handling**: Gateway manages CORS for all services
✅ **Path Rewriting**: Hides internal service paths from clients

## Testing

### 1. Check Eureka Dashboard
- Visit: http://localhost:9020
- Should see:
  - `gateway-service` (UP)
  - `user-service` (UP)
  - `config-service` (UP)
  - `discovery-service` (UP)

### 2. Test Gateway Route
```bash
curl -X GET http://localhost:9010/api/users
```

Should return list of users (may be empty if no users created yet)

### 3. Test Create User
```bash
curl -X POST http://localhost:9010/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "fullName": "Test User",
    "passwordHash": "hashed_password",
    "userType": "INTERNAL"
  }'
```

### 4. Angular Frontend
- Open http://localhost:4201
- Navigate to Admin → User Management
- Try creating/searching/deleting users

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Gateway can't find user-service | Check Eureka dashboard - user-service must be UP |
| CORS errors in browser | Gateway CORS config might be missing |
| Connection refused on 9110 | User service not running |
| Connection refused on 9020 | Eureka server not running |
| 404 errors from gateway | Check route configuration matches service controller paths |

## Environment Variables (Optional for Production)

```bash
# Set these for production instead of hardcoding in properties
export EUREKA_URL=http://eureka-server:9020/eureka
export GATEWAY_PORT=9010
export USER_SERVICE_PORT=9110
export POSTGRES_URL=jdbc:postgresql://db-server:5432/postgres
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=secret
```

## Next Steps

1. ✅ Start all services in correct order
2. ✅ Verify on Eureka dashboard
3. ✅ Test user CRUD via Angular UI
4. ✅ Create other microservices (Shipments, Vehicles, Customers)
5. ✅ Add their routes to gateway
6. ✅ Deploy to production with Docker/Kubernetes
