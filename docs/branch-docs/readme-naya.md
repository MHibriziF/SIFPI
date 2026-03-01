# User Management - Create Executive Account & Password Management# User Management - Create Executive Account (UM-3)# User Management - Create Executive Account



**Author:** Valiza Nadya Jatikansha  

**Date:** 1 Maret 2026  

**Branch:** `dev/naya`**Author:** Valiza Nadya Jatikansha  **Author:** Naya  



---**Date:** 1 Maret 2026  **Date:** 28 Februari 2026  



## 📋 Backlog**Branch:** `dev/naya`**Branch:** `feature/user-management`



### Epic: User Management



| ID | Backlog Item | Status |------

|----|--------------|--------|

| UM-3 | Create Executive Account (by Admin) | ✅ Done |

| UM-9 | Update Password | ✅ Done |

## 📋 Backlog## 📋 Backlog

---



## 🎯 Feature 1: Create Executive Account (UM-3)

### Epic: User Management### Epic: User Management

### Deskripsi

Admin dapat membuat akun Executive untuk pejabat tertentu melalui dashboard. Sistem akan mengirim email undangan dengan link untuk membuat password.



### Flow| ID | Backlog Item | Status || Backlog Item | Status |

1. Admin mengisi form create executive (email, nama, phone, jabatan)

2. Sistem membuat akun dengan status inactive|----|--------------|--------||--------------|--------|

3. Sistem generate token dan kirim email undangan

4. Executive klik link di email → halaman set password| UM-3 | Create Executive Account (by Admin) | ✅ Done || Create Executive Account (by Admin) | ✅ Done |

5. Executive membuat password → akun diaktifkan



### Endpoint Create Executive

------

```

POST /api/register

```

## 🎯 Feature: Create Executive Account## 🎯 Feature: Create Executive Account

### Authorization

- **Role Required:** `ADMIN`

- Menggunakan JWT Cookie authentication

### Deskripsi### Deskripsi

### Request Body

Admin dapat membuat akun Executive untuk pejabat tertentu melalui dashboard. Executive dapat mengakses dashboard insight untuk mendukung pengambilan kebijakan.Admin dapat membuat akun Executive baru. Password di-generate otomatis dan dikirim ke email Executive melalui SendGrid.

```json

{

  "email": "executive@example.com",

  "nama": "John Doe",### User Story### Endpoint

  "phone": "081234567890",

  "jabatan": "Direktur Utama"- **AS** Admin

}

```- **I WANT TO** Membuat akun Executive untuk pejabat tertentu melalui dashboard```



| Field | Type | Required | Validation |- **SO THAT** Executive dapat mengakses dashboard insight untuk mendukung pengambilan kebijakanPOST /api/admin/users/executive

|-------|------|----------|------------|

| `email` | string | ✅ | Format email valid |```

| `nama` | string | ✅ | 2-100 karakter |

| `phone` | string | ✅ | 10-15 karakter |### Endpoint

| `jabatan` | string | ✅ | Max 100 karakter |

### Authorization

### Response

```- **Role Required:** `ADMIN`

**201 Created**

```jsonPOST /api/admin/users/executive- Menggunakan JWT Cookie authentication

{

  "status": 201,```

  "message": "Akun Executive berhasil dibuat.",

  "timestamp": "2026-03-01T10:30:00.000+07:00",### Request Body

  "data": {

    "id": "uuid",### Authorization

    "email": "executive@example.com",

    "name": "John Doe",- **Role Required:** `admin````json

    "phone": "081234567890",

    "jabatan": "Direktur Utama",- Menggunakan JWT Cookie authentication{

    "roleName": "EXECUTIVE",

    "active": false,  "email": "executive@example.com",

    "verified": false

  }### Request Body  "nama": "John Doe",

}

```  "phone": "081234567890",



**Error Responses**```json  "jabatan": "Direktur Utama"

| Status | Message |

|--------|---------|{}

| 400 | Validation error |

| 401 | Unauthorized |  "email": "executive@example.com",```

| 403 | Forbidden (bukan ADMIN) |

| 409 | Email sudah terdaftar |  "nama": "John Doe",



---  "phone": "081234567890",| Field | Type | Required | Validation |



## 🎯 Feature 2: Set Password (First Time)  "jabatan": "Direktur Utama"|-------|------|----------|------------|



### Deskripsi}| `email` | string | ✅ | Format email valid |

Executive yang baru dibuat dapat membuat password pertama kali melalui link dari email.

```| `nama` | string | ✅ | 2-100 karakter |

### Endpoint

| `phone` | string | ✅ | 10-15 karakter |

```

POST /api/auth/set-password| Field | Type | Required | Validation || `jabatan` | string | ✅ | Max 100 karakter |

```

|-------|------|----------|------------|

### Authorization

- **Public** - Tidak perlu authentication| `email` | string | ✅ | Format email valid |### Response



### Request Body| `nama` | string | ✅ | 2-100 karakter |



