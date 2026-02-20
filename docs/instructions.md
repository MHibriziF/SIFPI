# Development Instructions

Guidelines to follow when working on this project.

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

| Cache Name | Constant | Max Size | TTL |
|------------|----------|----------|-----|
| `projects` | `AppConstant.CACHE_PROJECTS` | 500 | 7 days |
| `news` | `AppConstant.CACHE_NEWS` | 200 | 1 hour |
| `project-detail` | `AppConstant.CACHE_PROJECT_DETAIL` | 1000 | 1 day |

Always reference cache names through `AppConstant`:
```java
@Cacheable(AppConstant.CACHE_PROJECT_DETAIL)
public ProjectDTO getProject(UUID id) { ... }
```

If you need a new cache with custom sizing/TTL, add it in `CacheConfig`. Otherwise, any unlisted cache name falls back to the default (100 entries, 10 min TTL).

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

## Response Format

All API responses use `BaseResponseDTO<T>` via `ResponseUtil`:
```java
return responseUtil.success(data, "Berhasil.", HttpStatus.OK);
return responseUtil.error("Gagal.", HttpStatus.BAD_REQUEST);
```

Don't construct `ResponseEntity` manually with raw bodies — always wrap with `ResponseUtil`.
