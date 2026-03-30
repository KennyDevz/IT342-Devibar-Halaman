# 🌿 Halaman — Plant Management System

**IT342 — System Integration and Architecture**
**Student:** Devibar, John Kenneth
**Repository:** IT342-Devibar-Halaman

---

## Project Overview

Halaman is a specialized management system for rare plant collectors that coordinates care schedules and growth milestones. The system includes a Spring Boot backend API, a React web application, and an Android mobile app.

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Spring Boot 3.5.0, Java 17 |
| **Database** | PostgreSQL (Supabase) |
| **Security** | Spring Security, JWT, BCrypt |
| **Web Frontend** | React 18, Vite |
| **Mobile** | Android (Kotlin), XML Layouts |
| **HTTP Client** | Axios (Web), Retrofit (Mobile) |
| **Architecture** | REST API, MVVM (Mobile) |

---

## Project Structure
```
IT342-Devibar-Halaman/
├── backend/
│   └── halaman/          ← Spring Boot backend
├── web/                  ← React web frontend
└── mobile/               ← Android mobile app
```

---

## Phase 1 — Backend & Web (Spring Boot + React)

### Backend Setup
- **Group ID:** `edu.cit.devibar`
- **Artifact ID:** `halaman`
- **Base Package:** `edu.cit.devibar.halaman`
- **Framework:** Spring Boot 3.5.0
- **Database:** PostgreSQL hosted on Supabase

### Backend Features
- ✅ User Registration with BCrypt password hashing (salt rounds 12)
- ✅ User Login with JWT authentication
- ✅ Google OAuth 2.0 integration
- ✅ JWT Access Token (24 hours) and Refresh Token (7 days)
- ✅ Role-Based Access Control (USER, ADMIN)
- ✅ Duplicate email prevention
- ✅ Global CORS configuration
- ✅ Stateless session management

### Backend Package Structure
```
edu.cit.devibar.halaman/
├── config/
│   ├── ApplicationConfig.java
│   └── SecurityConfig.java
├── controller/
│   └── AuthController.java
├── dto/
│   ├── AuthResponse.java
│   ├── GoogleAuthRequest.java
│   ├── LoginRequest.java
│   └── RegisterRequest.java
├── entity/
│   └── User.java
├── repository/
│   └── UserRepository.java
├── security/
│   ├── JwtAuthFilter.java
│   └── JwtService.java
└── service/
    └── AuthService.java
```

### API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | None | Register new user |
| POST | `/api/auth/login` | None | Login with email/password |
| GET | `/api/auth/me` | Bearer JWT | Get current user |
| POST | `/api/auth/oauth/google` | None | Login with Google |

### Database Table — `users`

| Column | Type | Description |
|---|---|---|
| `user_id` | UUID | Primary key |
| `email` | VARCHAR(255) | Unique email |
| `password_hash` | VARCHAR(255) | BCrypt hashed password |
| `google_id` | VARCHAR(255) | Google OAuth ID |
| `first_name` | VARCHAR(100) | First name |
| `last_name` | VARCHAR(100) | Last name |
| `role` | VARCHAR(50) | USER or ADMIN |
| `created_at` | TIMESTAMP | Account creation time |

### Web Frontend Setup
```bash
cd web
npm install
npm run dev
```

### Web Frontend Features
- ✅ Login page matching SDD wireframes
- ✅ Register page matching SDD wireframes
- ✅ Google Sign In integration
- ✅ JWT token storage in localStorage
- ✅ Protected routes
- ✅ Toast notifications for success and error
- ✅ Simple Dashboard with sidebar layout
- ✅ Form validation

### Web Frontend Structure
```
web/src/
├── api/
│   ├── api.js
│   └── authApi.js
├── assets/
│   └── authbg.jpg
├── components/
│   └── Toast.jsx
├── context/
│   └── AuthContext.jsx
├── pages/
│   ├── DashboardPage.jsx
│   ├── LoginPage.jsx
│   └── RegisterPage.jsx
├── styles/
│   ├── auth.css
│   ├── dashboard.css
│   └── Toast.css
├── App.jsx
├── index.css
└── main.jsx
```

---

## Phase 2 — Mobile Development (Android)

### Mobile Setup
1. Open `mobile/` folder in Android Studio
2. Let Gradle sync finish
3. Update IP address in `RetrofitClient.kt` to your backend IP
4. Run on physical device or emulator (API 34+)

### Mobile Features
- ✅ Login screen with email/password validation
- ✅ Register screen with all required fields
- ✅ Password show/hide toggle
- ✅ Connected to Phase 1 Spring Boot backend
- ✅ MVVM architecture
- ✅ Retrofit for API calls
- ✅ Custom Toast notifications
- ✅ Form validation with inline errors
- ✅ Loading state inside buttons
- ✅ Dashboard placeholder after login

### Mobile Package Structure
```
edu.cit.devibar.halaman/
├── api/
│   ├── ApiService.kt
│   └── RetrofitClient.kt
├── model/
│   ├── AuthData.kt
│   ├── AuthResponse.kt
│   ├── ErrorResponse.kt
│   ├── LoginRequest.kt
│   ├── RegisterRequest.kt
│   └── UserDto.kt
├── repository/
│   └── AuthRepository.kt
├── utils/
│   └── ToastHelper.kt
├── viewmodel/
│   └── AuthViewModel.kt
└── ui/
    └── auth/
        ├── LoginActivity.kt
        ├── RegisterActivity.kt
        └── DashboardActivity.kt
```

### Mobile Architecture (MVVM)
```
UI (Activities)
    ↕
ViewModel (AuthViewModel)
    ↕
Repository (AuthRepository)
    ↕
API (Retrofit → Spring Boot)
```

---

## Environment Setup

### Backend `application.properties`
```properties
spring.datasource.url=YOUR_SUPABASE_JDBC_URL
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
app.jwt.secret=YOUR_JWT_SECRET
app.google.client-id=YOUR_GOOGLE_CLIENT_ID
app.cors.allowed-origins=http://localhost:5173
```

### Web `.env`
```env
VITE_GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID
```

### Mobile `RetrofitClient.kt`
```kotlin
private const val BASE_URL = "http://YOUR_IP_ADDRESS:8080/"
```

---

## Running the Project

### Backend
```bash
cd backend/halaman
mvn spring-boot:run
```

### Web
```bash
cd web
npm install
npm run dev
```

### Mobile
- Open `mobile/` in Android Studio
- Click ▶ Run

---

## Phase 1 — IT342 Phase 1 – User Registration and Login Completed
## Phase 2 — IT342 Phase 2 – Mobile Development Completed
