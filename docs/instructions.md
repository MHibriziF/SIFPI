# Development Instructions

Guidelines to follow when working on this project.

> **Note:** All code examples in this document use `Project`, `User`, etc. as illustrative placeholders. They are not the actual domain models of this application — refer to the feature packages for real implementations.

## Before You Start

Always pull the latest changes from `staging` before starting new work. Other features may already be merged there and your branch could be missing them, leading to larger merge conflicts later.

Prefer `--rebase` to keep a clean linear history:

```bash
git pull origin staging --rebase
```

If the rebase runs into many conflicts (resolving them commit-by-commit gets messy), abort it and fall back to a regular merge instead — it lets you resolve everything in one go:

```bash
git rebase --abort
git pull origin staging
```

Make it a habit to do this at the start of each working session.

## Project Structure

Follow the existing feature-based package layout:

```
sifpi/
├── common/          # shared across all features
│   ├── config/
│   ├── constants/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── security/
│   ├── services/
│   └── utils/
└── <feature>/       # one package per feature
    ├── controller/
    ├── dto/
    ├── mapper/
    ├── model/
    ├── repository/
    └── service/
```

## Dependency Injection

Use **constructor injection**. Do not use field injection (`@Autowired` on fields).

Preferred — use Lombok:

```java
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final StorageService storageService;
}
```

Also fine — manual constructor:

```java
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
}
```

Do **not** do this:

```java
@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository; // field injection, avoid this
}
```

## Constants

Never hardcode magic strings or numbers. Use `static final` constants.

If a constant is **only used in one class**, keep it in that class:

```java
@Service
public class DocumentService {
    private static final long MAX_DOCUMENT_SIZE = 5 * 1024 * 1024;
    private static final String UPLOAD_FOLDER = "documents";
    // ...
}
```

If a constant is **shared across multiple features/services**, put it in `AppConstant`:

```java
// common/constants/AppConstant.java
public static final String CACHE_PROJECTS = "projects";
```

## Credentials & Environment Variables

Never hardcode secrets, URLs, or credentials. Use environment variables bound through `application.yaml`.

Reference `.env.example` for all available env vars. When adding a new one:

1. Add it to `.env.example` with a placeholder value and a comment
2. Add the binding in `application.yaml` with `${VAR_NAME}` (use `${VAR_NAME:default}` if a default makes sense)
3. Add it to the `sifpi-app` environment block in `docker-compose.yaml`

## Caching

Use `@Cacheable`, `@CacheEvict`, etc. when appropriate. Pre-configured caches are defined in `CacheConfig`:

| Cache Name       | Constant                           | Max Size | TTL    |
| ---------------- | ---------------------------------- | -------- | ------ |
| `projects`       | `AppConstant.CACHE_PROJECTS`       | 500      | 7 days |
| `news`           | `AppConstant.CACHE_NEWS`           | 200      | 1 hour |
| `project-detail` | `AppConstant.CACHE_PROJECT_DETAIL` | 1000     | 1 day  |

Always reference cache names through `AppConstant`:

```java
@Cacheable(AppConstant.CACHE_PROJECT_DETAIL)
public ProjectDTO getProject(UUID id) { ... }
```

If you need a new cache with custom sizing/TTL, add it in `CacheConfig`. Otherwise, any unlisted cache name falls back to the default (100 entries, 10 min TTL).

### Eviction strategy

Caches go stale on writes. Evict the relevant caches on every create, update, and delete operation.

**Evict a single entry** (by key) when updating or deleting a specific item:

```java
@CacheEvict(value = AppConstant.CACHE_PROJECT_DETAIL, key = "#id")
public void deleteProject(UUID id) { ... }
```

**Evict all entries** in a collection cache on any write — the whole list is now stale:

```java
@CacheEvict(value = AppConstant.CACHE_PROJECTS, allEntries = true)
public ProjectDTO createProject(CreateProjectRequest request) { ... }
```

**Combine multiple evictions** with `@Caching` when a write affects both a detail cache and a collection cache:

```java
@Caching(evict = {
    @CacheEvict(value = AppConstant.CACHE_PROJECT_DETAIL, key = "#id"),
    @CacheEvict(value = AppConstant.CACHE_PROJECTS, allEntries = true)
})
public ProjectDTO updateProject(UUID id, UpdateProjectRequest request) { ... }
```

As a general rule:

- **Create** → evict collection caches (`allEntries = true`)
- **Update** → evict the specific detail entry + evict collection caches
- **Delete** → evict the specific detail entry + evict collection caches

