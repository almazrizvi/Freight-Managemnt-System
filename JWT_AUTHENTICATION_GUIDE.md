# JWT Authentication Implementation Guide

## Architecture Overview

```
┌────────────────────────────────────────────────┐
│           Angular Frontend (4201)               │
│     ┌──────────────────────────────────┐       │
│     │  Local Storage                   │       │
│     │  - JWT Token                     │       │
│     │  - Expires at                    │       │
│     └──────────────────────────────────┘       │
└────────────────────────────────────────────────┘
              ↓ (HTTP Request)
        📩 Add JWT to Header
        Authorization: Bearer {token}
              ↓
┌────────────────────────────────────────────────┐
│         API Gateway (9010)                      │
│    ┌──────────────────────────────────┐        │
│    │ JwtAuthFilter                    │        │
│    │ - Check if JWT enabled           │        │
│    │ - Validate Bearer format         │        │
│    │ - Verify JWT signature           │        │
│    │ - Add claims to headers          │        │
│    └──────────────────────────────────┘        │
│              ↓                                  │
│    Public endpoints (skip validation)          │
│    ✓ /api/users/login                         │
│    ✓ /api/users/register                      │
│    ✓ /actuator/health                         │
└────────────────────────────────────────────────┘
              ↓
┌────────────────────────────────────────────────┐
│        User Service (9110)                      │
│  ┌─────────────────────────────────────┐       │
│  │ /users/login endpoint               │       │
│  │ - Find user by email                │       │
│  │ - Verify password (bcrypt)          │       │
│  │ - Generate JWT token                │       │
│  │ - Return token + user info          │       │
│  └─────────────────────────────────────┘       │
│  ┌─────────────────────────────────────┐       │
│  │ /users/register endpoint            │       │
│  │ - Validate email uniqueness         │       │
│  │ - Hash password (bcrypt)            │       │
│  │ - Save user                         │       │
│  │ - Generate JWT token                │       │
│  │ - Return token + user info          │       │
│  └─────────────────────────────────────┘       │
│  ┌─────────────────────────────────────┐       │
│  │ /users/validate-token endpoint      │       │
│  │ - Verify JWT signature              │       │
│  │ - Check expiration                  │       │
│  │ - Return valid: true/false          │       │
│  └─────────────────────────────────────┘       │
└────────────────────────────────────────────────┘
```

---

## Current Status: JWT DISABLED

**Default:** `jwt.enabled=false` in all configuration files

This allows the system to work without authentication while you test other features. When ready to enable JWT:

### Enable JWT in Gateway

Edit `config-service/src/main/resources/configs/gateway-service/application-dev.properties`:
```properties
jwt.enabled=true
jwt.secret=your-strong-secret-key-here
jwt.expiration=3600000
```

### Gateway JWT Flow

1. **Request arrives at gateway** with `Authorization: Bearer {token}`
2. **JwtAuthFilter checks:**
   - Is JWT enabled? (`jwt.enabled`)
   - Is this a public endpoint? (login, register, health)
   - Does it have Bearer token?
   - Is token format valid (header.payload.signature)?
   - Is JWT signature valid?
   - Is token not expired?
3. **If valid:** Add user claims to request headers:
   - `X-User-Id: {userId}`
   - `X-User-Email: {email}`
   - `X-JWT-Token: {token}`
   - Route request to downstream service
4. **If invalid:** Return 401 Unauthorized

---

## User Service: Authentication Endpoints

### 1. Login Endpoint

**HTTP POST** `/users/login`
- Path: `http://localhost:9010/api/users/login` (via Gateway)
- Direct: `http://localhost:9110/users/login`

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (Success - 200):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "fullName": "John Doe",
  "userType": "CUSTOMER",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
```

**Response (Failure - 401):**
```json
"Login failed: User not found with email: invalid@example.com"
```

### 2. Register Endpoint

**HTTP POST** `/users/register`
- Path: `http://localhost:9010/api/users/register` (via Gateway)
- Direct: `http://localhost:9110/users/register`

