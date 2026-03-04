# API Specification: Registration Features PM & Investor (UM-4 & UM-5)

## Overview
Spesifikasi lengkap untuk fitur registrasi Project Owner (UM-4) dan Investor (UM-5) di aplikasi IPFO.

---

## 1. REGISTER PROJECT OWNER

### Endpoint
```
POST /api/auth/register/owner
```

### Request Body

```json
{
  "nama": "John Doe",
  "email": "john.doe@example.com",
  "organisasi": "PT Example Indonesia",
  "jabatan": "Manager Proyek",
  "phone": "081234567890",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!"
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
  "timestamp": "2026-03-01T23:35:29.613+07:00",
  "data": {
    "id": "23e4c9af-b628-ddcf-b3c1-3dd95f2d19d9",
    "nama": "John Doe",
    "email": "john.doe@example.com",
    "organisasi": "PT Example Indonesia",
    "jabatan": "Manager Proyek",
    "phone": "081234567890",
    "role": "PROJECT_OWNER",
    "isActive": true,
    "isVerified": false,
    "createdAt": "2026-03-01T23:35:29.613+07:00"
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
| `role` | String | Role user: `PROJECT_OWNER` |
| `isActive` | Boolean | Status aktif user (default: `true`) |
| `isVerified` | Boolean | Status verifikasi email (default: `false` - pending admin review) |
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
  "nama": "Jane Doe",
  "email": "jane.doe@example.com",
  "organisasi": "PT Investasi Nusantara",
  "jabatan": "Direktur Investasi",
  "phone": "082134567890",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!",
  "budgetInvestasi": "1-5 Miliar",
  "sektorPrioritas": [
    "Air Bersih",
    "Transportasi",
    "Energi"
  ],
  "optInEmail": false,
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
| `password` | String | ✅ Yes | Min 6 char | Password (akan di-hash) |
| `confirmPassword` | String | ✅ Yes | Harus match dengan password | Konfirmasi password |
| `budgetInvestasi` | String | ✅ Yes | Max 50 char, non-empty | Rentang budget investasi (pilihan dropdown di FE) |
| `sektorPrioritas` | Array<String> | ✅ Yes | Min 3 items, max 6 items | Array sektor yang dipilih (minimal 3, maksimal 6) |
| `optInEmail` | Boolean | ❌ No | - | Opt-in untuk update katalog proyek via email (default: false) |
| `agreePrivacy` | Boolean | ✅ Yes | Harus `true` | Persetujuan data benar & kebijakan privasi (REQUIRED TRUE) |

### Valid Sector Options (Sektor Prioritas)

- `Air Bersih`
- `Sampah`
- `Transportasi`
- `Perumahan`
- `Energi`
- `MBG` (Mitigasi Bencana Gempa)

### Valid Budget Range Options (Budget Investasi)

- `< 500 Juta`
- `500 Juta - 1 Miliar`
- `1-5 Miliar`
- `5-10 Miliar`
- `10-50 Miliar`
- `> 50 Miliar`

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
  "timestamp": "2026-03-01T23:40:15.892+07:00",
  "data": {
    "id": "45f6d2ba-c781-4a9b-b5e2-7c9e3f1d2a8b",
    "nama": "Jane Doe",
    "email": "jane.doe@example.com",
    "organisasi": "PT Investasi Nusantara",
    "jabatan": "Direktur Investasi",
    "phone": "082134567890",
    "role": "INVESTOR",
    "isActive": true,
    "isVerified": false,
    "budgetInvestasi": "1-5 Miliar",
    "sektorPrioritas": [
      "Air Bersih",
      "Transportasi",
      "Energi"
    ],
    "optInEmail": false,
    "createdAt": "2026-03-01T23:40:15.892+07:00"
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
| `role` | String | Role user: `INVESTOR` |
| `isActive` | Boolean | Status aktif user (default: `true`) |
| `isVerified` | Boolean | Status verifikasi (default: `false` - pending admin review) |
| `budgetInvestasi` | String | Rentang budget yang dipilih |
| `sektorPrioritas` | Array<String> | Daftar sektor yang dipilih |
| `optInEmail` | Boolean | Status opt-in email |
| `createdAt` | ISO 8601 | Timestamp user created |

### Error Response (400 Bad Request - Sector < 3)

```json
{
  "status": 400,
  "message": "Permintaan tidak valid: Minimal 3 sektor prioritas harus dipilih",
  "timestamp": "2026-03-01T23:40:15.892+07:00",
  "data": null
}
```

### Error Response (400 Bad Request - Agreement Not Accepted)

```json
{
  "status": 400,
  "message": "Permintaan tidak valid: Anda harus menyetujui syarat dan kebijakan privasi",
  "timestamp": "2026-03-01T23:40:15.892+07:00",
  "data": null
}
```

### Error Response (400 Bad Request - Password Mismatch)

```json
{
  "status": 400,
  "message": "Permintaan tidak valid: Password dan konfirmasi password tidak cocok",
  "timestamp": "2026-03-01T23:40:15.892+07:00",
  "data": null
}
```

### Error Response (409 Conflict - Duplicate Email)

```json
{
  "status": 409,
  "message": "Email sudah terdaftar",
  "timestamp": "2026-03-01T23:40:15.892+07:00",
  "data": null
}
```

### Business Logic

1. Validasi semua field required
2. Cek email tidak duplikat → jika duplikat throw `ConflictException` (409)
3. Cek password dan confirmPassword match → jika tidak match throw `BadRequestException` (400)
4. Cek sectorPrioritas minimal 3 items → jika < 3 throw `BadRequestException` (400)
5. Cek agreePrivacy = true → jika false throw `BadRequestException` (400)
6. Get role `INVESTOR` dari database
7. Hash password menggunakan `BCryptPasswordEncoder`
8. Convert sectorPrioritas array to comma-separated string untuk storage
9. Create User entity dengan:
   - `role` = INVESTOR
   - `isActive` = true
   - `isVerified` = false (pending admin verification)
   - `password` = hashed password
   - `budgetInvestasi` = budget yang dipilih
   - `sectorPrioritas` = comma-separated string
   - `optInEmail` = subscription preference
   - Field lainnya sesuai request
10. Save user ke database
11. Return UserDTO dengan 201 Created
12. (Optional) Kirim email verifikasi atau notifikasi

---

## 3. HTTP Status Code Reference

| Code | Scenario |
|------|----------|
| `201 Created` | Registration berhasil |
| `400 Bad Request` | Validasi gagal (password mismatch, sector < 3, agreementFalse, dll) |
| `409 Conflict` | Email sudah terdaftar |
| `422 Unprocessable Entity` | Data format invalid (misal: sector yang tidak valid) |
| `500 Internal Server Error` | Server error |

---

## 4. Error Message Convention

Semua error message mengikuti format:
```
"Permintaan tidak valid: <detail error>"
```

Atau untuk conflict:
```
"<resource> sudah terdaftar"
```

---

## 5. Security Considerations

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

## 6. Implementation Notes

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
    @NotBlank @Size(min=6) private String password;
    @NotBlank private String confirmPassword;
    @NotBlank private String budgetInvestasi;
    @NotEmpty @Size(min=3, max=6) private List<String> sektorPrioritas;
    private Boolean optInEmail = false;
    @NotNull @AssertTrue private Boolean agreePrivacy;
}
```

**UserDTO (Response):**
```java
@Data
public class UserDTO {
    private UUID id;
    private String nama;
    private String email;
    private String organisasi;
    private String jabatan;
    private String phone;
    private String role;
    private Boolean isActive;
    private Boolean isVerified;
    private String budgetInvestasi;    // nullable untuk Project Owner
    private List<String> sektorPrioritas; // nullable untuk Project Owner
    private Boolean optInEmail;         // nullable untuk Project Owner
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

## 7. Testing Checklist

### Project Owner Registration
- ✅ Valid request → 201 Created
- ✅ Duplicate email → 409 Conflict
- ✅ Password mismatch → 400 Bad Request
- ✅ Missing required field → 400 Bad Request
- ✅ Invalid email format → 400 Bad Request

### Investor Registration
- ✅ Valid request → 201 Created
- ✅ Duplicate email → 409 Conflict
- ✅ Password mismatch → 400 Bad Request
- ✅ Sector < 3 → 400 Bad Request
- ✅ agreePrivacy = false → 400 Bad Request
- ✅ Missing required field → 400 Bad Request
- ✅ Invalid sector in list → 422 Unprocessable Entity

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-03-01 | Initial specification |