## Request & Response DTOs

Never expose entity classes directly in controllers. Use DTOs for both incoming requests and outgoing responses, kept in the feature's `dto/` package.

- **Request DTO** — represents the incoming request body. Name it `Create<Feature>Request`, `Update<Feature>Request`, etc. Put validation annotations here, not in the entity.
- **Response DTO** — represents what gets returned to the client. Name it `<Feature>DTO`. Only include fields the client needs — don't leak internal or sensitive fields.

```java
// dto/CreateProjectRequest.java
@Data
public class CreateProjectRequest {
    @NotBlank
    private String name;

    @NotNull
    private BigDecimal targetFunding;
}

// dto/ProjectDTO.java
@Data
public class ProjectDTO {
    private UUID id;
    private String name;
    private BigDecimal targetFunding;
    private String thumbnailUrl;
}
```

If a DTO is shared across multiple features (e.g. a common paginated response wrapper), put it in `common/dto/`.

## Exception Handling

Let `GlobalExceptionHandler` handle exceptions — don't catch them just to return an error response manually.

**Don't do this:**

```java
public ProjectDTO getProject(UUID id) {
    try {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project", id));
        return projectMapper.toDTO(project);
    } catch (NotFoundException e) {
        // don't catch here just to handle the response — GlobalExceptionHandler does this
    }
}
```

**Do this — just throw and let it propagate:**

```java
public ProjectDTO getProject(UUID id) {
    Project project = projectRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Project", id));
    return projectMapper.toDTO(project);
}
```

**Only use try-catch when you need to rethrow as a different exception**, e.g. wrapping a low-level checked exception into something meaningful:

```java
public String upload(MultipartFile file) {
    try {
        // ...
    } catch (IOException e) {
        throw new RuntimeException("Gagal mengunggah file: " + e.getMessage(), e);
    }
}
```

If you need a new error type, either reuse `NotFoundException` / `IllegalArgumentException` / `IllegalStateException` (already handled), or add a new custom exception class and register it in `GlobalExceptionHandler`.

## Response Format

All API responses use `BaseResponseDTO<T>` via `ResponseUtil`:

```java
return responseUtil.success(data, "Berhasil.", HttpStatus.OK);
return responseUtil.successPaged(result, "Berhasil.", HttpStatus.OK); // If data contains paginations
return responseUtil.error("Gagal.", HttpStatus.BAD_REQUEST);
```

Don't construct `ResponseEntity` manually with raw bodies — always wrap with `ResponseUtil`.

## Mapping (MapStruct)

Use **MapStruct** for all entity ↔ DTO conversions. Do not manually set fields in the service layer.

Define a mapper interface per feature in the `mapper/` package:

```java
@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDTO toDTO(Project project);

    // create — returns a new entity
    Project toEntity(CreateProjectRequest request);

    // update — patches an existing entity; fields absent in the request are left unchanged
    void updateEntity(UpdateProjectRequest request, @MappingTarget Project project);
}
```

For updates, load the entity first, then call `updateEntity` to patch it in place — fields not present in `UpdateProjectRequest` (e.g. `id`, `createdAt`) are naturally preserved:

```java
// create
public ProjectDTO createProject(CreateProjectRequest request) {
    return projectMapper.toDTO(projectRepository.save(projectMapper.toEntity(request)));
}

// update
public ProjectDTO updateProject(UUID id, UpdateProjectRequest request) {
    Project project = projectRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Project", id));
    projectMapper.updateEntity(request, project);
    return projectMapper.toDTO(projectRepository.save(project));
}
```

Inject and use it in the service:

```java
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectDTO getProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project", id));
        return projectMapper.toDTO(project);
    }
}
```

When a field requires custom logic that MapStruct can't handle automatically (e.g. resolving a storage key to a public URL), use `@AfterMapping`:

```java
@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDTO toDTO(Project project);

    @AfterMapping
    default void setThumbnailUrl(Project project, @MappingTarget ProjectDTO dto,
            @Context StorageService storageService) {
        if (project.getThumbnailKey() != null) {
            dto.setThumbnailUrl(storageService.getPublicUrl(project.getThumbnailKey()));
        }
    }
}
```

## Entity Relationships

### General rules

- Always use **`fetch = FetchType.LAZY`** on all relationships. Eager fetching loads data you may not need and causes performance issues.
- The **owning side** (the one with the FK column) carries `@JoinColumn`. The **inverse side** uses `mappedBy`.
- Only make a relationship **bidirectional** if you actually need to navigate from both sides. Unidirectional is simpler.

