# JWT Authentication Architecture Decision

## Question
> Where should auth logic be handled: Gateway or User Service? Keeping in mind we'll expand to multiple microservices.

## Answer: BOTH (Specialized Responsibilities)

---

## Architecture Pattern

```
                    ┌─────────────────────┐
                    │  User Browser       │
                    │  localStorage       │
                    └──────────┬──────────┘
                               │ Has JWT Token?
                               ↓
                    ┌─────────────────────────────────┐
                    │      API Gateway (9010)         │
                    │                                 │
                    │  JwtAuthFilter                  │
                    │  Validates JWT signature        │
                    │  Checks expiration              │
                    │  Blocks unauthorized requests   │
                    │  Adds user context headers      │
                    └──────────┬──────────────────────┘
                               │ Authorized?
                               ↓
        ┌──────────────────────────────────────┬─────────────────┐
        ↓                                       ↓                 ↓
   ┌────────────────┐  ┌────────────────┐  ┌──────────────────────┐
   │ User Service   │  │ Shipments Svc  │  │ Vehicles Service     │
   │ (9110)         │  │ (9120)         │  │ (9130)               │
   │                │  │                │  │                      │
   │ /login         │  │ All routes     │  │ All routes use       │
   │ /register      │  │ use            │  │ X-User-Id from       │
   │ Generates JWT  │  │ X-User-Id      │  │ gateway headers      │
   │                │  │ from headers   │  │                      │
   └────────────────┘  └────────────────┘  └──────────────────────┘
```

---

## Where Each Responsibility Lives

### 1. **User Service** - Generates & Owns Tokens
**Location:** User Service (Port 9110)

**Responsibility:**
- `POST /users/login` - Authenticate user, generate JWT
- `POST /users/register` - Create user, generate JWT  
- `GET /users/validate-token` - Verify token validity
- Hash passwords with bcrypt
- Maintain user credentials in database
- Create audit trail (createdAt, updatedAt)

**Why User Service?**
- User Service owns the User entity
- Only User Service knows passwords
- Only User Service knows email/credentials
- Makes logical sense: "User Service generates user tokens"
- Decoupled from infrastructure concerns

**Files:**
- `AuthService.java` - Login/register/validate logic
- `AuthController.java` - HTTP endpoints for /login, /register
- `JwtTokenProvider.java` - Token generation/validation
- `SecurityConfig.java` - BCrypt password encoder bean

---

### 2. **API Gateway** - Validates & Protects
**Location:** Gateway (Port 9010)

**Responsibility:**
- `JwtAuthFilter` - Intercept all requests
- Verify JWT signature using secret key
- Check token expiration
- Allow/block requests based on token validity
- Add user context headers for downstream services
- Handle public vs protected endpoints

**Why Gateway?**
- Single point of entry - centralized security
- All services protected uniformly
- No need to implement JWT logic in every service
- Microservices pattern: don't repeat cross-cutting concerns
- Can enable/disable JWT globally with one config
- Protects services even if they have bugs

**Files:**
- `JwtAuthFilter.java` - Request validation and filtering
- `JwtTokenProvider.java` - Token verification only

**Configuration:**
- `jwt.enabled=false` (default) - Disabled for testing
- Can be toggled per environment: dev, uat, prod

---

### 3. **Config Server** - Manages Settings
**Location:** Config Server (Port 9030)

**Files per Profile:**
```
configs/
├── user-service/
│   ├── application.properties (jwt.secret, jwt.expiration)
│   ├── application-dev.properties
│   └── application-uat.properties
└── gateway-service/
    ├── application.properties (jwt.enabled, jwt.secret)
    ├── application-dev.properties
    └── application-uat.properties
```

**Key Settings:**
- `jwt.enabled` - Enable/disable validation at gateway
- `jwt.secret` - Same secret on both services (used for signing/verification)
- `jwt.expiration` - Token lifetime in milliseconds

---

## Data Flow: Login Request

```
1. User enters email/password in Angular UI
   ↓
2. POST http://localhost:4201/login
   ↓
3. Angular forwards to: POST http://localhost:9010/api/users/login
   ↓
4. Gateway receives request
   - Is /api/users/login a public endpoint? YES
   - Skip JWT validation
   - Forward to User Service
   ↓
5. User Service receives: POST /users/login
   - Find user by email
   - Verify password hash
   - Generate JWT token:
     * Payload: { sub: userId, email: userEmail }
     * Sign with secret: HS512
     * Result: eyJhbGciOiJIUzUxMiJ9...
   - Return: { token, userId, email, expiresIn }
   ↓
6. Angular receives token
   - Store in localStorage
   - Add to HTTP interceptor
   ↓
7. Next API request includes: Authorization: Bearer {token}
   ↓
8. Gateway receives request with token
   - Is jwt.enabled = true? YES  
   - Is public endpoint? NO
   - Extract token from "Bearer {token}"
   - Verify signature with secret key
   - Verify expiration date
   - Extract claims: userId, email
   - Add headers: X-User-Id, X-User-Email, X-JWT-Token
   - Forward to target service
   ↓
9. Target service receives request
   - Can read X-User-Id from headers
   - Knows which user made request
   - Can enforce authorization rules
   ↓
10. Service returns data, Angular displays to user
```

