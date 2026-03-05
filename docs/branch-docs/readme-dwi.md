# API Specification: Registration Features PM & Investor (UM-4 & UM-5)

Spesifikasi lengkap dengan request/response examples untuk fitur registrasi Project Owner (UM-4) dan Investor (UM-5) di aplikasi IPFO.

---

## 1. REGISTER PROJECT OWNER

### Endpoint
```
POST /api/auth/register/owner
```

### Request Body

```json
{
  "nama": "Budi Santoso",
  "email": "budi.santoso@example.com",
  "jabatan": "Direktur Proyek",
  "organisasi": "PT Infrastruktur Indonesia",
  "phone": "081234567890",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!"
}
```

### Request Field Specification

| Field | Tipe Data | Required | Validasi | Deskripsi |
|-------|-----------|----------|----------|-----------|
| `nama` | String | ✅ Yes | Max 255 char, non-empty | Nama lengkap project owner |
| `email` | String | ✅ Yes | Valid email format, max 255 char, unique | Email resmi (tidak boleh duplikat) |
| `organisasi` | String | ✅ Yes | Max 255 char, non-empty | Nama organisasi/instansi |
| `jabatan` | String | ✅ Yes | Max 100 char, non-empty | Jabatan di organisasi |
| `phone` | String | ✅ Yes | Max 20 char, non-empty | Nomor telepon (format: 08xxxxxxxxxx atau +62xxxxxxxxxx) |
| `password` | String | ✅ Yes | Min 6 char | Password (akan di-hash dengan BCrypt) |
| `confirmPassword` | String | ✅ Yes | Harus match dengan password | Konfirmasi password (untuk validasi di frontend) |

### Validasi Rules

1. **Email:**
   - Format valid (RFC 5322)
   - Belum terdaftar di sistem (unique)
   - Case-insensitive untuk pengecekan duplikat

2. **Password:**
   - Minimum 6 karakter
   - Password dan confirmPassword harus match
   - Akan di-hash menggunakan BCryptPasswordEncoder sebelum disimpan

3. **Phone:**
   - Format: `08xxxxxxxxxx` atau `+62xxxxxxxxxx`
   - Max 20 karakter

4. **Organisasi & Jabatan:**
   - Non-empty strings
   - Max length sesuai specification

### Success Response (201 Created)

```json
{
  "status": 201,
  "message": "Berhasil mendaftar sebagai Project Owner.",
  "timestamp": "2026-03-04T10:30:45.123456Z",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nama": "Budi Santoso",
    "email": "budi.santoso@example.com",
    "jabatan": "Direktur Proyek",
    "organisasi": "PT Infrastruktur Indonesia",
    "phone": "081234567890",
    "isVerified": false,
    "isActive": true,
    "roleName": "PROJECT_OWNER",
    "createdAt": "2026-03-04T10:30:45.123456Z"
  }
}
```

### Response Field Specification (Success)

| Field | Tipe Data | Deskripsi |
|-------|-----------|-----------|
| `id` | UUID | User ID yang di-generate |
| `nama` | String | Nama lengkap user |
| `email` | String | Email user |
| `organisasi` | String | Organisasi/instansi |
| `jabatan` | String | Jabatan user |
| `phone` | String | Nomor telepon |
| `roleName` | String | Role user: `PROJECT_OWNER` |
| `isActive` | Boolean | Status aktif user (default: `true`) |
| `isVerified` | Boolean | Status verifikasi email (default: `false` - pending email verification) |
| `createdAt` | ISO 8601 | Timestamp user created |

### Error Response (400 Bad Request - Password Mismatch)

```json
{
  "status": 400,
  "message": "Permintaan tidak valid: Password dan konfirmasi password tidak cocok",
  "timestamp": "2026-03-01T23:35:29.613+07:00",
  "data": null
}
```

### Error Response (409 Conflict - Duplicate Email)

```json
{
  "status": 409,
  "message": "Email sudah terdaftar",
  "timestamp": "2026-03-01T23:35:29.613+07:00",
  "data": null
}
```

### Error Response (400 Bad Request - Validation Error)

```json
{
  "status": 400,
  "message": "Permintaan tidak valid: Nama wajib diisi",
  "timestamp": "2026-03-01T23:35:29.613+07:00",
  "data": null
}
```

### Business Logic