### `@ManyToOne` / `@OneToMany`

`@ManyToOne` is the owning side — it holds the FK:

```java
// owning side (has the FK column)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "owner_id", nullable = false)
private User owner;
```

`@OneToMany` is the inverse side — use `mappedBy` pointing to the field on the owning side:

```java
// inverse side
@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
private List<Project> projects = new ArrayList<>();
```

Only add `cascade = CascadeType.ALL` and `orphanRemoval = true` when the child's lifecycle is fully owned by the parent (e.g. order items owned by an order). Don't use it by default.

### `@ManyToMany`

Avoid `@ManyToMany`. Always model the join table as an **intermediate entity** instead. You will almost always need extra columns on it eventually (e.g. `createdAt`, `role`, `status`), and refactoring away from `@ManyToMany` later is painful. It also gives you full control over querying and cascade behavior.

```java
@Entity
@Table(name = "project_members")
public class ProjectMember {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // extra columns are easy to add when needed
    private String role;
    private LocalDate joinedAt;
}
```

From `Project`, navigate via the intermediate entity:

```java
@OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
private List<ProjectMember> members = new ArrayList<>();
```

### `@OneToOne`

Use sparingly. Only when there's a strict one-to-one relationship that can't be a column on the same table. The owning side has the FK:

```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "profile_id")
private UserProfile profile;
```

## Database Indexing

Add indexes on columns that will be frequently queried, filtered, or joined on. Define them in `@Table` on the entity.

```java
@Entity
@Table(
    name = "projects",
    indexes = {
        @Index(name = "idx_project_owner_id", columnList = "owner_id"),
        @Index(name = "idx_project_status", columnList = "status"),
        @Index(name = "idx_project_owner_status", columnList = "owner_id, status") // composite
    }
)
public class Project {
    // ...
}
```

When to add an index:

- Foreign key columns (e.g. `owner_id`, `category_id`) — almost always
- Columns used in `WHERE` filters or `ORDER BY` in common queries
- Columns used in `JOIN` conditions

When **not** to:

- Columns that are rarely queried
- Small lookup tables (a few dozen rows) — a full scan is fine
- Every column by default — indexes have a write cost

Use composite indexes when queries consistently filter on multiple columns together. Column order matters: put the more selective column first.

## Logging