**Request:**
```json
{
  "email": "newuser@example.com",
  "password": "securepassword123"
}
```

**Response (Success - 201):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ...",
  "userId": "550e8400-e29b-41d4-a716-446655440001",
  "email": "newuser@example.com",
  "fullName": "newuser@example.com",
  "userType": "CUSTOMER",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
```

### 3. Token Validation Endpoint

**HTTP GET** `/users/validate-token`
- Header: `Authorization: Bearer {token}`
- Response: `{"valid": true}` or `{"valid": false}`

---

## JWT Token Structure

### JWT Format
```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20iLCJpYXQiOjE3MDk3NDUzNjEsImV4cCI6MTcwOTc0ODk2MX0.abc123xyz...
```

### Decoded JWT

**Header:**
```json
{
  "alg": "HS512",
  "typ": "JWT"
}
```

**Payload (Claims):**
```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "iat": 1709745361,
  "exp": 1709748961
}
```

**Signature:**
```
HMACSHA512(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret-key
)
```

---

## Frontend Integration

### 1. Store Token After Login

```typescript
// In auth.service.ts or similar
login(email: string, password: string) {
  return this.http.post('/api/users/login', {
    email: email,
    password: password
  }).pipe(
    tap(response => {
      // Store token
      localStorage.setItem('authToken', response.token);
      localStorage.setItem('expiresIn', response.expiresIn);
      localStorage.setItem('userId', response.userId);
    })
  );
}
```

### 2. Add Token to Requests (HTTP Interceptor)

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // Get token from localStorage
    const token = localStorage.getItem('authToken');

    // If token exists and request is not to public endpoint
    if (token && !this.isPublicEndpoint(req.url)) {
      // Clone request with Authorization header
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(req);
  }

  private isPublicEndpoint(url: string): boolean {
    return url.includes('/login') || url.includes('/register');
  }
}
```

### 3. Add Interceptor to App

```typescript
// In app.config.ts or main module
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { JwtInterceptor } from './core/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ]
};
```

### 4. Handle Token Expiration

```typescript
// Check if token is expired
isTokenExpired(): boolean {
  const expiresIn = localStorage.getItem('expiresIn');
  if (!expiresIn) return true;
  
  return parseInt(expiresIn) < Date.now() / 1000;
}

// Logout when expired
logout(): void {
  localStorage.removeItem('authToken');
  localStorage.removeItem('userId');
  localStorage.removeItem('expiresIn');
  this.router.navigate(['/login']);
}
```

---

## Configuration Properties

### Gateway (`jwt.enabled=false` by default)

| Property | Default | Description |
|----------|---------|-------------|
| `jwt.enabled` | `false` | Enable/disable JWT validation at gateway |
| `jwt.secret` | `freight-system-secret-...` | Secret key for signing/verifying tokens |
| `jwt.expiration` | `3600000` | Token lifetime in milliseconds (1 hour) |

### Configuration File Locations

```
config-service/src/main/resources/configs/
├── gateway-service/
│   ├── application.properties (default - jwt.enabled=false)
│   ├── application-dev.properties (dev - jwt.enabled=false)
│   └── application-uat.properties (uat - jwt.enabled=true recommended)
└── user-service/
    ├── application.properties (default)
    ├── application-dev.properties (dev)
    └── application-uat.properties (uat)
```

---

## Enable JWT for Testing

### Step 1: Update Gateway Dev Config

Edit: `config-service/src/main/resources/configs/gateway-service/application-dev.properties`

```properties
# JWT Configuration (ENABLED for testing)
jwt.enabled=true
jwt.secret=freight-system-secret-key-dev-environment
jwt.expiration=3600000
```

### Step 2: Restart Gateway

```bash
cd gateway-service
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Step 3: Test Login Flow

```bash
# 1. Register new user
curl -X POST http://localhost:9010/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Response:
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "test@example.com",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}