1. Validasi semua field required
2. Cek email tidak duplikat → jika duplikat throw `ConflictException` (409)
3. Cek password dan confirmPassword match → jika tidak match throw `BadRequestException` (400)
4. Get role `PROJECT_OWNER` dari database
5. Hash password menggunakan `BCryptPasswordEncoder`
6. Create User entity dengan:
   - `role` = PROJECT_OWNER
   - `isActive` = true
   - `isVerified` = false (pending admin verification)
   - `password` = hashed password
   - Field lainnya sesuai request
7. Save user ke database
8. Return UserDTO dengan 201 Created

---

## 2. REGISTER INVESTOR

### Endpoint
```
POST /api/auth/register/investor
```

### Request Body

```json
{
  "nama": "Rina Wijaya",
  "email": "rina.wijaya@example.com",
  "jabatan": "Investment Manager",
  "organisasi": "PT Dana Investasi Nasional",
  "phone": "082345678901",
  "password": "InvestPass456!",
  "confirmPassword": "InvestPass456!",
  "budgetInvestasi": "1-5",
  "sectorInterest": [
    "TRANSPORTASI",
    "AIR_BERSIH",
    "ENERGI"
  ],
  "preferredInvestmentInstrument": "Equity",
  "engagementModel": "Direct Investment",
  "stagePreference": "Greenfield",
  "riskAppetite": "Moderate",
  "esgStandards": "IFC Performance Standards",
  "localPresence": "Jakarta Office",
  "aumSize": "USD 100M",
  "optInEmail": true,
  "agreePrivacy": true
}
```

### Request Field Specification

| Field | Tipe Data | Required | Validasi | Deskripsi |
|-------|-----------|----------|----------|-----------|
| `nama` | String | ✅ Yes | Max 255 char, non-empty | Nama lengkap investor |
| `email` | String | ✅ Yes | Valid email format, max 255 char, unique | Email resmi (tidak boleh duplikat) |
| `organisasi` | String | ✅ Yes | Max 255 char, non-empty | Nama organisasi/instansi investor |
| `jabatan` | String | ✅ Yes | Max 100 char, non-empty | Jabatan di organisasi |
| `phone` | String | ✅ Yes | Max 20 char, non-empty | Nomor telepon |
| `password` | String | ✅ Yes | Min 8 char | Password (akan di-hash) |
| `confirmPassword` | String | ✅ Yes | Harus match dengan password | Konfirmasi password |
| `budgetInvestasi` | String | ✅ Yes | Max 50 char, non-empty, dari enum BudgetRange | Rentang budget investasi (nilai: `<1`, `1-5`, `5-10`, `>10`) |
| `sectorInterest` | Array<String> | ✅ Yes | Min 3 items, nilai dari enum Sector | Array sektor yang dipilih (minimal 3 sektor) |
| `preferredInvestmentInstrument` | String | ❌ No | Max 255 char | Instrumen investasi yang disukai (e.g., Equity, Debt, Hybrid) |
| `engagementModel` | String | ❌ No | Max 255 char | Model engagement investasi (e.g., Direct Investment, Co-Investment) |
| `stagePreference` | String | ❌ No | Greenfield atau Brownfield | Preferensi stage proyek (pilihan: Greenfield, Brownfield) |
| `riskAppetite` | String | ❌ No | Max 255 char | Tingkat risk appetite (e.g., Conservative, Moderate, Aggressive) |
| `esgStandards` | String | ❌ No | Max 255 char | ESG standards yang diikuti (e.g., IFC Performance Standards) |
| `localPresence` | String | ❌ No | Max 255 char | Kehadiran lokal (e.g., Jakarta Office, Regional Presence) |
| `aumSize` | String | ❌ No | Max 255 char | Assets Under Management size (e.g., USD 100M, USD 1B+) |
| `optInEmail` | Boolean | ❌ No | - | Opt-in untuk update katalog proyek via email (default: false) |
| `agreePrivacy` | Boolean | ✅ Yes | Harus `true` | Persetujuan data benar & kebijakan privasi (REQUIRED TRUE) |

### Valid Sector Options (Sektor Interest)

Gunakan **value** (bukan label) dalam request:

| Value | Label |
|-------|-------|
| `AIR_BERSIH` | Air Bersih |
| `TRANSPORTASI` | Transportasi |
| `ENERGI` | Energi |
| `SAMPAH` | Sampah |
| `PERUMAHAN` | Perumahan |
| `MBGI` | MBGI |

