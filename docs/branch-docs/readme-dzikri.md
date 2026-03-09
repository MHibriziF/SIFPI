# User Management Documentation

---

# UM-6 — Get All Users (Admin)

## Endpoint

```
GET /api/admin/users
```

**Authorization:** Requires role `ADMIN` (`@PreAuthorize("hasRole('ADMIN')")`).

---

## Query Parameters

| Parameter       | Type      | Required | Default | Description                                                          |
| --------------- | --------- | -------- | ------- | -------------------------------------------------------------------- |
| `page`          | `int`     | No       | `0`     | Page index (0-based)                                                 |
| `size`          | `int`     | No       | `4`     | Number of records per page                                           |
| `role`          | `String`  | No       | —       | Filter by exact role name (e.g. `INVESTOR`)                          |
| `isVerified`    | `Boolean` | No       | —       | Filter by verification status                                        |
| `isActive`      | `Boolean` | No       | —       | Filter by active status                                              |
| `organisasi`    | `String`  | No       | —       | Filter by exact organisation name (case-insensitive)                 |
| `search`        | `String`  | No       | —       | Case-insensitive search on `name`, `email`, `organisasi`             |
| `sortBy`        | `String`  | No       | —       | Sort field: only `status` is supported (sorts by `isActive`)         |
| `sortDirection` | `String`  | No       | `asc`   | Sort direction when `sortBy=status`: `asc` or `desc`                 |

When `sortBy` is absent or any value other than `status`, results default to `ORDER BY createdAt DESC`.

---

## Response

**200 OK**

```json
{
  "status": 200,
  "message": "Berhasil.",
  "timestamp": "...",
  "data": {
    "content": [
      {
        "email": "user@example.com",
        "nama": "John Doe",
        "organisasi": "PT Example",
        "phone": "081234567890",
        "role": "INVESTOR",
        "is_verified": true,
        "is_active": true,
        "created_at": "2026-01-01T00:00:00"
      }
    ],
    "page": 0,
    "size": 4,
    "totalElements": 42,
    "totalPages": 11,
    "first": true,
    "last": false
  }
}
```

---

## Implementation Notes

- **Sorting:** only `status` (`isActive`) is supported. Default order is `createdAt DESC`. Role sorting is not implemented.
- **Search:** JPQL `LIKE LOWER(CONCAT('%', :search, '%'))` across `name`, `email`, `organisasi`. Null/blank `search` matches all records.
- **`organisasi` filter:** lowercased in Java (`organisasi.toLowerCase()`) before being passed to the query to avoid PostgreSQL `lower(bytea)` type errors on null parameters.
- **Soft-delete:** `deletedAt IS NOT NULL` users are always excluded.
- **Count query:** all three repository methods carry an explicit `countQuery` — required for Spring Data JPA to correctly populate `totalElements` / `totalPages` on custom `@Query` methods.
- **Mapper:** `UserMapper.toUserDTO` (MapStruct); `role` resolved via `@Named("roleToString")` default method.
- **Transaction:** `@Transactional(readOnly = true)` on the service method (not class-level).

---

## Files

| File | Role |
| ---- | ---- |
| `user/controller/UserController.java` | REST controller |
| `user/service/UserService.java` | Service interface |
| `user/service/impl/UserServiceImpl.java` | Service implementation |
| `user/dto/UserDTO.java` | Response DTO |
| `user/mapper/UserMapper.java` | MapStruct mapper |
| `auth/repository/UserRepository.java` | JPQL filter queries (`findAllByFilters`, `OrderByStatusAsc/Desc`) |

---

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

---

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

---

# UM-14 — Read Role Detail (Admin)

## Endpoint

```
GET /api/roles/{id}
```

**Authorization:** Requires role `ADMIN` (`@PreAuthorize("hasRole('ADMIN')")`).

---

## Path Parameters

