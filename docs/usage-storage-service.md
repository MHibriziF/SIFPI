# StorageService Usage Guide

`StorageService` is an S3-compatible storage client available for any feature that needs file upload/download.

> The bean is **conditional** — it only loads when `STORAGE_ENDPOINT` env var is set.

## Available Methods

| Method | Params | Returns | Description |
|--------|--------|---------|-------------|
| `upload(folder, file)` | `String folder`, `MultipartFile file` | `String` (object key) | Uploads file to `{folder}/{uuid}.{ext}` |
| `download(key)` | `String key` | `FileDownload` | Downloads file — returns bytes + content type in one call |
| `delete(key)` | `String key` | `void` | Deletes file from bucket |
| `getPublicUrl(key)` | `String key` | `String` | Returns the full public URL |

`FileDownload` is a nested record on `StorageService`: `StorageService.FileDownload(byte[] data, String contentType)`.

## Injecting into Your Service

```java
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final StorageService storageService;
    // ...
}
```

## Upload Example

In your service, accept a `MultipartFile` and call `upload` with a folder name. Store the returned key in your entity.

```java
public ProjectDTO createProject(CreateProjectRequest request, MultipartFile thumbnail) {
    String fileKey = storageService.upload("projects", thumbnail);

    Project project = projectMapper.toEntity(request);
    project.setThumbnailKey(fileKey); // set after mapping since it comes from the upload, not the request
    projectRepository.save(project);

    return projectMapper.toDTO(project);
}
```

Your controller passes the `MultipartFile` from the request:

```java
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<BaseResponseDTO<ProjectDTO>> create(
        @RequestPart("data") @Valid CreateProjectRequest request,
        @RequestPart("thumbnail") MultipartFile thumbnail) {

    var result = projectService.createProject(request, thumbnail);
    return responseUtil.success(result, "Proyek berhasil dibuat.", HttpStatus.CREATED);
}
```

## Download Example

File downloads need both the bytes and the content type. Use a record to return both from the service so the controller doesn't need to call `StorageService` directly:

```java
public StorageService.FileDownload getProjectThumbnail(UUID projectId) {
    Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("Project", projectId));

    return storageService.download(project.getThumbnailKey());
}
```

The controller only calls the feature service — no `StorageService` injection needed here:

```java
@GetMapping("/{id}/thumbnail")
public ResponseEntity<byte[]> getThumbnail(@PathVariable UUID id) {
    StorageService.FileDownload file = projectService.getProjectThumbnail(id);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, file.contentType())
            .body(file.data());
}
```

## Delete Example

When deleting an entity that has an associated file:

```java
public void deleteProject(UUID projectId) {
    Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("Project", projectId));

    // delete file from bucket first
    if (project.getThumbnailKey() != null) {
        storageService.delete(project.getThumbnailKey());
    }

    projectRepository.delete(project);
}
```

## Get Public URL Example

Use `getPublicUrl` when you need to expose a file URL in a DTO. Don't do manual mapping in the service — use a MapStruct `@AfterMapping` to resolve the key to a URL:

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

Then in the service, pass `storageService` as a context argument when calling the mapper:

```java
public ProjectDTO getProject(UUID id) {
    Project project = projectRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Project", id));
    return projectMapper.toDTO(project, storageService);
}
```

## Key Conventions

- **Store the key, not the URL** — keys are stable, URLs may change if the bucket endpoint changes.
- **Use a meaningful folder name** per feature — e.g. `"projects"`, `"documents"`, `"avatars"`.
- **Delete files** when deleting the owning entity to avoid orphaned files in the bucket.
- The key format is `{folder}/{uuid}.{extension}`, guaranteed unique.

## Error Handling

- Uploading with an empty file throws `IllegalArgumentException` (validate in your controller).
- Downloading a nonexistent key throws `NotFoundException`.
- IO errors during upload/download throw `RuntimeException` — caught by `GlobalExceptionHandler`.