**Tip:** Frontend dapat memanggil `GET /api/auth/register/registration-options` untuk mendapatkan daftar lengkap opsi dengan labels.

### Valid Stage Preference Options (Stage Preference)

Gunakan salah satu dari pilihan berikut:

| Value | Deskripsi |
|-------|-----------|
| `Greenfield` | Proyek baru (dari nol) |
| `Brownfield` | Proyek yang sudah ada atau pengembangan |

### Valid Budget Range Options (Budget Investasi)

Gunakan **value** dalam request:

| Value | Label |
|-------|-------|
| `<1` | < 1 Miliar |
| `1-5` | 1-5 Miliar |
| `5-10` | 5-10 Miliar |
| `>10` | > 10 Miliar |

### Validasi Rules

1. **Email:**
   - Format valid (RFC 5322)
   - Belum terdaftar (unique)
   - Case-insensitive untuk pengecekan duplikat

2. **Password:**
   - Minimum 6 karakter
   - Password dan confirmPassword harus match
   - Akan di-hash menggunakan BCryptPasswordEncoder

3. **Sektor Prioritas:**
   - Minimum 3 sektor harus dipilih
   - Maksimum 6 sektor
   - Hanya dari opsi yang valid
   - Tidak boleh ada duplikat dalam list

4. **Agree Privacy:**
   - HARUS `true` (mandatory checkbox)
   - Jika `false` → reject dengan 400 Bad Request

5. **Budget Investasi:**
   - Hanya dari opsi yang valid
   - Non-empty

6. **Phone & Organisasi:**
   - Sama seperti Project Owner

### Success Response (201 Created)

```json
{
  "status": 201,
  "message": "Berhasil mendaftar sebagai Investor.",
  "timestamp": "2026-03-04T10:35:20.654321Z",
  "data": {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "nama": "Rina Wijaya",
    "email": "rina.wijaya@example.com",
    "jabatan": "Investment Manager",
    "organisasi": "PT Dana Investasi Nasional",
    "phone": "082345678901",
    "isVerified": false,
    "isActive": true,
    "roleName": "INVESTOR",
    "budgetInvestasi": "1-5",
    "sectorInterest": [
      "TRANSPORTASI",
      "AIR_BERSIH",
      "ENERGI"
    ],
    "preferredInvestmentInstrument": "Equity",
    "engagementModel": "Direct Investment",
    "stagePreference": "Greenfield",
    "riskAppetite": "Moderate",
    "esgStandards": "IFC Performance Standards",
    "localPresence": "Jakarta Office",
    "aumSize": "USD 100M",
    "optInEmail": true,
    "agreePrivacy": true,
    "createdAt": "2026-03-04T10:35:20.654321Z"
  }
}
```

### Response Field Specification (Success)

| Field | Tipe Data | Deskripsi |
|-------|-----------|-----------|
| `id` | UUID | User ID yang di-generate |
| `nama` | String | Nama lengkap user |
| `email` | String | Email user |
| `organisasi` | String | Organisasi/instansi |
| `jabatan` | String | Jabatan user |
| `phone` | String | Nomor telepon |
| `roleName` | String | Role user: `INVESTOR` |
| `isActive` | Boolean | Status aktif user (default: `true`) |
| `isVerified` | Boolean | Status verifikasi email (default: `false` - pending email verification) |
| `budgetInvestasi` | String | Rentang budget yang dipilih (value dari enum, e.g., `1-5`) |
| `sectorInterest` | Array<String> | Daftar sektor yang dipilih (values dari enum, e.g., `[TRANSPORTASI, AIR_BERSIH]`) |
| `preferredInvestmentInstrument` | String | Instrumen investasi yang disukai (optional, null jika tidak diisi) |
| `engagementModel` | String | Model engagement investasi (optional, null jika tidak diisi) |
| `stagePreference` | String | Preferensi stage proyek (optional, null jika tidak diisi) |
| `riskAppetite` | String | Tingkat risk appetite (optional, null jika tidak diisi) |
| `esgStandards` | String | ESG standards yang diikuti (optional, null jika tidak diisi) |
| `localPresence` | String | Kehadiran lokal (optional, null jika tidak diisi) |
| `aumSize` | String | Assets Under Management size (optional, null jika tidak diisi) |
| `optInEmail` | Boolean | Status opt-in email |
| `agreePrivacy` | Boolean | Status persetujuan kebijakan privasi |
| `createdAt` | ISO 8601 | Timestamp user created |