# 2. Use token to access protected endpoint
curl -X GET http://localhost:9010/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

# Should return list of users

# 3. Without token - should get 401
curl -X GET http://localhost:9010/api/users

# Response: 401 Unauthorized - Missing authorization header
```

---

## Security Best Practices

### ✅ DO:
- [ ] Use HTTPS in production (not HTTP)
- [ ] Store token in secure httpOnly cookie or localStorage
- [ ] Include token expiration (reasonable timeout)
- [ ] Rotate secret key periodically
- [ ] Use strong secret key (min 32 chars)
- [ ] Validate token on every request
- [ ] Clear token on logout
- [ ] Refresh token before expiration
- [ ] Hash passwords before storage (✓ using bcrypt)

### ❌ DON'T:
- [ ] Store token in plain URL query parameters
- [ ] Hardcode secret in code
- [ ] Use weak secret keys
- [ ] Send tokens over HTTP (use HTTPS)
- [ ] Store sensitive data in JWT payload (it's encoded, not encrypted)
- [ ] Trust token without server validation
- [ ] Remove token expiration
- [ ] Reuse secret across environments

---

## Troubleshooting

### Issue: "Invalid token format"
**Cause:** Token doesn't start with "Bearer "
**Solution:** Ensure header is `Authorization: Bearer {token}`

### Issue: "Invalid JWT token"
**Cause:** Secret key mismatch
**Solution:** Verify `jwt.secret` is same on gateway and user-service

### Issue: "Expired JWT token"
**Cause:** Token lifetime exceeded
**Solution:** Login again to get new token, or increase `jwt.expiration`

### Issue: Gateway returning 401 for all requests
**Cause:** JWT enabled but tokens not being sent
**Solution:** 
- Add token to localStorage after login
- Ensure HTTP interceptor is registered
- Verify `jwt.enabled=false` during testing if not ready

### Issue: User can't create new users via API
**Cause:** JWT validation blocking /users endpoint
**Solution:** Add `/users/register` to `PUBLIC_ENDPOINTS` in JwtAuthFilter.java (already done ✓)

---

## Architecture Decision: Why Gateway + User Service Split?

### User Service Responsible For:
✓ Generating tokens (login/register)
✓ Hashing passwords with bcrypt
✓ Creating audit trail (created_at, updated_at)
✓ Validating user credentials
✓ Business logic for user management

### Gateway Responsible For:
✓ Validating JWT at entry point
✓ Blocking unauthorized requests early
✓ Adding user context to downstream requests
✓ Protecting all microservices uniformly
✓ Handling token validation errors

### Why This Design?
- **Centralized Security:** Gateway validates once, all services protected
- **Scalable:** Each new service doesn't duplicate JWT logic
- **Microservices Pattern:** User Service only knows about users, not gateway concerns
- **Decoupled:** Services can run independently if needed
- **Audit Trail:** Each service can log `X-User-Id` header for request tracking

---

## Next Steps

1. ✅ JWT infrastructure implemented
2. ✅ Login/Register endpoints ready (public)
3. ✅ Token generation working
4. ⏳ Enable `jwt.enabled=true` when ready to test
5. ⏳ Implement Angular token management
6. ⏳ Add JWT interceptor to frontend
7. ⏳ Add role-based access control (RBAC) on top of JWT
8. ⏳ Implement refresh token mechanism

---

## Testing Checklist

- [ ] User can register with email/password
- [ ] User receives valid JWT token after registration
- [ ] Token has correct structure (header.payload.signature)
- [ ] User can login with existing credentials
- [ ] Login returns same token structure
- [ ] Token contains userId and email claims
- [ ] Can use token to access /users endpoint
- [ ] Without token, /users returns 401
- [ ] Public endpoints work without token (/login, /register)
- [ ] Token expires after configured time
- [ ] Angular interceptor adds token to requests
- [ ] Angular stores token in localStorage
- [ ] Can logout by clearing localStorage

