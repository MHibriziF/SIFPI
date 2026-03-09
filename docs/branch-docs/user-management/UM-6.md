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