### Error Response (400 Bad Request - Sector < 3)

```json
{
  "status": 400,
  "message": "Minimal 3 sektor prioritas harus dipilih",
  "timestamp": "2026-03-04T10:42:18.234567Z",
  "data": null
}
```

### Error Response (400 Bad Request - Invalid Sector Value)

```json
{
  "status": 400,
  "message": "Sektor tidak valid: INVALID_SECTOR",
  "timestamp": "2026-03-04T10:42:18.234567Z",
  "data": null
}
```

### Error Response (400 Bad Request - Agreement Not Accepted)

```json
{
  "status": 400,
  "message": "Anda harus menyetujui syarat dan kebijakan privasi",
  "timestamp": "2026-03-04T10:43:10.123456Z",
  "data": null
}
```

### Error Response (400 Bad Request - Password Mismatch)

```json
{
  "status": 400,
  "message": "Password dan konfirmasi password tidak cocok",
  "timestamp": "2026-03-04T10:41:22.456789Z",
  "data": null
}
```

### Error Response (400 Bad Request - Invalid Budget)

```json
{
  "status": 400,
  "message": "Budget investasi tidak valid",
  "timestamp": "2026-03-04T10:42:18.234567Z",
  "data": null
}
```

### Error Response (409 Conflict - Duplicate Email)

```json
{
  "status": 409,
  "message": "Email sudah terdaftar",
  "timestamp": "2026-03-04T10:40:15.789012Z",
  "data": null
}
```

### Business Logic

1. Validasi semua field required (via `@Valid` annotation)
2. Cek `budgetInvestasi` adalah value yang valid dari enum `BudgetRange` → jika tidak throw `IllegalArgumentException` (400)
3. Cek email tidak duplikat → jika duplikat throw `ConflictException` (409)
4. Cek password dan confirmPassword match → jika tidak match throw `IllegalArgumentException` (400)
5. Cek `sectorInterest` minimal 3 items → jika < 3 throw `IllegalArgumentException` (400)
6. Validasi setiap nilai dalam `sectorInterest` adalah value yang valid dari enum `Sector` → jika ada yang invalid throw `IllegalArgumentException` (400)
7. Cek `agreePrivacy` = true → jika false throw `IllegalArgumentException` (400)
8. Get role `INVESTOR` dari database
9. Hash password menggunakan `BCryptPasswordEncoder`
10. Convert `sectorInterest` list to comma-separated string untuk storage di `investor_profiles.sector_interest`
11. Create dan save User entity dengan:
    - `role` = INVESTOR
    - `isActive` = true
    - `isVerified` = false (pending admin verification)
    - `password` = hashed password
    - Field lainnya sesuai request
12. Create dan save InvestorProfile entity dengan:
    - `budgetRange` = `budgetInvestasi` value
    - `sectorInterest` = comma-separated string
    - `optInEmail` = subscription preference
    - `agreePrivacy` = true
13. Set bidirectional relationship (User ↔ InvestorProfile)
14. Reload User dari database untuk memastikan InvestorProfile ter-load
15. Load email template `investor-registration.html` dan kirim email async (non-blocking)
16. Map User + InvestorProfile ke InvestorDTO
17. Return dengan HTTP 201 Created

---

## 3. ENDPOINT METADATA

### GET /api/auth/register/registration-options

Endpoint publik untuk mendapatkan daftar opsi yang valid untuk dropdown dan checkbox di UI frontend.

**Response (200 OK):**

```json
{
  "budgetOptions": [
    { "value": "<1", "label": "< 1 Miliar" },
    { "value": "1-5", "label": "1-5 Miliar" },
    { "value": "5-10", "label": "5-10 Miliar" },
    { "value": ">10", "label": "> 10 Miliar" }
  ],
  "sectorOptions": [
    { "value": "AIR_BERSIH", "label": "Air Bersih" },
    { "value": "TRANSPORTASI", "label": "Transportasi" },
    { "value": "ENERGI", "label": "Energi" },
    { "value": "SAMPAH", "label": "Sampah" },
    { "value": "PERUMAHAN", "label": "Perumahan" },
    { "value": "MBGI", "label": "MBGI" }
  ]
}
```

**Keterangan:**
- Endpoint ini **publik** (tidak perlu authentication)
- Frontend panggil endpoint ini saat load registration page
- Gunakan `value` dalam request body, tampilkan `label` di UI