---

## Expanding to Multiple Services

### Current Services
- ✅ User Service (9110) - Generates tokens
- ⏳ Shipments Service (9120)
- ⏳ Vehicles Service (9130)
- ⏳ Customers Service (9140)
- ⏳ Reports Service (9150)

### Adding New Service: Shipments Service

**Setup:**
1. Create Shipments Service (9120)
2. User Service continues handling login/auth
3. Gateway continues validating all tokens
4. Shipments Service reads user context from headers

**Configuration:**
```properties
# No auth logic needed in new service!
# Gateway handles it automatically
```

**Code in Shipments Controller:**
```java
@GetMapping("/shipments")
public List<Shipment> getShipments(
    @RequestHeader("X-User-Id") String userId) {
    // Service knows which user made request
    // Can filter shipments by user
    return shipmentService.getShipmentsByUser(userId);
}
```

**No duplication needed!** Each service just:
- Reads user context from headers
- Uses it for authorization/filtering
- No JWT logic, no token generation, no password hashing

---

## Security Flow Summary

| Layer | Component | Responsibility |
|-------|-----------|-----------------|
| Browser | Local Storage | Store JWT token |
| Angular | HTTP Interceptor | Add JWT to every request |
| Gateway | JwtAuthFilter | Validate JWT, block unauthorized |
| Service | Controller/Service | Read user context from headers |

---

## Enable JWT When Ready

### Current Status: DISABLED (`jwt.enabled=false`)

### To Enable:

Edit: `config-service/src/main/resources/configs/gateway-service/application-dev.properties`
```properties
jwt.enabled=true
jwt.secret=freight-system-secret-key-dev-environment
jwt.expiration=3600000
```

Then restart gateway:
```bash
cd gateway-service
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Test:
```bash
# Register
curl -X POST http://localhost:9010/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"pass"}'

# Login  
curl -X POST http://localhost:9010/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"pass"}'

# Use token to access protected endpoint
curl -X GET http://localhost:9010/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## Why This Architecture is Best for Microservices

✅ **Centralized Security**
- One point to enforce JWT rules
- Easy to update token requirements globally
- No need to patch every microservice

✅ **Separation of Concerns**
- User Service: Authentication
- Gateway: Authorization (token validation)
- Other Services: Business logic

✅ **Scalable**
- Add 10 new services - no auth code duplication
- Each service just reads `X-User-Id` header
- Gateway protects all equally

✅ **Flexible**
- Can enable/disable JWT per environment
- Can change token lifetime without touching services
- Can rotate secrets without deploying services

✅ **Production-Ready**
- Follows industry best practices
- Similar to enterprise systems (Netflix, Uber, etc.)
- Easy to add OAuth2, SAML, etc. later

---

## Files Created

```
gateway-service/
├── src/main/java/.../config/
│   ├── JwtFilter.java (OLD - can delete)
│   └── JwtAuthFilter.java (NEW - actual filter)
└── src/main/java/.../util/
    └── JwtTokenProvider.java (Token validation)

user-service/
├── src/main/java/.../controller/
│   └── AuthController.java (Login/register endpoints)
├── src/main/java/.../service/
│   └── AuthService.java (Auth business logic)
├── src/main/java/.../util/
│   └── JwtTokenProvider.java (Token generation)
├── src/main/java/.../dto/
│   ├── LoginRequest.java
│   └── LoginResponse.java
└── src/main/java/.../config/
    └── SecurityConfig.java (BCrypt bean)

config-service/src/main/resources/configs/
├── gateway-service/application*.properties (jwt.enabled)
└── user-service/application*.properties (jwt.secret, jwt.expiration)
```

---

## Next Phase: Role-Based Access Control (RBAC)

Once JWT is working:
1. Add roles table to database
2. Add user_roles join table
3. Add @RequireRole annotation
4. Gateway passes roles in header: `X-User-Roles: ADMIN,USER`
5. Each service enforces role requirements

---

## Recommended Reading

- See: `JWT_AUTHENTICATION_GUIDE.md` - Complete guide with curl examples
- See: `PROJECT_STATUS.md` - Architecture diagram
- See: `CONFIG_SERVER_GUIDE.md` - How to enable/disable JWT per environment

