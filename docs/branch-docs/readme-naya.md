# User Management - Documentation# User Management - Documentation



**Author:** Valiza Nadya Jatikansha  **Author:** Valiza Nadya Jatikansha  

**Date:** 1 Maret 2026  **Date:** 1 Maret 2026  

**Branch:** `dev/naya`**Branch:** `dev/naya`



------



## 📋 Backlog## 📋 Backlog



| ID | Feature | Status || ID | Feature | Status |

|----|---------|--------||----|---------|--------|

| UM-3 | Create Executive Account (by Admin) | ✅ Done || UM-3 | Create Executive Account (by Admin) | ✅ Done |

| UM-9 | Update Password | ✅ Done || UM-9 | Update Password | ✅ Done |



------



## 🎯 Feature 1: Create Executive Account (UM-3)## 🎯 Feature 1: Create Executive Account (UM-3)



### Overview### Overview

Admin dapat membuat akun Executive baru. Sistem akan mengirim email undangan dengan **link setup password** (bukan password langsung). Executive harus set password sendiri sebelum bisa login.Admin dapat membuat akun Executive baru. Sistem akan mengirim email undangan dengan **link setup password** (bukan password langsung). Executive harus set password sendiri sebelum bisa login.



### Flow### Flow

1. Admin create executive → Akun dibuat dengan status **inactive**1. Admin create executive → Akun dibuat dengan status **inactive**

2. Sistem generate **setup token** (24 jam) → Kirim email dengan link2. Sistem generate **setup token** (24 jam) → Kirim email dengan link

3. Executive klik link → Halaman set password3. Executive klik link → Halaman set password

4. Executive set password → Akun jadi **active** → Bisa login4. Executive set password → Akun jadi **active** → Bisa login



### API Endpoint### API Endpoint



#### Create Executive (Admin Only)#### Create Executive (Admin Only)

```http```http

POST /api/registerPOST /api/register

Authorization: JWT Cookie (Role: ADMIN)Authorization: JWT Cookie (Role: ADMIN)

Content-Type: application/jsonContent-Type: application/json



{{

  "email": "executive@example.com",  "email": "executive@example.com",

  "nama": "John Doe",  "nama": "John Doe",

  "phone": "081234567890",  "phone": "081234567890",

  "jabatan": "Direktur Utama"  "jabatan": "Direktur Utama"

}}- **I WANT TO** Membuat akun Executive untuk pejabat tertentu melalui dashboard```

```



**Response: 201 Created**

```json| Field | Type | Required | Validation |- **SO THAT** Executive dapat mengakses dashboard insight untuk mendukung pengambilan kebijakanPOST /api/admin/users/executive

{

  "status": "success",|-------|------|----------|------------|

  "message": "Executive berhasil dibuat. Email undangan telah dikirim.",

  "data": {| `email` | string | ✅ | Format email valid |```

    "id": "uuid",

    "email": "executive@example.com",| `nama` | string | ✅ | 2-100 karakter |

    "nama": "John Doe",

    "phone": "081234567890",| `phone` | string | ✅ | 10-15 karakter |### Endpoint

    "jabatan": "Direktur Utama",

    "roleName": "EXECUTIVE",| `jabatan` | string | ✅ | Max 100 karakter |

    "active": false

  }### Authorization

}

```### Response



**Error Responses:**```- **Role Required:** `ADMIN`

- `400` - Validation error (email format salah, field kosong)

- `401` - Unauthorized (tidak ada cookie)**201 Created**

- `403` - Forbidden (bukan role ADMIN)

- `409` - Conflict (email sudah terdaftar)```jsonPOST /api/admin/users/executive- Menggunakan JWT Cookie authentication



---{



## 🎯 Feature 2: Set Password (First Time)  "status": 201,```



### Overview  "message": "Akun Executive berhasil dibuat.",

Executive yang baru dibuat menggunakan **token dari email** untuk set password pertama kali. Endpoint ini **PUBLIC** (tidak perlu login).

  "timestamp": "2026-03-01T10:30:00.000+07:00",### Request Body

### Cara Ambil Token dari Database