---

## 4. HTTP Status Code Reference

| Code | Scenario |
|------|----------|
| `200 OK` | Metadata retrieved successfully (GET /registration-options) |
| `201 Created` | Registration berhasil |
| `400 Bad Request` | Validasi gagal (password mismatch, sector < 3, invalid sector/budget value, agreePrivacy false, dll) |
| `409 Conflict` | Email sudah terdaftar |
| `500 Internal Server Error` | Server error |

---

## 5. Error Message Convention

Error messages dari service layer:
- Field validation errors (dari `@Valid` annotation): Sesuai message di DTO
- Business logic errors (email duplikat): `"Email sudah terdaftar"`
- Invalid sector: `"Sektor tidak valid: <values>"`
- Invalid budget: `"Budget investasi tidak valid"`
- Password mismatch: `"Password dan konfirmasi password tidak cocok"`
- Minimum sector: `"Minimal 3 sektor prioritas harus dipilih"`
- Agreement: `"Anda harus menyetujui syarat dan kebijakan privasi"`

Error response wrapper dari GlobalExceptionHandler:
```json
{
  "status": <HTTP_STATUS>,
  "message": "<error message>",
  "timestamp": "ISO_8601_TIMESTAMP",
  "data": null
}
```

---

## 6. Security Considerations

1. **Password Hashing:**
   - Semua password harus di-hash dengan BCryptPasswordEncoder sebelum disimpan
   - Jangan pernah return password di response

2. **Email Verification:**
   - Email belum terverifikasi setelah registration (isVerified = false)
   - Admin harus approve account sebelum bisa digunakan

3. **Data Privacy:**
   - Implement proper logging (jangan log password atau sensitive data)
   - Validate semua input di server-side
   - Implement rate limiting untuk prevent brute force

4. **SQL Injection & CSRF:**
   - Gunakan parameterized queries (JPA/Hibernate)
   - CSRF protection enabled di SecurityConfig

---

## 7. Implementation Notes

### DTOs

**CreateOwnerRequest:**
```java
@Data
public class CreateOwnerRequest {
    @NotBlank private String nama;
    @NotBlank @Email private String email;
    @NotBlank private String organisasi;
    @NotBlank private String jabatan;
    @NotBlank private String phone;
    @NotBlank @Size(min=6) private String password;
    @NotBlank private String confirmPassword;
}
```

**CreateInvestorRequest:**
```java
@Data
public class CreateInvestorRequest {
    @NotBlank private String nama;
    @NotBlank @Email private String email;
    @NotBlank private String organisasi;
    @NotBlank private String jabatan;
    @NotBlank private String phone;
    @NotBlank @Size(min=8) private String password;
    @NotBlank private String confirmPassword;
    @NotBlank private String budgetInvestasi;
    @NotEmpty @Size(min=3) private List<String> sectorInterest;
    
    // Additional optional fields (Investor Catalogue Template)
    @Size(max=255) private String preferredInvestmentInstrument;
    @Size(max=255) private String engagementModel;
    @Size(max=255) private String stagePreference;
    @Size(max=255) private String riskAppetite;
    @Size(max=255) private String esgStandards;
    @Size(max=255) private String localPresence;
    @Size(max=255) private String aumSize;
    
    private Boolean optInEmail = false;
    @NotNull @AssertTrue private Boolean agreePrivacy;
}
```

**OwnerDTO (Response - Project Owner):**
```java
@Data
public class OwnerDTO {
    private UUID id;
    private String nama;
    private String email;
    private String organisasi;
    private String jabatan;
    private String phone;
    private String roleName;
    private Boolean isActive;
    private Boolean isVerified;
    private LocalDateTime createdAt;
}
```

**InvestorDTO (Response - Investor):**
```java
@Data
public class InvestorDTO {
    private UUID id;
    private String nama;
    private String email;
    private String organisasi;
    private String jabatan;
    private String phone;
    private String roleName;
    private Boolean isActive;
    private Boolean isVerified;
    private String budgetInvestasi;    // value dari enum (e.g., "1-5")
    private List<String> sectorInterest; // values dari enum (e.g., ["TRANSPORTASI", "AIR_BERSIH"])
    
    // Additional optional fields (may be null)
    private String preferredInvestmentInstrument;
    private String engagementModel;
    private String stagePreference;
    private String riskAppetite;
    private String esgStandards;
    private String localPresence;
    private String aumSize;
    
    private Boolean optInEmail;
    private Boolean agreePrivacy;
    
    private LocalDateTime createdAt;
}
```