| Parameter | Type   | Required | Description          |
| --------- | ------ | -------- | -------------------- |
| `id`      | `UUID` | Yes      | ID of the role to retrieve |

---

## Response

**200 OK**

```json
{
  "status": 200,
  "message": "Detail role berhasil diambil.",
  "timestamp": "...",
  "data": {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "name": "INVESTOR",
    "description": "Role untuk investor",
    "status": true,
    "permissions": {
      "PROJECT": ["READ"],
      "INQUIRY": ["CREATE", "READ"]
    },
    "createdAt": "2026-01-01T00:00:00",
    "updatedAt": "2026-02-01T00:00:00"
  }
}
```

**404 Not Found** — returned when the given `id` does not exist in the database.

```json
{
  "status": 404,
  "message": "Role dengan ID '3fa85f64-5717-4562-b3fc-2c963f66afa6' tidak ditemukan.",
  "timestamp": "..."
}
```

---

## Implementation Notes

- **Authorization:** `hasRole('ADMIN')` — role management is inherently admin-specific and should never be delegated to other roles.
- **Permissions:** retrieved via `RolePermissionRepository.findByRoleWithResource(role)`, which uses `JOIN FETCH rp.resource` to eagerly load the `Resource` association within the transaction, avoiding `LazyInitializationException`.
- **`permissions` field:** serialized as a `Map<String, List<String>>` (resource name → list of action names) via `PermissionsDTO extends LinkedHashMap`, mapped by `PermissionsMapper.toPermissionsDTO`.
- **`createdAt` / `updatedAt`:** populated automatically by Spring Data JPA Auditing (`@CreatedDate` / `@LastModifiedDate` on the `Role` entity, `@EnableJpaAuditing` on `SifpiApplication`, `@EntityListeners(AuditingEntityListener.class)` on the entity).
- **Transaction:** `@Transactional(readOnly = true)` on the service method; lazy relationships are accessed safely within the open session.
- **Error handling:** `NotFoundException` is thrown and propagated to `GlobalExceptionHandler` — no manual catch block.
- **Mapping:** `RoleMapper.toDTO(role, permissions)` (MapStruct); `createdAt`/`updatedAt` auto-mapped by field name.

---

## Files

| File | Role |
| ---- | ---- |
| `auth/controller/RoleController.java` | REST controller — adds `GET /api/roles/{id}` endpoint |
| `auth/service/RoleService.java` | Service interface — declares `getRoleById(UUID id)` |
| `auth/service/impl/RoleServiceImpl.java` | Service implementation — fetches role + permissions, maps to DTO |
| `auth/dto/RoleResponseDTO.java` | Response DTO — includes `createdAt` and `updatedAt` fields |
| `auth/mapper/RoleMapper.java` | MapStruct mapper — `toDTO(Role, List<RolePermission>)` |
| `auth/mapper/PermissionsMapper.java` | Converts `List<RolePermission>` → `PermissionsDTO` (grouped by resource) |
| `auth/repository/RolePermissionRepository.java` | `findByRoleWithResource` — JOIN FETCH to avoid lazy load issues |
| `auth/model/Role.java` | Entity — added `createdAt`, `updatedAt` audit fields |
| `SifpiApplication.java` | Added `@EnableJpaAuditing` |

---

# UM-15 — Update Role (Admin)

## Endpoint

```
PUT /api/roles/{id}
```

**Authorization:** Requires role `ADMIN` (`@PreAuthorize("hasRole('ADMIN')")`).

---

## Path Parameters

| Parameter | Type   | Required | Description            |
| --------- | ------ | -------- | ---------------------- |
| `id`      | `UUID` | Yes      | ID of the role to update |

---

## Request Body

All fields are optional. Only fields present in the request body are applied — omitted fields leave the existing values unchanged.

```json
{
  "name": "NEW_ROLE_NAME",
  "description": "Deskripsi role baru",
  "status": true,
  "permissions": [
    {
      "resource": "PROJECT",
      "actions": ["READ", "CREATE"]
    }
  ]
}
```

