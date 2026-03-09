# UM-7 — Read Detail Account (Admin)

## Endpoint

```
GET /api/admin/users/{email}
```

**Authorization:** Requires role `ADMIN` (`@PreAuthorize("hasRole('ADMIN')")`).

---

## Path Parameters

| Parameter | Type     | Required | Description                        |
| --------- | -------- | -------- | ---------------------------------- |
| `email`   | `String` | Yes      | Email address of the user to retrieve |

> The route uses `{email:.+}` as the Spring MVC path pattern to prevent truncation at dots (e.g. `user@gmail.com` would otherwise be matched as `user@gmail` with extension `.com`).

---

## Response

The response shape is the same for all roles. Role-specific fields are populated only for the matching role; all other role-specific fields are omitted (`@JsonInclude(NON_NULL)`).

### PROJECT_OWNER

```json
{
  "status": 200,
  "message": "Berhasil.",
  "timestamp": "...",
  "data": {
    "id": "a1b2c3d4-...",
    "email": "owner@example.com",
    "nama": "Budi Santoso",
    "phone": "081234567890",
    "role": "PROJECT_OWNER",
    "organisasi": "PT Maju Bersama",
    "jabatan": "Direktur",
    "created_at": "2025-01-15T08:00:00",
    "last_login": "2026-03-07T14:22:00",
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
  "message": "Berhasil.",
  "timestamp": "...",
  "data": {
    "id": "b2c3d4e5-...",
    "email": "investor@example.com",
    "nama": "Siti Rahayu",
    "phone": "082198765432",
    "role": "INVESTOR",
    "created_at": "2025-03-10T09:30:00",
    "last_login": "2026-03-06T11:00:00",
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

### EXECUTIVE

```json
{
  "status": 200,
  "message": "Berhasil.",
  "timestamp": "...",
  "data": {
    "id": "c3d4e5f6-...",
    "email": "executive@example.com",
    "nama": "Ahmad Fauzi",
    "phone": "085311223344",
    "role": "EXECUTIVE",
    "jabatan": "Kepala Divisi",
    "created_at": "2025-06-01T07:00:00",
    "last_login": "2026-03-08T08:45:00",
    "email_verified": true
  }
}
```

### ADMIN

```json
{
  "status": 200,
  "message": "Berhasil.",
  "timestamp": "...",
  "data": {
    "id": "d4e5f6a7-...",
    "email": "admin@sifpi.go.id",
    "nama": "Admin SIFPI",
    "phone": "021-5000000",
    "role": "ADMIN",
    "created_at": "2024-12-01T00:00:00",
    "last_login": "2026-03-08T09:00:00",
    "email_verified": true
  }
}
```

**404 Not Found** — returned when no user with the given email exists.

```json
{
  "status": 404,
  "message": "User dengan ID 'unknown@example.com' tidak ditemukan.",
  "timestamp": "..."
}
```

---

## Field Reference

### Base fields (all roles)

| Field            | Type       | Description                                                   |
| ---------------- | ---------- | ------------------------------------------------------------- |
| `id`             | `UUID`     | User's unique identifier                                      |
| `email`          | `String`   | Email address                                                 |
| `nama`           | `String`   | Full name (`User.name`)                                       |
| `phone`          | `String`   | Phone number                                                  |
| `role`           | `String`   | Role name (e.g. `INVESTOR`, `PROJECT_OWNER`, `EXECUTIVE`)    |
| `organisasi`     | `String`   | Organisation (`User.organization`)                            |
| `jabatan`        | `String`   | Position/job title (`User.jabatan`)                           |

### Audit trail (all roles)

| Field            | Type            | Description                                                                 |
| ---------------- | --------------- | --------------------------------------------------------------------------- |
| `created_at`     | `LocalDateTime` | Account creation timestamp                                                  |
| `last_login`     | `LocalDateTime` | Most recent `UserToken.createdAt` — absent if user has never received a token |
| `email_verified` | `Boolean`       | Whether the email was verified                                              |
| `phone_verified` | `Boolean`       | Always omitted — field not yet tracked in the `User` model                  |

### PROJECT_OWNER specific

| Field           | Type      | Description                                                          |
| --------------- | --------- | -------------------------------------------------------------------- |
| `jumlah_proyek` | `Integer` | Total projects owned — counted via `ProjectRepository.countByOwnerId` |
| `inquiry_masuk` | `Integer` | Incoming inquiries — always `0` (placeholder; Inquiry feature not yet implemented) |

### INVESTOR specific

| Field             | Type           | Description                                                                            |
| ----------------- | -------------- | -------------------------------------------------------------------------------------- |
| `company_info`    | `Object`       | Nested object with `name`, `sector`, `industry_type`                                   |
| `company_info.name` | `String`     | Investor's company name — mapped from `User.organization`                             |
| `company_info.sector` | `String`  | First entry in `InvestorProfile.sectorInterest` (comma-separated string, parsed)      |
| `company_info.industry_type` | `String` | Always `null` — field not yet tracked in the model                         |
| `sector_interest` | `List<String>` | All sectors parsed from `InvestorProfile.sectorInterest` (split by `,`, trimmed)      |
| `project_interest`| —              | Always omitted — no `ProjectInterest` model yet                                        |
| `budget_range`    | `String`       | Mapped from `InvestorProfile.budgetRange`                                              |

---

## Implementation Notes

- **Single flat DTO** (`UserDetailDTO`) with `@JsonInclude(NON_NULL)` — role-specific null fields are omitted from the serialized response rather than returning explicit nulls.
- **`last_login`:** derived from `UserTokenRepository.findTopByUserIdOrderByCreatedAtDesc`. This returns the creation time of the user's most recently issued token (verification or password setup), not a dedicated login timestamp field.
- **`phone_verified`:** field does not exist on the `User` model — always omitted.
- **`inquiry_masuk`:** hardcoded `0`; will become a real count once the Inquiry feature is implemented.
- **`company_info.sector`:** only the first comma-separated value from `InvestorProfile.sectorInterest` is used. The full list is available in `sector_interest`.
- **`{email:.+}` pattern:** required in Spring MVC to prevent the path variable from dropping the file extension portion of an email (e.g. `.com`).
- **Error handling:** `NotFoundException` is thrown and propagated to `GlobalExceptionHandler` — no manual catch.
- **Transaction:** `@Transactional(readOnly = true)` on the service method.

---

## Files

| File | Role |
| ---- | ---- |
| `usermanagement/controller/AdminUserController.java` | REST controller — adds `GET /api/admin/users/{email:.+}` |
| `usermanagement/service/AdminUserDetailService.java` | Service interface |
| `usermanagement/service/impl/AdminUserDetailServiceImpl.java` | Service implementation — role-based enrichment logic |
| `usermanagement/dto/UserDetailDTO.java` | Response DTO with nested `CompanyInfoDTO` |
| `usermanagement/mapper/UserDetailMapper.java` | MapStruct base mapper (`User` → `UserDetailDTO`) |
| `auth/repository/UserTokenRepository.java` | Added `findTopByUserIdOrderByCreatedAtDesc` for `last_login` |
| `project/repository/ProjectRepository.java` | Added `countByOwnerId` for `jumlah_proyek` |
| `auth/repository/InvestorProfileRepository.java` | `findByUserId` — fetches investor profile |