Setelah create executive, ambil token dengan command:  "data": {

```bash

docker exec -it propen-a-backend-sifpi-app-db-1 psql -U sifpi-dev -d sifpi-dev -c "SELECT email, password_setup_token FROM users WHERE email = 'executive@example.com';"    "id": "uuid",### Authorization

```

    "email": "executive@example.com",

Copy token dari kolom `password_setup_token`.

    "name": "John Doe",- **Role Required:** `admin````json

### API Endpoint

    "phone": "081234567890",

#### Set Password (Public)

```http    "jabatan": "Direktur Utama",- Menggunakan JWT Cookie authentication{

POST /api/auth/set-password

Content-Type: application/json    "roleName": "EXECUTIVE",



{    "active": false,  "email": "executive@example.com",

  "token": "1336eb780c84448ca2fd3c68bfe4858e",

  "password": "MySecurePassword123!",    "verified": false

  "confirmPassword": "MySecurePassword123!"

}  }### Request Body  "nama": "John Doe",

```

}

**Response: 200 OK**

```json```  "phone": "081234567890",

{

  "status": "success",

  "message": "Password berhasil diatur. Silakan login."

}**Error Responses**```json  "jabatan": "Direktur Utama"

```

| Status | Message |

**Error Responses:**

- `400` - Password dan confirmPassword tidak sama|--------|---------|{}

- `404` - Token tidak valid atau sudah digunakan

- `401` - Token sudah kedaluwarsa (>24 jam)| 400 | Validation error |



---| 401 | Unauthorized |  "email": "executive@example.com",```



## 🎯 Feature 3: Update Password (UM-9)| 403 | Forbidden (bukan ADMIN) |



### Overview| 409 | Email sudah terdaftar |  "nama": "John Doe",

User yang **sudah login** (Admin/Executive) dapat mengubah password mereka. Harus memasukkan password lama untuk validasi.



### API Endpoint

---  "phone": "081234567890",| Field | Type | Required | Validation |

#### Update Password (Authenticated)

```http

PATCH /api/users/password

Authorization: JWT Cookie (Any logged-in user)## 🎯 Feature 2: Set Password (First Time)  "jabatan": "Direktur Utama"|-------|------|----------|------------|

Content-Type: application/json



{

  "currentPassword": "OldPassword123!",### Deskripsi}| `email` | string | ✅ | Format email valid |

  "newPassword": "NewPassword456!",

  "confirmPassword": "NewPassword456!"Executive yang baru dibuat dapat membuat password pertama kali melalui link dari email.

}

``````| `nama` | string | ✅ | 2-100 karakter |



**Response: 200 OK**### Endpoint

```json

{| `phone` | string | ✅ | 10-15 karakter |

  "status": "success",

  "message": "Password berhasil diubah"```

}

```POST /api/auth/set-password| Field | Type | Required | Validation || `jabatan` | string | ✅ | Max 100 karakter |



**Error Responses:**```

- `400` - Password dan confirmPassword tidak sama

- `401` - Password saat ini tidak valid|-------|------|----------|------------|

- `401` - Password baru tidak boleh sama dengan password saat ini

### Authorization

---

- **Public** - Tidak perlu authentication| `email` | string | ✅ | Format email valid |### Response

## 📧 Email Template



Executive akan menerima email dengan:

- Subject: "Undangan Bergabung ke SIFPI"### Request Body| `nama` | string | ✅ | 2-100 karakter |

- Button "Buat Password" yang mengarah ke: `{APP_LOGIN_URL}/set-password?token={token}`

- Informasi bahwa link valid selama **24 jam**



Email dikirim via **SendGrid API** dari `noreply@mhibrizif.site`.```json| `phone` | string | ✅ | 10-20 karakter |**201 Created**



---{



## 📁 Files Created/Modified  "token": "abc123...",| `jabatan` | string | ✅ | Max 100 karakter |```json



### New Files  "password": "MySecurePassword123!",



| File | Location | Description |  "confirmPassword": "MySecurePassword123!"{

|------|----------|-------------|

| `PasswordController.java` | `usermanagement/controller/` | REST endpoints untuk set & update password |}

| `SetPasswordRequest.java` | `usermanagement/dto/` | DTO untuk set password (token, password, confirmPassword) |

| `UpdatePasswordRequest.java` | `usermanagement/dto/` | DTO untuk update password (UM-9) |```### Response  "status": 201,

| `PasswordService.java` | `usermanagement/service/` | Business logic untuk password management |

| `AdminUserController.java` | `usermanagement/controller/` | `POST /api/register` dengan `@PreAuthorize("hasRole('ADMIN')")` |

| `CreateExecutiveRequest.java` | `usermanagement/dto/` | Request DTO dengan validasi |

| `ExecutiveDTO.java` | `usermanagement/dto/` | Response DTO || Field | Type | Required | Validation |  "message": "Akun Executive berhasil dibuat.",

| `ExecutiveMapper.java` | `usermanagement/mapper/` | MapStruct mapper |

| `ExecutiveService.java` | `usermanagement/service/` | Business logic: generate token, send email ||-------|------|----------|------------|

| `EmailService.java` | `common/services/` | SendGrid API integration |

| `token` | string | ✅ | Token dari email |**201 Created**  "timestamp": "2026-02-28T10:30:00.000+07:00",

### Modified Files

| `password` | string | ✅ | Min 8 karakter |

| File | Changes |

|------|---------|| `confirmPassword` | string | ✅ | Harus sama dengan password |```json  "data": {

| `User.java` | Added: `passwordSetupToken`, `passwordSetupTokenExpiry` |

| `UserRepository.java` | Added: `findByEmail()`, `findByPasswordSetupToken()` |

| `AuthPath.java` | Added: `SET_PASSWORD` to `PUBLIC` paths |

| `GlobalExceptionHandler.java` | Handler `SecurityException` return 401 |### Response{    "id": "550e8400-e29b-41d4-a716-446655440000",

| `application.yaml` | Added SendGrid email config |

| `.env` | Added: `SENDGRID_API_KEY`, `EMAIL_FROM_ADDRESS`, `APP_LOGIN_URL` |



---**200 OK**  "status": 201,    "email": "executive@example.com",



## ⚙️ Environment Variables```json



```bash{  "message": "Akun Executive berhasil dibuat.",    "name": "John Doe",

# SendGrid Email Configuration

SENDGRID_API_KEY=SG.xxx  "status": 200,

EMAIL_FROM_ADDRESS=noreply@mhibrizif.site

EMAIL_FROM_NAME=SIFPI  "message": "Password berhasil dibuat. Silakan login.",  "timestamp": "2026-03-01T10:30:00.000+07:00",    "phone": "081234567890",

APP_LOGIN_URL=http://localhost:3000/login

```  "timestamp": "2026-03-01T10:35:00.000+07:00",



---  "data": null  "data": {    "jabatan": "Direktur Utama",



## 🧪 Testing Guide}



### Test 1: Create Executive & Set Password```    "id": "550e8400-e29b-41d4-a716-446655440000",    "role": "EXECUTIVE",



**1. Login sebagai Admin**

```http

POST http://localhost:8080/api/auth/login**Error Responses**    "email": "executive@example.com",    "isVerified": true,

{

  "email": "admin@sifpi.go.id",| Status | Message |

  "password": "admin123"

}|--------|---------|    "name": "John Doe",    "emailVerified": true,

```

| 400 | Validation error |

**2. Create Executive**

```http| 401 | Token tidak valid / sudah kedaluwarsa |    "phone": "081234567890",    "isActive": true,

POST http://localhost:8080/api/register

Cookie: SIFPI_TOKEN (otomatis dari login)| 404 | Token tidak ditemukan |

{

  "email": "test.executive@example.com",    "jabatan": "Direktur Utama",    "createdAt": "2026-02-28T10:30:00",

  "nama": "Test Executive",

  "phone": "081234567890",---

  "jabatan": "Direktur Keuangan"

}    "roleName": "executive",    "updatedAt": "2026-02-28T10:30:00"

```

## 🎯 Feature 3: Update Password (UM-9)

**3. Ambil Token dari Database**

```bash    "isVerified": true,  }

docker exec -it propen-a-backend-sifpi-app-db-1 psql -U sifpi-dev -d sifpi-dev -c "SELECT email, password_setup_token FROM users WHERE email = 'test.executive@example.com';"

```### Deskripsi

Copy token yang muncul.

User yang sudah login dapat mengubah password untuk menjaga keamanan akun.    "emailVerified": true,}

**4. Set Password (Public, Tidak Perlu Login)**

```http

POST http://localhost:8080/api/auth/set-password

{### Endpoint    "isActive": true,```

  "token": "PASTE_TOKEN_DI_SINI",

  "password": "executive123",

  "confirmPassword": "executive123"

}```    "createdAt": "2026-03-01T10:30:00",

```

PATCH /api/users/password

**5. Login sebagai Executive**

```http```    "updatedAt": "2026-03-01T10:30:00"**409 Conflict** (Email sudah terdaftar)

POST http://localhost:8080/api/auth/login

{

  "email": "test.executive@example.com",

  "password": "executive123"### Authorization  }```json

}

```- **Authenticated** - Perlu login (JWT Cookie)



---}{



### Test 2: Update Password### Request Body



**1. Login dulu (Admin atau Executive)**```  "status": 409,



**2. Update Password**```json

```http

PATCH http://localhost:8080/api/users/password{  "message": "User dengan email 'executive@example.com' sudah terdaftar.",

Cookie: SIFPI_TOKEN (otomatis dari login)

{  "currentPassword": "OldPassword123!",

  "currentPassword": "admin123",

  "newPassword": "newadmin456",  "newPassword": "NewSecurePassword456!"**409 Conflict** (Email sudah terdaftar)  "timestamp": "2026-02-28T10:30:00.000+07:00",

  "confirmPassword": "newadmin456"

}}

```

``````json  "data": null

**3. Logout & Login dengan Password Baru**



---

| Field | Type | Required | Validation |{}

### Error Test Cases

|-------|------|----------|------------|

| Test Case | Expected Result |

|-----------|-----------------|| `currentPassword` | string | ✅ | Password saat ini |  "status": 409,```

| Set password dengan token invalid | 404 Not Found |

| Set password dengan token expired | 401 Unauthorized || `newPassword` | string | ✅ | Min 8 karakter, tidak boleh sama dengan current |

| Update password dengan currentPassword salah | 401 Unauthorized |

| Update password dengan confirmPassword tidak sama | 401 Unauthorized |  "message": "User dengan email 'executive@example.com' sudah terdaftar.",

| Update password dengan newPassword = currentPassword | 401 Unauthorized |

| Create executive tanpa login | 401 Unauthorized |### Response

| Create executive dengan role bukan ADMIN | 403 Forbidden |

| Create executive dengan email sudah ada | 409 Conflict |  "timestamp": "2026-03-01T10:30:00.000+07:00",**400 Bad Request** (Validasi gagal)



---**200 OK**



## 🔄 Flow Diagram```json  "data": null```json



```{

┌─────────────────┐

│  POST /api/     │  "status": 200,}{

│  register       │

│  (ADMIN Only)   │  "message": "Password berhasil diubah.",

└────────┬────────┘

         │  "timestamp": "2026-03-01T10:40:00.000+07:00",```  "status": 400,

         ▼

┌─────────────────────────────────┐  "data": null

│  ExecutiveService               │

│  ├── Check email exists? → 409  │}  "message": "Permintaan tidak valid: email: Format email tidak valid",

│  ├── Get role 'EXECUTIVE'       │

│  ├── Generate setup token (UUID)│```

│  ├── Set token expiry (+24h)    │

│  ├── Set user inactive          │**400 Bad Request** (Validasi gagal)  "timestamp": "2026-02-28T10:30:00.000+07:00",

│  ├── Save to DB                 │

│  └── Send email async           │**Error Responses**

└────────┬────────────────────────┘

         │| Status | Message |```json  "data": null

         ▼

┌─────────────────┐|--------|---------|

│  Email sent     │

│  with token     │| 400 | Validation error |{}

└────────┬────────┘

         │| 401 | Password saat ini tidak valid |

         ▼

┌─────────────────────────────────┐| 401 | Password baru tidak boleh sama dengan password saat ini |  "status": 400,```

│  POST /api/auth/set-password    │

│  (PUBLIC)                       │

│  ├── Validate token             │

│  ├── Check expiry               │---  "message": "Permintaan tidak valid: email: Format email tidak valid",

│  ├── Hash password              │

│  ├── Clear token                │

│  ├── Activate user              │

│  └── Return success             │## 📁 Files Created/Modified  "timestamp": "2026-03-01T10:30:00.000+07:00",**403 Forbidden** (Bukan Admin)

└────────┬────────────────────────┘

         │

         ▼

┌─────────────────┐### New Files  "data": null```json

│  User can login │

└─────────────────┘```

```

src/main/java/id/go/kemenkoinfra/ipfo/sifpi/}{

---

├── usermanagement/

## 📝 Notes

│   ├── controller/```  "status": 403,

- **Token validity:** 24 hours

- **Password minimum length:** 8 characters│   │   ├── AdminUserController.java      # POST /api/register

- **Email service:** SendGrid REST API

- **Authentication:** JWT Cookie (`SIFPI_TOKEN`)│   │   └── PasswordController.java       # POST /api/auth/set-password, PATCH /api/users/password  "message": "Access Denied",

- **Password hashing:** BCrypt

- **Token format:** UUID v4 (32 characters hex)│   ├── dto/



---│   │   ├── CreateExecutiveRequest.java**403 Forbidden** (Bukan Admin)  "timestamp": "2026-02-28T10:30:00.000+07:00",



## 🚀 Next Steps│   │   ├── ExecutiveDTO.java



- [ ] Add email verification flow│   │   ├── SetPasswordRequest.java       # NEW```json  "data": null

- [ ] Add password strength validation (uppercase, number, special char)

- [ ] Add rate limiting for password attempts│   │   └── UpdatePasswordRequest.java    # NEW (UM-9)

- [ ] Add password history (prevent reusing old passwords)

- [ ] Add "Forgot Password" feature│   ├── mapper/{}



---│   │   └── ExecutiveMapper.java



**Last Updated:** 1 Maret 2026│   └── service/  "status": 403,```


│       ├── ExecutiveService.java

│       └── PasswordService.java          # NEW  "message": "Access Denied",

├── common/

│   ├── config/  "timestamp": "2026-03-01T10:30:00.000+07:00",---

│   │   └── EmailProperties.java

│   ├── exception/  "data": null

│   │   └── ConflictException.java

│   └── services/}## 📁 Files Created/Modified

│       └── EmailService.java

``````



### Modified Files### New Files

```

src/main/java/id/go/kemenkoinfra/ipfo/sifpi/---

├── auth/

│   ├── model/User.java                   # Added: passwordSetupToken, passwordSetupTokenExpiry| File | Location | Description |

│   └── repository/UserRepository.java    # Added: findByEmail, findByPasswordSetupToken

├── common/## 📁 Files Created/Modified|------|----------|-------------|

│   ├── constants/AuthPath.java           # Added: SET_PASSWORD to PUBLIC paths

│   └── exception/GlobalExceptionHandler.java  # Added: ConflictException handler| `User.java` | `usermanagement/model/` | Entity User dengan field id, email, name, phone, jabatan, passwordHash, role, isVerified, emailVerified, isActive |

src/main/resources/

└── application.yaml                      # Added: app.email config### New Files| `UserRepository.java` | `usermanagement/repository/` | JPA Repository dengan `findByEmail()`, `existsByEmail()` |

.env.example                              # Added: SendGrid env vars

```| `CreateExecutiveRequest.java` | `usermanagement/dto/` | Request DTO dengan validasi |



---| File | Location | Description || `UserDTO.java` | `usermanagement/dto/` | Response DTO (tanpa password) |



## 🔧 Configuration|------|----------|-------------|| `UserMapper.java` | `usermanagement/mapper/` | MapStruct mapper |



### Environment Variables| `CreateExecutiveRequest.java` | `usermanagement/dto/` | Request DTO dengan validasi || `UserCreationService.java` | `usermanagement/service/` | Business logic: generate password, hash, save, send email |

```bash

# SendGrid Email| `ExecutiveDTO.java` | `usermanagement/dto/` | Response DTO || `AdminUserController.java` | `usermanagement/controller/` | REST Controller dengan `@PreAuthorize("hasRole('ADMIN')")` |

SENDGRID_API_KEY=SG.xxx

EMAIL_FROM_ADDRESS=noreply@mhibrizif.site| `ExecutiveMapper.java` | `usermanagement/mapper/` | MapStruct mapper || `PasswordEncoderConfig.java` | `common/config/` | Bean BCryptPasswordEncoder |

EMAIL_FROM_NAME=SIFPI

APP_LOGIN_URL=http://localhost:3000/login| `ExecutiveService.java` | `usermanagement/service/` | Business logic: generate password, hash, save, send email || `ConflictException.java` | `common/exception/` | Custom exception untuk 409 |

```

| `AdminUserController.java` | `usermanagement/controller/` | REST Controller dengan `@PreAuthorize("hasRole('admin')")` || `EmailProperties.java` | `common/config/` | Properties untuk SendGrid |

### application.yaml

```yaml| `ConflictException.java` | `common/exception/` | Custom exception untuk 409 || `AsyncConfig.java` | `common/config/` | Enable @Async untuk email |

app:

  email:| `EmailProperties.java` | `common/config/` | Properties untuk SendGrid || `EmailService.java` | `common/services/` | SendGrid API integration |

    sendgrid-api-key: ${SENDGRID_API_KEY}

    from-address: ${EMAIL_FROM_ADDRESS:noreply@sifpi.go.id}| `EmailService.java` | `common/services/` | SendGrid API integration |

    from-name: ${EMAIL_FROM_NAME:SIFPI}

    login-url: ${APP_LOGIN_URL:http://localhost:3000/login}### Modified Files

```

### Modified Files

---

| File | Changes |

## 🧪 Testing with cURL

| File | Changes ||------|---------|

### 1. Login as Admin

```bash|------|---------|| `GlobalExceptionHandler.java` | Tambah handler untuk `ConflictException` |

curl -X POST http://localhost:8080/api/auth/login \

  -H "Content-Type: application/json" \| `User.java` | Tambah field: `phone`, `jabatan`, `isVerified`, `emailVerified`, `isActive` || `application.yaml` | Tambah konfigurasi SendGrid |

  -c cookies.txt \

  -d '{"email":"admin@sifpi.go.id","password":"admin123"}'| `GlobalExceptionHandler.java` | Tambah handler untuk `ConflictException` || `.env.example` | Tambah env vars untuk email |

```

| `application.yaml` | Tambah konfigurasi SendGrid |

### 2. Create Executive

```bash| `.env.example` | Tambah env vars untuk email |---

curl -X POST http://localhost:8080/api/register \

  -H "Content-Type: application/json" \

  -b cookies.txt \

  -d '{---## ⚙️ Environment Variables

    "email":"executive@example.com",

    "nama":"John Executive",

    "phone":"081234567890",

    "jabatan":"Direktur Utama"## ⚙️ Environment Variables```bash

  }'

```# SendGrid Email Configuration



### 3. Set Password (use token from email)```bashSENDGRID_API_KEY=your-sendgrid-api-key

```bash

curl -X POST http://localhost:8080/api/auth/set-password \# SendGrid Email ConfigurationEMAIL_FROM_ADDRESS=noreply@mhibrizif.site

  -H "Content-Type: application/json" \

  -d '{SENDGRID_API_KEY=your-sendgrid-api-keyEMAIL_FROM_NAME=SIFPI

    "token":"abc123...",

    "password":"SecurePassword123!",EMAIL_FROM_ADDRESS=noreply@mhibrizif.siteAPP_LOGIN_URL=http://localhost:3000/login

    "confirmPassword":"SecurePassword123!"

  }'EMAIL_FROM_NAME=SIFPI```

```

APP_LOGIN_URL=http://localhost:3000/login

### 4. Update Password (logged in user)

```bash```---

curl -X PATCH http://localhost:8080/api/users/password \

  -H "Content-Type: application/json" \

  -b cookies.txt \

  -d '{---## 🔄 Flow Diagram

    "currentPassword":"SecurePassword123!",

    "newPassword":"NewPassword456!"

  }'

```## 🔄 Flow Diagram```



---┌─────────────────┐



## 📧 Email Template```│  POST Request   │



Email undangan akan berisi:┌─────────────────┐│  /api/admin/    │

- Informasi akun (email, role)

- Button "Buat Password" yang mengarah ke: `{loginUrl}/set-password?token={token}`│  POST Request   ││  users/executive│

- Informasi bahwa link valid selama 24 jam

│  /api/admin/    │└────────┬────────┘

│  users/executive│         │

└────────┬────────┘         ▼

         │┌─────────────────┐

         ▼│  JWT Filter     │

┌─────────────────┐│  Check Auth     │

│  JWT Filter     │└────────┬────────┘

│  Check Auth     │         │

└────────┬────────┘         ▼

         │┌─────────────────┐

         ▼│  @PreAuthorize  │

┌─────────────────┐│  hasRole(ADMIN) │

│  @PreAuthorize  │└────────┬────────┘

│  hasRole(admin) │         │

└────────┬────────┘         ▼

         │┌─────────────────┐

         ▼│  @Valid         │

┌─────────────────┐│  Validation     │

│  @Valid         │└────────┬────────┘

│  Validation     │         │

└────────┬────────┘         ▼

         │┌─────────────────────────────────────┐

         ▼│  UserCreationService                │

┌─────────────────────────────────────┐│  ├── Check email exists? → 409      │

│  ExecutiveService                   ││  ├── Generate random password (12ch)│

│  ├── Check email exists? → 409      ││  ├── Hash password (BCrypt)         │

│  ├── Get role 'executive'           ││  ├── Set role=EXECUTIVE             │

│  ├── Generate random password (12ch)││  ├── Set isVerified=true            │

│  ├── Hash password (BCrypt)         ││  ├── Set emailVerified=true         │

│  ├── Set isVerified=true            ││  ├── Set isActive=true              │

│  ├── Set emailVerified=true         ││  ├── Save to DB                     │

│  ├── Set isActive=true              ││  └── Send email (async)             │

│  ├── Save to DB                     │└────────┬────────────────────────────┘

│  └── Send email (async via SendGrid)│         │

└────────┬────────────────────────────┘         ▼

         │┌─────────────────┐

         ▼│  201 Created    │

┌─────────────────┐│  + UserDTO      │

│  201 Created    │└─────────────────┘

│  + ExecutiveDTO │```

└─────────────────┘

```---



---## 📧 Email Template



## 📧 Email yang DikirimExecutive akan menerima email dengan:

- Kredensial login (email + generated password)

Executive akan menerima email yang berisi:- Link ke halaman login

- Kredensial login (email + generated password)- Warning untuk segera ganti password

- Link ke halaman login

- Warning untuk segera ganti password---



Email dikirim via **SendGrid API** dari `noreply@mhibrizif.site`.## 🧪 Testing Guide



---Lihat bagian Testing di bawah untuk cara test endpoint ini.


## 🧪 Testing

### Test dengan Postman

1. **Method:** `POST`
2. **URL:** `http://localhost:8080/api/admin/users/executive`
3. **Headers:** `Content-Type: application/json`
4. **Auth:** Login sebagai admin dulu untuk dapat JWT cookie
5. **Body:**
```json
{
  "email": "executive@example.com",
  "nama": "John Doe",
  "phone": "081234567890",
  "jabatan": "Direktur Utama"
}
```

### Test Cases

| Test Case | Expected Result |
|-----------|-----------------|
| Create executive dengan data valid | 201 Created + ExecutiveDTO |
| Create dengan email yang sudah ada | 409 Conflict |
| Create dengan email format salah | 400 Bad Request |
| Create dengan nama kosong | 400 Bad Request |
| Create tanpa JWT token | 401 Unauthorized |
| Create dengan role non-admin | 403 Forbidden |


# PM-3: Read All Project (Project Owner)

## Overview
Endpoint untuk Project Owner melihat seluruh proyek yang telah mereka ajukan dan memantau status verifikasi.

## Endpoint
```
GET /api/projects/my-projects
```

## Authentication
- Required: Yes
- Role: PROJECT_OWNER
- Permission: PROJECT:READ

## Request Parameters

### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| status | ProjectStatus | No | - | Filter berdasarkan status proyek |
| page | int | No | 0 | Nomor halaman (0-indexed) |
| size | int | No | 10 | Jumlah item per halaman (max 100) |
| sortBy | string | No | createdAt | Field untuk sorting (createdAt, name, etc.) |
| sortDirection | string | No | desc | Arah sorting (asc/desc) |

### Project Status Options
- `DRAFT` - Proyek masih dalam draft
- `DIAJUKAN` - Proyek sudah diajukan
- `IN_REVIEW` - Proyek sedang dalam review
- `PERBAIKAN_DATA` - Proyek perlu perbaikan data
- `TERVERIFIKASI` - Proyek sudah terverifikasi
- `TERPUBLIKASI` - Proyek sudah dipublikasikan

## Response

### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Daftar proyek berhasil diambil.",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Pembangunan Jalan Tol",
        "sector": "TRANSPORTATION",
        "status": "DIAJUKAN",
        "location": "Jakarta - Bandung",
        "description": "Proyek pembangunan jalan tol sepanjang 150 km",
        "isSubmitted": true,
        "createdAt": "2026-03-01T10:00:00",
        "editedAt": "2026-03-05T14:30:00"
      },
      {
        "id": 2,
        "name": "PLTS Tenaga Surya",
        "sector": "ENERGY",
        "status": "DRAFT",
        "location": "Bali",
        "description": "Pembangkit listrik tenaga surya 50 MW",
        "isSubmitted": false,
        "createdAt": "2026-02-28T09:00:00",
        "editedAt": "2026-03-04T16:20:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 15,
    "totalPages": 2,
    "first": true,
    "last": false
  }
}
```

## Example Usage

### Get All My Projects (Default - Newest First)
```bash
GET /api/projects/my-projects
```

### Get My Projects - Page 2
```bash
GET /api/projects/my-projects?page=1&size=10
```

### Filter by Status - Only Submitted Projects
```bash
GET /api/projects/my-projects?status=DIAJUKAN
```

### Filter by Status - Only Draft Projects
```bash
GET /api/projects/my-projects?status=DRAFT
```

### Sort by Name (Ascending)
```bash
GET /api/projects/my-projects?sortBy=name&sortDirection=asc
```

### Sort by Creation Date (Oldest First)
```bash
GET /api/projects/my-projects?sortBy=createdAt&sortDirection=asc
```

### Combined Filters
```bash
GET /api/projects/my-projects?status=TERVERIFIKASI&page=0&size=20&sortBy=editedAt&sortDirection=desc
```

## Implementation Details

### Service Layer
- **Service**: `ReadProjectService`
- **Implementation**: `ReadProjectServiceImpl`
- **Method**: `getMyProjects(UUID ownerId, ProjectStatus status, int page, int size, String sortBy, String sortDirection)`

### Key Features
1. **Automatic User ID Extraction**: User ID diambil dari token JWT (@AuthenticationPrincipal)
2. **Pagination**: Default 10 items per halaman, maksimal 100
3. **Filtering**: Filter berdasarkan status proyek (optional)
4. **Sorting**: Support sorting berdasarkan berbagai field (default: createdAt desc)
5. **Security**: Hanya menampilkan proyek milik user yang sedang login

### Database Query
- Menggunakan `ProjectRepository.findByOwnerId()` untuk semua proyek
- Menggunakan `ProjectRepository.findByOwnerIdAndStatus()` untuk filter status
- Support Spring Data JPA pagination dan sorting

## Error Handling

### Unauthorized (401)
User tidak authenticated atau token invalid
```json
{
  "success": false,
  "message": "Unauthorized",
  "data": null
}
```

### Forbidden (403)
User tidak memiliki permission PROJECT:READ
```json
{
  "success": false,
  "message": "Access Denied",
  "data": null
}
```

## Testing

### Manual Testing dengan cURL
```bash
# Login sebagai project owner
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "projectowner@sifpi.go.id",
    "password": "projectowner123"
  }' \
  -c cookies.txt

# Get my projects
curl -X GET http://localhost:8080/api/projects/my-projects \
  -b cookies.txt

# Get my projects with filters
curl -X GET "http://localhost:8080/api/projects/my-projects?status=DIAJUKAN&page=0&size=10" \
  -b cookies.txt
```

## Notes
- Endpoint ini hanya bisa diakses oleh role PROJECT_OWNER
- User hanya bisa melihat proyek yang mereka buat sendiri (berdasarkan ownerId)
- Response menggunakan simplified DTO (`ProjectListItemDTO`) untuk performa lebih baik
- Support pagination untuk menghindari response yang terlalu besar