| Field         | Type                  | Required | Constraints          | Description |
| ------------- | --------------------- | -------- | -------------------- | ----------- |
| `name`        | `String`              | No       | max 50 chars         | New role name. Must be unique across all roles. |
| `description` | `String`              | No       | max 200 chars        | New description. |
| `status`      | `Boolean`             | No       | —                    | `true` = active, `false` = inactive. |
| `permissions` | `List<PermissionRequest>` | No   | —                    | Full replacement of permissions. Pass `[]` to clear all. Omit to leave unchanged. |

### `PermissionRequest`

| Field      | Type           | Required | Description |
| ---------- | -------------- | -------- | ----------- |
| `resource` | `String`       | Yes      | Resource name (e.g. `PROJECT`, `USER`). Must exist in the `resources` table. |
| `actions`  | `List<Action>` | Yes      | One or more actions: `CREATE`, `READ`, `UPDATE`, `DELETE`. |

---

## Response

**200 OK**

```json
{
  "status": 200,
  "message": "Role berhasil diperbarui.",
  "timestamp": "...",
  "data": {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "name": "NEW_ROLE_NAME",
    "description": "Deskripsi role baru",
    "status": true,
    "permissions": {
      "PROJECT": ["READ", "CREATE"]
    },
    "createdAt": "2026-01-01T00:00:00",
    "updatedAt": "2026-03-04T10:00:00"
  }
}
```

**404 Not Found** — role with the given `id` does not exist.

```json
{
  "status": 404,
  "message": "Role dengan ID '3fa85f64-5717-4562-b3fc-2c963f66afa6' tidak ditemukan.",
  "timestamp": "..."
}
```

**409 / 500** — another role already has the requested `name`.

```json
{
  "status": 500,
  "message": "Role dengan nama 'NEW_ROLE_NAME' sudah ada.",
  "timestamp": "..."
}
```

**400 Bad Request** — duplicate `resource` + `action` combination within the same request.

```json
{
  "status": 400,
  "message": "Izin duplikat dalam permintaan: resource 'PROJECT' dengan aksi 'READ'.",
  "timestamp": "..."
}
```

---

## Permissions Behaviour

| Request body                            | Effect on permissions         |
| --------------------------------------- | ----------------------------- |
| `"permissions"` field omitted           | Permissions are **unchanged** |
| `"permissions": []`                     | All permissions are **removed** |
| `"permissions": [{ ... }]`             | Existing permissions are **fully replaced** by the new list |

---

## Audit Trail

Every successful update persists a record to the `role_audit_logs` table containing:

| Column       | Value |
| ------------ | ----- |
| `role_id`    | FK to the updated role |
| `action`     | `"UPDATE"` |
| `changed_by` | Email / subject from the JWT (resolved via `SecurityContextHolder`) |
| `details`    | `BEFORE: { name=..., description=..., status=..., permissions=[...] } \| AFTER: { ... }` |
| `timestamp`  | Set automatically via `@CreationTimestamp` |

---

## Implementation Notes