### Services

**OwnerRegistrationService:**
- Validate CreateOwnerRequest
- Check email uniqueness
- Hash password
- Create & save User entity
- Return UserDTO

**InvestorRegistrationService:**
- Validate CreateInvestorRequest (including sector count & agreement)
- Check email uniqueness
- Hash password
- Create & save User entity with investor data
- Return UserDTO

### Controller

**RegistrationController:**
```java
@PostMapping("/owner")
public ResponseEntity<BaseResponseDTO<UserDTO>> registerOwner(
    @Valid @RequestBody CreateOwnerRequest request)

@PostMapping("/investor")
public ResponseEntity<BaseResponseDTO<UserDTO>> registerInvestor(
    @Valid @RequestBody CreateInvestorRequest request)
```

---

## 8. Testing Checklist

### Get Registration Options
- ✅ GET /api/auth/register/registration-options → 200 OK with budgetOptions & sectorOptions

### Project Owner Registration
- ✅ Valid request → 201 Created
- ✅ Duplicate email → 409 Conflict
- ✅ Password mismatch → 400 Bad Request
- ✅ Missing required field → 400 Bad Request
- ✅ Invalid email format → 400 Bad Request
- ✅ Password < 8 chars → 400 Bad Request (validation)

### Investor Registration
- ✅ Valid request → 201 Created
- ✅ Duplicate email → 409 Conflict
- ✅ Password mismatch → 400 Bad Request
- ✅ Sector < 3 → 400 Bad Request
- ✅ Invalid sector value (e.g., "INVALID_SECTOR") → 400 Bad Request
- ✅ Invalid budget value (e.g., "100 Miliar") → 400 Bad Request
- ✅ agreePrivacy = false → 400 Bad Request
- ✅ Missing required field → 400 Bad Request

---

## 9. API Integration Quick Start

### Step 1: Get Registration Options
```bash
curl -s http://localhost:8080/api/auth/register/registration-options | jq
```

### Step 2: Register Project Owner
```bash
curl -X POST http://localhost:8080/api/auth/register/owner \
  -H "Content-Type: application/json" \
  -d '{
    "nama": "Budi Santoso",
    "email": "budi.test@example.com",
    "jabatan": "Direktur Proyek",
    "organisasi": "PT Infrastruktur Indonesia",
    "phone": "081234567890",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!"
  }' | jq
```

### Step 3: Register Investor
```bash
curl -X POST http://localhost:8080/api/auth/register/investor \
  -H "Content-Type: application/json" \
  -d '{
    "nama": "Rina Wijaya",
    "email": "rina.test@example.com",
    "jabatan": "Investment Manager",
    "organisasi": "PT Dana Investasi Nasional",
    "phone": "082345678901",
    "password": "InvestPass456!",
    "confirmPassword": "InvestPass456!",
    "budgetInvestasi": "1-5",
    "sectorInterest": ["TRANSPORTASI", "AIR_BERSIH", "ENERGI"],
    "preferredInvestmentInstrument": "Equity",
    "engagementModel": "Direct Investment",
    "stagePreference": "Greenfield",
    "riskAppetite": "Moderate",
    "esgStandards": "IFC Performance Standards",
    "localPresence": "Jakarta Office",
    "aumSize": "USD 100M",
    "optInEmail": true,
    "agreePrivacy": true
```

**Valid stagePreference values:**
- `Greenfield` - Proyek baru
- `Brownfield` - Proyek yang sudah ada/dikembangkan
  }' | jq
```

**Note:** Field tambahan (preferredInvestmentInstrument, engagementModel, dll) bersifat **optional** dan bisa dihilangkan dari request jika tidak diperlukan.

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 2.2 | 2026-03-05 | Final cleanup: User fields optimized (emailVerified + isActive only), event-driven email implemented, enum fields relocated to common/enums |
| 2.1 | 2026-03-05 | Added 7 optional fields to InvestorProfile (preferredInvestmentInstrument, engagementModel, stagePreference, riskAppetite, esgStandards, localPresence, aumSize) for Investor Catalogue Template |
| 2.0 | 2026-03-04 | Merged docs + added enum values + registration-options endpoint |
| 1.0 | 2026-03-01 | Initial specification |