```json| `phone` | string | ✅ | 10-20 karakter |**201 Created**

{

  "token": "abc123...",| `jabatan` | string | ✅ | Max 100 karakter |```json

  "password": "MySecurePassword123!",

  "confirmPassword": "MySecurePassword123!"{

}

```### Response  "status": 201,



| Field | Type | Required | Validation |  "message": "Akun Executive berhasil dibuat.",

|-------|------|----------|------------|

| `token` | string | ✅ | Token dari email |**201 Created**  "timestamp": "2026-02-28T10:30:00.000+07:00",

| `password` | string | ✅ | Min 8 karakter |

| `confirmPassword` | string | ✅ | Harus sama dengan password |```json  "data": {



### Response{    "id": "550e8400-e29b-41d4-a716-446655440000",



**200 OK**  "status": 201,    "email": "executive@example.com",

```json

{  "message": "Akun Executive berhasil dibuat.",    "name": "John Doe",

  "status": 200,

  "message": "Password berhasil dibuat. Silakan login.",  "timestamp": "2026-03-01T10:30:00.000+07:00",    "phone": "081234567890",

  "timestamp": "2026-03-01T10:35:00.000+07:00",

  "data": null  "data": {    "jabatan": "Direktur Utama",

}

```    "id": "550e8400-e29b-41d4-a716-446655440000",    "role": "EXECUTIVE",



**Error Responses**    "email": "executive@example.com",    "isVerified": true,

| Status | Message |

|--------|---------|    "name": "John Doe",    "emailVerified": true,

| 400 | Validation error |

| 401 | Token tidak valid / sudah kedaluwarsa |    "phone": "081234567890",    "isActive": true,

| 404 | Token tidak ditemukan |

    "jabatan": "Direktur Utama",    "createdAt": "2026-02-28T10:30:00",

---

    "roleName": "executive",    "updatedAt": "2026-02-28T10:30:00"

## 🎯 Feature 3: Update Password (UM-9)

    "isVerified": true,  }

### Deskripsi

User yang sudah login dapat mengubah password untuk menjaga keamanan akun.    "emailVerified": true,}



### Endpoint    "isActive": true,```



```    "createdAt": "2026-03-01T10:30:00",

PATCH /api/users/password

```    "updatedAt": "2026-03-01T10:30:00"**409 Conflict** (Email sudah terdaftar)



### Authorization  }```json

- **Authenticated** - Perlu login (JWT Cookie)

}{

### Request Body

```  "status": 409,

```json

{  "message": "User dengan email 'executive@example.com' sudah terdaftar.",

  "currentPassword": "OldPassword123!",

  "newPassword": "NewSecurePassword456!"**409 Conflict** (Email sudah terdaftar)  "timestamp": "2026-02-28T10:30:00.000+07:00",

}

``````json  "data": null



| Field | Type | Required | Validation |{}

|-------|------|----------|------------|

| `currentPassword` | string | ✅ | Password saat ini |  "status": 409,```

| `newPassword` | string | ✅ | Min 8 karakter, tidak boleh sama dengan current |

  "message": "User dengan email 'executive@example.com' sudah terdaftar.",

### Response

  "timestamp": "2026-03-01T10:30:00.000+07:00",**400 Bad Request** (Validasi gagal)

**200 OK**

```json  "data": null```json

{

  "status": 200,}{

  "message": "Password berhasil diubah.",

  "timestamp": "2026-03-01T10:40:00.000+07:00",```  "status": 400,

  "data": null

}  "message": "Permintaan tidak valid: email: Format email tidak valid",

```

**400 Bad Request** (Validasi gagal)  "timestamp": "2026-02-28T10:30:00.000+07:00",

**Error Responses**

| Status | Message |```json  "data": null

|--------|---------|

| 400 | Validation error |{}

| 401 | Password saat ini tidak valid |

| 401 | Password baru tidak boleh sama dengan password saat ini |  "status": 400,```



---  "message": "Permintaan tidak valid: email: Format email tidak valid",



## 📁 Files Created/Modified  "timestamp": "2026-03-01T10:30:00.000+07:00",**403 Forbidden** (Bukan Admin)



### New Files  "data": null```json

```

src/main/java/id/go/kemenkoinfra/ipfo/sifpi/}{

├── usermanagement/

│   ├── controller/```  "status": 403,

│   │   ├── AdminUserController.java      # POST /api/register

│   │   └── PasswordController.java       # POST /api/auth/set-password, PATCH /api/users/password  "message": "Access Denied",

│   ├── dto/

│   │   ├── CreateExecutiveRequest.java**403 Forbidden** (Bukan Admin)  "timestamp": "2026-02-28T10:30:00.000+07:00",

│   │   ├── ExecutiveDTO.java

│   │   ├── SetPasswordRequest.java       # NEW```json  "data": null

│   │   └── UpdatePasswordRequest.java    # NEW (UM-9)

│   ├── mapper/{}

│   │   └── ExecutiveMapper.java

│   └── service/  "status": 403,```

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