- **Partial update:** `UpdateRoleRequest` uses all-nullable fields. `RoleMapper.updateEntity` is annotated with `@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)` so only non-null fields are written to the entity.
- **Permissions replacement:** when `permissions` is provided, the existing `RolePermission` rows for the role are deleted via `RolePermissionRepository.deleteByRole(role)` and rebuilt using the same `buildPermissions` helper as `createRole`.
- **Name uniqueness check:** performed before patching the entity. Throws `IllegalStateException` if another role already holds the requested name.
- **Duplicate permission guard:** `buildPermissions` tracks seen `resource:action` keys in a `HashSet` and throws `IllegalArgumentException` on collision.
- **Audit actor:** resolved from `SecurityContextHolder.getContext().getAuthentication().getName()` (JWT subject, which is the user's email).
- **Transaction:** `@Transactional` on the service method — all writes (role update, permission delete/insert, audit log insert) succeed or roll back together.
- **Error handling:** `NotFoundException`, `IllegalStateException`, `IllegalArgumentException` all propagate to `GlobalExceptionHandler` — no manual catch blocks.
- **Authorization:** `hasRole('ADMIN')` — role management is inherently admin-specific.

---

## Files

| File | Role |
| ---- | ---- |
| `auth/controller/RoleController.java` | Adds `PUT /api/roles/{id}` endpoint |
| `auth/service/RoleService.java` | Declares `updateRole(UUID, UpdateRoleRequest)` |
| `auth/service/impl/RoleServiceImpl.java` | Implements update logic, permission replacement, and audit logging |
| `auth/dto/request/UpdateRoleRequest.java` | Request DTO — all fields optional/nullable |
| `auth/dto/RoleResponseDTO.java` | Response DTO |
| `auth/mapper/RoleMapper.java` | Adds `updateEntity(UpdateRoleRequest, @MappingTarget Role)` with null-ignore strategy |
| `auth/model/RoleAuditLog.java` | Audit log entity — FK to `Role`, records every update |
| `auth/repository/RoleAuditLogRepository.java` | Persists `RoleAuditLog`; provides `findByRoleOrderByTimestampDesc` for future history endpoint |
| `auth/repository/RolePermissionRepository.java` | Adds `deleteByRole(Role)` for permission replacement |

---

# Get All Roles (Admin)

## Endpoint

```
GET /api/roles
```

**Authorization:** Requires role `ADMIN` (`@PreAuthorize("hasRole('ADMIN')")`).

---

## Response

**200 OK**

```json
{
  "status": 200,
  "message": "Daftar role berhasil diambil.",
  "timestamp": "...",
  "data": [
    {
      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "name": "INVESTOR",
      "description": "Role untuk investor",
      "userCount": 42
    },
    {
      "id": "4gb96g75-6828-5673-c4gd-3d074g77bgb7",
      "name": "ADMIN",
      "description": "Role untuk admin",
      "userCount": 3
    }
  ]
}
```

---

## Field Reference

| Field       | Type     | Description                                          |
| ----------- | -------- | ---------------------------------------------------- |
| `id`        | `UUID`   | Role's unique identifier                             |
| `name`      | `String` | Role name (e.g. `ADMIN`, `INVESTOR`)                 |
| `description` | `String` | Role description                                   |
| `userCount` | `long`   | Number of users currently assigned to this role      |

---

## Implementation Notes

- **No pagination** — the number of roles in this system is small and bounded; returning the full list is appropriate.
- **`userCount`:** derived from `UserRepository.countByRole(role)` — Spring Data derives the COUNT query automatically from the method name.
- **Mapping:** `RoleMapper.toSummaryDTO(role)` (MapStruct) handles the entity → `RoleSummaryDTO` conversion; `userCount` is set manually after mapping since it comes from a separate repository call, not from the `Role` entity directly.
- **Transaction:** `@Transactional(readOnly = true)` on the service method.
- **Authorization:** `hasRole('ADMIN')` — role listing is an admin-only operation.
- **Error handling:** no domain errors possible; relies on `GlobalExceptionHandler` for infrastructure-level failures.

---

## Files

| File | Role |
| ---- | ---- |
| `auth/controller/RoleController.java` | Adds `GET /api/roles` endpoint |
| `auth/service/RoleService.java` | Declares `List<RoleSummaryDTO> getAllRoles()` |
| `auth/service/impl/RoleServiceImpl.java` | Implements `getAllRoles()` — fetches all roles, counts users per role |
| `auth/dto/RoleSummaryDTO.java` | New response DTO — `id`, `name`, `description`, `userCount` |
| `auth/mapper/RoleMapper.java` | Adds `toSummaryDTO(Role)` |
| `auth/repository/UserRepository.java` | Adds `long countByRole(Role role)` |

---

# Get Users By Role (Admin)

## Endpoint

```
GET /api/roles/{id}/users
```

**Authorization:** Requires role `ADMIN` (`@PreAuthorize("hasRole('ADMIN')")`).

---

## Path Parameters

| Parameter | Type   | Required | Description               |
| --------- | ------ | -------- | ------------------------- |
| `id`      | `UUID` | Yes      | ID of the role to list users for |

---

## Query Parameters

| Parameter | Type     | Required | Default    | Description                                             |
| --------- | -------- | -------- | ---------- | ------------------------------------------------------- |
| `search`  | `String` | No       | `""`       | Case-insensitive search on `name` and `email`           |
| `page`    | `int`    | No       | `0`        | Page index (0-based)                                    |
| `size`    | `int`    | No       | `20`       | Number of records per page                              |
| `sort`    | `String` | No       | `name,asc` | Sort field and direction                                |

---

## Response

**200 OK — non-INVESTOR role (e.g. ADMIN, EXECUTIVE, PROJECT_OWNER)**

```json
{
  "status": 200,
  "message": "Daftar pengguna berhasil diambil.",
  "timestamp": "...",
  "data": {
    "content": [
      {
        "nama": "Budi Santoso",
        "email": "budi@example.com"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 5,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

**200 OK — INVESTOR role**

```json
{
  "status": 200,
  "message": "Daftar pengguna berhasil diambil.",
  "timestamp": "...",
  "data": {
    "content": [
      {
        "nama": "Siti Rahayu",
        "email": "siti@investor.com",
        "organisasi": "PT Investasi Nusantara",
        "sectorInterest": ["Energi", "Infrastruktur", "Pariwisata"],
        "budgetRange": "10M - 50M USD"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 42,
    "totalPages": 3,
    "first": true,
    "last": false
  }
}
```

**404 Not Found** — no role with the given `id` exists.

```json
{
  "status": 404,
  "message": "Role dengan ID '...' tidak ditemukan.",
  "timestamp": "..."
}
```

---

## Field Reference

### Base fields (all roles)

| Field   | Type     | Description                   |
| ------- | -------- | ----------------------------- |
| `nama`  | `String` | User's full name (`User.name`) |
| `email` | `String` | Email address                 |

### INVESTOR-only fields (omitted via `@JsonInclude(NON_NULL)` for all other roles)

| Field            | Type           | Description                                                                         |
| ---------------- | -------------- | ----------------------------------------------------------------------------------- |
| `organisasi`     | `String`       | Company name — mapped from `User.organization`                                      |
| `sectorInterest` | `List<String>` | Parsed from `InvestorProfile.sectorInterest` (comma-separated string, split + trim) |
| `budgetRange`    | `String`       | Mapped from `InvestorProfile.budgetRange`                                           |

---

## Implementation Notes

- **Role existence check:** `RoleRepository.findById(roleId)` is called first; throws `NotFoundException` (→ 404) if the role does not exist.
- **Search:** `findByRoleIdWithSearch` — JPQL `LIKE LOWER(CONCAT('%', :search, '%'))` on both `name` and `email`. Empty string matches all records (no filtering).
- **Investor enrichment:** the service checks `role.getName().equals("INVESTOR")` before fetching `InvestorProfile`. For all other roles, `organisasi`, `sectorInterest`, and `budgetRange` remain `null` and are omitted from the JSON response by `@JsonInclude(NON_NULL)` on `RoleUserDTO`.
- **`sectorInterest` parsing:** `InvestorProfile.sectorInterest` is stored as a comma-separated string (e.g. `"Energi,Infrastruktur,Pariwisata"`). The service splits it, trims whitespace, and filters out blank entries before setting `List<String>` on the DTO.
- **`organisasi`:** set directly from `User.organization` in the service — not from `InvestorProfile` — to mirror the `company_info.name` convention used in UM-7.
- **Mapping:** `RoleMapper.toRoleUserDTO(User)` (MapStruct) handles `name → nama` and `email`; investor-only fields are `ignore`d in the mapper and set manually in the service.
- **Pagination defaults:** `size = 20`, sorted by `name ASC` (`@PageableDefault`). Client can override all parameters via standard Spring MVC query parameters.
- **Transaction:** `@Transactional(readOnly = true)` on the service method. Lazy relationships are not accessed — only scalar `User` fields and a separate `InvestorProfile` lookup are used.
- **Error handling:** `NotFoundException` propagates to `GlobalExceptionHandler` — no manual catch.
- **Authorization:** `hasRole('ADMIN')` — role-scoped user listing is an admin-only operation.
- **Count query:** `findByRoleIdWithSearch` carries an explicit `countQuery` for correct `totalElements`/`totalPages` pagination metadata.

---

## Files

| File | Role |
| ---- | ---- |
| `auth/controller/RoleController.java` | Adds `GET /api/roles/{id}/users` endpoint |
| `auth/service/RoleService.java` | Declares `Page<RoleUserDTO> getUsersByRole(UUID, String, Pageable)` |
| `auth/service/impl/RoleServiceImpl.java` | Implements `getUsersByRole` — role existence check, search, investor enrichment |
| `auth/dto/RoleUserDTO.java` | New response DTO — `nama`, `email`; investor-only: `organisasi`, `sectorInterest`, `budgetRange` with `@JsonInclude(NON_NULL)` |
| `auth/mapper/RoleMapper.java` | Adds `toRoleUserDTO(User)` mapping `name → nama`; investor fields ignored |
| `auth/repository/UserRepository.java` | Adds `findByRoleIdWithSearch(UUID, String, Pageable)` with explicit count query |
| `auth/repository/InvestorProfileRepository.java` | Reuses `findByUserId` for investor profile lookup |

---

# PM-4 — Get Project Detail & Status History (Project Owner)

## Endpoints

```
GET /api/projects/{id}
GET /api/projects/{id}/history
```

**Authorization:** Requires authority `PROJECT:READ` (`@PreAuthorize("hasAuthority('PROJECT:READ')")`).

---

## Path Parameters

| Parameter | Type   | Required | Description                |
| --------- | ------ | -------- | -------------------------- |
| `id`      | `Long` | Yes      | Primary key of the project |

---

## GET /api/projects/{id} — Project Detail

Returns the full detail of a single project. Only accessible by the project owner (RLS enforced via `ownerId`).

### Response — 200 OK

```json
{
  "status": 200,
  "message": "Detail proyek berhasil diambil.",
  "timestamp": "...",
  "data": {
    "id": 1,
    "ownerId": "a1b2c3d4-...",
    "name": "Proyek Infrastruktur X",
    "description": "Deskripsi singkat proyek",
    "sector": "ENERGI",
    "status": "DRAFT",
    "location": "Jakarta",
    "valueProposition": "...",
    "locationImageUrl": "https://storage.example.com/projects/uuid.png",
    "ownerInstitution": "PT Contoh",
    "contactPersonName": "Budi",
    "contactPersonEmail": "budi@example.com",
    "contactPersonPhone": "081234567890",
    "cooperationModel": "KPBU",
    "concessionPeriod": 25,
    "assetReadiness": "Siap",
    "projectStructureImageUrl": "https://storage.example.com/projects/uuid2.png",
    "governmentSupport": "Dukungan pemerintah...",
    "totalCapex": 100000000,
    "totalOpex": 5000000,
    "npv": 30000000,
    "irr": 0.15,
    "revenueStream": "Tarif pengguna",
    "projectFileDownloadUrl": "https://storage.example.com/projects/uuid3.pdf",
    "isFeasibilityStudy": true,
    "additionalInfo": null,
    "timelines": [
      { "id": 1, "timeRange": "2026 Q1", "phaseDescription": "Persiapan lahan" }
    ],
    "isSubmitted": false,
    "rejectionReason": null,
    "createdAt": "2026-03-01T08:00:00",
    "editedAt": "2026-03-08T10:00:00"
  }
}
```

> **`rejectionReason`** is always `null` for now — will be populated once the status-history feature (owned by a separate team member) is complete.

### Error Responses

| Status | Condition |
| ------ | --------- |
| `403 Forbidden` | The authenticated user is not the project owner |
| `404 Not Found` | No project exists with the given `id` |

---

## GET /api/projects/{id}/history — Status Transition History

Returns the ordered list of status transitions for the project. The same ownership RLS check applies.

### Response — 200 OK

```json
{
  "status": 200,
  "message": "Riwayat status proyek berhasil diambil.",
  "timestamp": "...",
  "data": null
}
```

> **`data` is `null`** — the status-history feature is pending implementation by another team member. The 404/403 ownership checks are fully active; only the history data itself is a stub.

### History Entry Shape (future)

Once implemented, each entry in `data` will have:

| Field       | Type            | Description                                    |
| ----------- | --------------- | ---------------------------------------------- |
| `status`    | `String`        | Project status at this point (e.g. `DIAJUKAN`) |
| `changedAt` | `LocalDateTime` | When the status transition occurred            |
| `changedBy` | `String`        | Display name of the user who made the change   |
| `notes`     | `String`        | Optional notes attached to the transition      |

### Error Responses

| Status | Condition |
| ------ | --------- |
| `403 Forbidden` | The authenticated user is not the project owner |
| `404 Not Found` | No project exists with the given `id` |

---

## Implementation Notes

- **RLS:** `ReadProjectDetailServiceImpl.enforceOwnership()` compares `project.ownerId` against the authenticated user's UUID. A mismatch throws `ForbiddenException` → 403. The check runs on both endpoints before any data is returned.
- **Identity resolution:** same pattern as PM-3 — `SecurityContextHolder` → email → `UserRepository.findByEmail` → UUID.
- **File URLs:** resolved via the existing `ProjectMapper.toDTO(project, storageService)` overload, which calls `storageService.getPublicUrl()` for `locationImageKey`, `projectStructureImageKey`, and `projectFileKey`.
- **`rejectionReason`:** field added to `ProjectResponseDTO`; hardcoded `null` with a `TODO` comment pending the history feature.
- **`createdAt` / `editedAt`:** added to `ProjectResponseDTO`; auto-mapped by MapStruct (same field names on the entity).
- **History stub:** `getProjectHistory` validates ownership (404 / 403), logs the access, then returns `null`. No `ProjectStatusHistory` entity exists yet.
- **Transaction:** `@Transactional(readOnly = true)` on both service methods. The lazy `@OneToMany` timelines collection is accessed safely within the open JPA session before mapping to DTO.
- **Error handling:** `NotFoundException` and `ForbiddenException` propagate to `GlobalExceptionHandler` — no manual catch blocks.

---

## Files

| File | Role |
| ---- | ---- |
| `project/controller/ProjectController.java` | Adds `GET /api/projects/{id}` and `GET /api/projects/{id}/history` endpoints |
| `project/service/ReadProjectDetailService.java` | Service interface — declares `getProjectDetail` and `getProjectHistory` |
| `project/service/impl/ReadProjectDetailServiceImpl.java` | Service implementation — RLS enforcement, fetch, mapping |
| `project/dto/ProjectResponseDTO.java` | Added `rejectionReason`, `createdAt`, `editedAt` fields |
| `project/dto/ProjectStatusHistoryDTO.java` | Response DTO for a single history entry (`status`, `changedAt`, `changedBy`, `notes`) |
| `project/mapper/ProjectMapper.java` | Reused — `toDTO(project, storageService)` resolves file storage keys to public URLs |
