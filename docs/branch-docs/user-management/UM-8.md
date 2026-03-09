# UM-8 — Update Profile (Self)

## Endpoint

```
PATCH /api/users/profile
```

**Authorization:** Any authenticated user. Identity is derived from the JWT — no user ID or email in the path. A user can only ever update their own profile.

---

## Request Body

`Content-Type: application/json`

### Base fields (all roles — required)

| Field          | Type     | Required | Validation                                      |
| -------------- | -------- | -------- | ----------------------------------------------- |
| `name`         | `String` | Yes      | Not blank                                       |
| `email`        | `String` | Yes      | Not blank, valid email format                   |
| `phone_number` | `String` | Yes      | Not blank, matches `^[+]?[0-9][0-9\s\-]{6,18}[0-9]$` |

### PROJECT_OWNER extra fields (optional)

| Field              | Type     | Required | Description                                  |
| ------------------ | -------- | -------- | -------------------------------------------- |
| `institution_name` | `String` | No       | Saved to `User.organization`                 |
| `position`         | `String` | No       | Saved to `User.jabatan`                      |

### INVESTOR extra fields (optional)

| Field                         | Type           | Required | Description                                       |
| ----------------------------- | -------------- | -------- | ------------------------------------------------- |
| `company_name`                | `String`       | No       | Saved to `User.organization`                      |
| `investment_interest_sectors` | `List<String>` | No       | Joined as comma-separated string in `InvestorProfile.sectorInterest` |
| `investment_scale`            | `String`       | No       | Saved to `InvestorProfile.budgetRange`            |

> Role-specific fields sent by a user whose role doesn't match are **silently ignored** (e.g. an ADMIN sending `company_name` has no effect).

---

## Request Examples

### ADMIN / EXECUTIVE

```json
{
  "name": "Ahmad Fauzi",
  "email": "ahmad.fauzi@sifpi.go.id",
  "phone_number": "085311223344"
}
```

### PROJECT_OWNER

```json
{
  "name": "Budi Santoso",
  "email": "budi@example.com",
  "phone_number": "081234567890",
  "institution_name": "PT Maju Bersama",
  "position": "Direktur"
}
```

### INVESTOR

```json
{
  "name": "Siti Rahayu",
  "email": "siti@investor.com",
  "phone_number": "082198765432",
  "company_name": "PT Investasi Nusantara",
  "investment_interest_sectors": ["Energi", "Infrastruktur", "Pariwisata"],
  "investment_scale": "10M - 50M USD"
}
```

---

## Response

Returns `200 OK` with the updated profile. Shape mirrors `UserDetailDTO` — role-specific null fields are omitted (`@JsonInclude(NON_NULL)`).

### PROJECT_OWNER

```json
{
  "status": 200,
  "message": "Profil berhasil diperbarui.",
  "timestamp": "...",
  "data": {
    "id": "a1b2c3d4-...",
    "email": "budi@example.com",
    "nama": "Budi Santoso",
    "phone": "081234567890",
    "role": "PROJECT_OWNER",
    "organisasi": "PT Maju Bersama",
    "jabatan": "Direktur",
    "created_at": "2025-01-15T08:00:00",
    "last_login": "2026-03-08T10:00:00",
    "email_verified": true,
    "jumlah_proyek": 4,
    "inquiry_masuk": 0
  }
}
```

### INVESTOR

```json
{
  "status": 200,
  "message": "Profil berhasil diperbarui.",
  "timestamp": "...",
  "data": {
    "id": "b2c3d4e5-...",
    "email": "siti@investor.com",
    "nama": "Siti Rahayu",
    "phone": "082198765432",
    "role": "INVESTOR",
    "created_at": "2025-03-10T09:30:00",
    "last_login": "2026-03-08T10:00:00",
    "email_verified": true,
    "company_info": {
      "name": "PT Investasi Nusantara",
      "sector": "Energi",
      "industry_type": null
    },
    "sector_interest": ["Energi", "Infrastruktur", "Pariwisata"],
    "budget_range": "10M - 50M USD"
  }
}
```

### EXECUTIVE / ADMIN

```json
{
  "status": 200,
  "message": "Profil berhasil diperbarui.",
  "timestamp": "...",
  "data": {
    "id": "c3d4e5f6-...",
    "email": "ahmad.fauzi@sifpi.go.id",
    "nama": "Ahmad Fauzi",
    "phone": "085311223344",
    "role": "EXECUTIVE",
    "created_at": "2025-06-01T07:00:00",
    "last_login": "2026-03-08T10:00:00",
    "email_verified": true
  }
}
```

---

## Error Responses

**400 Bad Request** — validation failure (missing required field or invalid format):

```json
{
  "status": 400,
  "message": "Format email tidak valid",
  "timestamp": "..."
}
```

**401 Unauthorized** — request has no valid JWT cookie.

**409 Conflict** — the new email is already registered to another account:

```json
{
  "status": 409,
  "message": "User dengan email 'siti@investor.com' sudah terdaftar.",
  "timestamp": "..."
}
```

---

## Validation Logic

1. `name`, `email`, `phone_number` are validated by Jakarta Bean Validation (`@NotBlank`, `@Email`, `@Pattern`) before the service is called.
2. If the `email` field differs from the current JWT email, `UserRepository.existsByEmail` is called. If it returns `true`, a `ConflictException` is thrown.
3. If `email` is unchanged (case-insensitive), the uniqueness check is skipped entirely.

---

## Implementation Notes

- **Identity from JWT only** — `@AuthenticationPrincipal String email` in the controller resolves the caller's email directly from the security context. There is no path variable; a user cannot update another user's profile.
- **Full overwrite on base fields** — `name`, `email`, and `phone_number` always replace the stored values. Omitting them causes a `400`.
- **Partial update on role-specific fields** — `institution_name`, `position`, `company_name`, `investment_interest_sectors`, and `investment_scale` are only applied when non-null in the request. Omitting them leaves the stored values untouched.
- **Investor profile upsert** — if no `InvestorProfile` row exists for the user yet, one is created on the fly during the update.
- **Transaction** — `@Transactional` on the service method. The `InvestorProfile` save, `User` save, and response enrichment all happen within the same session, preventing `LazyInitializationException`.
- **Error handling** — `NotFoundException` and `ConflictException` propagate to `GlobalExceptionHandler`; no manual catch.
- **Response enrichment** — `last_login` is derived from `UserTokenRepository.findTopByUserIdOrderByCreatedAtDesc`, same as UM-7.

---

## Files

| File | Role |
| ---- | ---- |
| `usermanagement/controller/UserProfileController.java` | REST controller — `PATCH /api/users/profile` |
| `usermanagement/service/UpdateProfileService.java` | Service interface |
| `usermanagement/service/impl/UpdateProfileServiceImpl.java` | Service implementation — role-based field application |
| `usermanagement/dto/UpdateProfileRequest.java` | Request DTO with validation annotations |
| `usermanagement/dto/UserDetailDTO.java` | Response DTO (shared with UM-7) |
| `usermanagement/mapper/UserDetailMapper.java` | MapStruct mapper used to build the response (shared with UM-7) |
| `auth/repository/UserRepository.java` | `existsByEmail` — email uniqueness check |
| `auth/repository/InvestorProfileRepository.java` | `findByUserId` — investor profile fetch/upsert |
| `auth/repository/UserTokenRepository.java` | `findTopByUserIdOrderByCreatedAtDesc` — `last_login` enrichment |