Use **SLF4J** via Lombok's `@Slf4j` annotation. This gives you a `log` field without any boilerplate.

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    // log is available automatically
}
```

### Log levels

| Level            | When to use                                                                       |
| ---------------- | --------------------------------------------------------------------------------- |
| `log.debug(...)` | Detailed diagnostic info — disabled in production by default                      |
| `log.info(...)`  | Normal operations: entity created/updated/deleted, significant state changes      |
| `log.warn(...)`  | Something unexpected but recoverable — e.g. a file was missing but had a fallback |
| `log.error(...)` | Unexpected failures that likely indicate a bug or infrastructure problem          |

### Rules

- **Use parameterized logging** — never concatenate strings:

  ```java
  log.info("Project created: {}", project.getId());   // correct
  log.info("Project created: " + project.getId());    // don't do this — allocates even when log level is off
  ```

- **Never log sensitive data** — no passwords, tokens, personal data, or full request bodies containing credentials.

- **Log with context** — include relevant IDs so you can trace an issue:

  ```java
  log.warn("Thumbnail not found for project {}, skipping delete", projectId);
  log.error("Failed to upload file for project {}: {}", projectId, e.getMessage(), e);
  ```

- **Don't log and throw** — pick one. If you throw, the exception handler or the caller can log. Logging then re-throwing creates duplicate log entries:

  ```java
  // don't do this
  } catch (IOException e) {
      log.error("Upload failed", e);
      throw new RuntimeException("Gagal mengunggah file", e);
  }

  // do this — just rethrow; the global handler or calling code can log
  } catch (IOException e) {
      throw new RuntimeException("Gagal mengunggah file: " + e.getMessage(), e);
  }
  ```

## Transactions

Use `@Transactional` at the **service layer** — never on controllers.

### When to use it

Add `@Transactional` on methods that perform **multiple writes** that must succeed or fail together:

```java
@Transactional
public ProjectDTO createProject(CreateProjectRequest request, MultipartFile thumbnail) {
    String fileKey = storageService.upload("projects", thumbnail);

    Project project = projectMapper.toEntity(request);
    project.setThumbnailKey(fileKey);
    projectRepository.save(project);

    // if anything above fails, the whole method rolls back
    return projectMapper.toDTO(project);
}
```

For **read-only** methods, add `readOnly = true` — it's a small performance hint to the JPA provider and connection pool:

```java
@Transactional(readOnly = true)
public ProjectDTO getProject(UUID id) {
    Project project = projectRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Project", id));
    return projectMapper.toDTO(project);
}
```

### When you don't need it

- Simple single-read methods — Spring Data repositories are already transactional internally.
- Methods that only call other `@Transactional` service methods — they'll join the existing transaction.

### Rules

- `@Transactional` only works on **public** methods — Spring AOP proxies can't intercept private or package-private calls.
- Don't annotate the whole class unless every method genuinely needs a transaction. Prefer per-method annotation to be explicit.
- Lazy-loaded relationships can be accessed inside a `@Transactional` method because the session is still open. Outside of one, accessing a lazy collection throws `LazyInitializationException` — map to DTO inside the transaction to avoid this.

## Pagination

Spring Data JPA has built-in pagination. Consider using pagination when response contain many records.

### Repository

Add a `Pageable` parameter to any repository method:

```java
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Page<Project> findAll(Pageable pageable);
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
}
```

### Service

Accept `Pageable` from the controller and return `Page<FeatureDTO>`. Map the page content using `.map()`:

```java
@Transactional(readOnly = true)
public Page<ProjectDTO> getProjects(Pageable pageable) {
    return projectRepository.findAll(pageable).map(projectMapper::toDTO);
}
```

### Controller

Spring MVC auto-resolves `Pageable` from query parameters (`?page=0&size=20&sort=createdAt,desc`). Use `@PageableDefault` to set sensible defaults, and use `responseUtil.successPaged()` instead of `success()`:

```java
@GetMapping
public ResponseEntity<BaseResponseDTO<PagedResponseDTO<ProjectDTO>>> getAll(
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<ProjectDTO> result = projectService.getProjects(pageable);
    return responseUtil.successPaged(result, "Berhasil.", HttpStatus.OK);
}
```

`successPaged` wraps the `Page<T>` into `PagedResponseDTO<T>` automatically. The response JSON will be:

```json
{
  "status": 200,
  "message": "Berhasil.",
  "timestamp": "...",
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  }
}
```

### Sorting

Clients can pass `sort=field,direction` — multiple sort params are allowed:

```
GET /api/projects?page=0&size=10&sort=status,asc&sort=createdAt,desc
```

Only expose fields that are safe to sort on. If you need to restrict which fields clients can sort by, validate `pageable.getSort()` in the service before passing it to the repository.

## Authorization

`SecurityConfig` only distinguishes public vs. authenticated routes. Fine-grained access control is handled at the method level using `@PreAuthorize` (enabled via `@EnableMethodSecurity`).

### Permission-based (granular)

The JWT filter registers each role permission as a `SimpleGrantedAuthority` with the format `RESOURCE:ACTION` — e.g. `PROJECT:CREATE`, `USER:READ`. Use `hasAuthority()` to check these:

```java
@GetMapping("/{id}")
@PreAuthorize("hasAuthority('PROJECT:READ')")
public ResponseEntity<?> getProject(@PathVariable UUID id) { ... }

@PostMapping
@PreAuthorize("hasAuthority('PROJECT:CREATE')")
public ResponseEntity<?> createProject(...) { ... }
```

Available resources: `PROJECT`, `USER`, `INQUIRY`, `NEWS`, `VERIFICATION`
Available actions: `CREATE`, `READ`, `UPDATE`, `DELETE`

### Role-based (coarse-grained)

The JWT filter also registers a `ROLE_<roleName>` authority. Use `hasRole()` when the restriction applies to an entire role rather than a specific resource action:

```java
@PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnlyEndpoint(...) { ... }
```

`hasRole('ADMIN')` is shorthand for `hasAuthority('ROLE_ADMIN')` — Spring adds the `ROLE_` prefix automatically.

Available roles: `ADMIN`, `INVESTOR`, `PROJECT_OWNER`, `EXECUTIVE`

### When to use which

- Use `hasAuthority('RESOURCE:ACTION')` for most endpoints — it's granular and lets permissions be configured per role without code changes.
- Use `hasRole('ROLE_NAME')` in `@PreAuthorize` only when the restriction is inherently role-specific (e.g. an admin-only management endpoint that should never be delegated).
- For routes that must be open or locked globally (e.g. public auth endpoints, health check), configure them in `SecurityConfig.securityFilterChain()` instead — those rules apply before the method security layer.
