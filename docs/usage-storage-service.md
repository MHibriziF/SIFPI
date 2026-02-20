# StorageService Usage Guide

`StorageService` is an S3-compatible storage client available for any feature that needs file upload/download.

> The bean is **conditional** — it only loads when `STORAGE_ENDPOINT` env var is set.

## Available Methods

| Method | Params | Returns | Description |
|--------|--------|---------|-------------|
| `upload(folder, file)` | `String folder`, `MultipartFile file` | `String` (object key) | Uploads file to `{folder}/{uuid}.{ext}` |
| `download(key)` | `String key` | `byte[]` | Downloads file content |
| `getContentType(key)` | `String key` | `String` | Returns the file's MIME type |
| `delete(key)` | `String key` | `void` | Deletes file from bucket |
| `getPublicUrl(key)` | `String key` | `String` | Returns the full public URL |

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
    // upload file — stored as "projects/{uuid}.png"
    String fileKey = storageService.upload("projects", thumbnail);

    Project project = new Project();
    project.setName(request.getName());
    project.setThumbnailKey(fileKey); // store the key, not the URL
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

```java
public byte[] getProjectThumbnail(UUID projectId) {
    Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("Project", projectId));

    return storageService.download(project.getThumbnailKey());
}
```

Controller returning the file as a response:

```java
@GetMapping("/{id}/thumbnail")
public ResponseEntity<byte[]> getThumbnail(@PathVariable UUID id) {
    Project project = projectService.findById(id);
    String contentType = storageService.getContentType(project.getThumbnailKey());
    byte[] data = storageService.download(project.getThumbnailKey());

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, contentType)
            .body(data);
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

Use this when you need to return a URL to the client (e.g., in a DTO):

```java
public ProjectDTO toDTO(Project project) {
    ProjectDTO dto = new ProjectDTO();
    dto.setName(project.getName());

    if (project.getThumbnailKey() != null) {
        dto.setThumbnailUrl(storageService.getPublicUrl(project.getThumbnailKey()));
    }

    return dto;
}
```

## Key Conventions

- **Store the key, not the URL** — keys are stable, URLs may change if the bucket endpoint changes.
- **Use a meaningful folder name** per feature — e.g. `"projects"`, `"documents"`, `"avatars"`.
- **Delete files** when deleting the owning entity to avoid orphaned files in the bucket.
- The key format is `{folder}/{uuid}.{extension}`, guaranteed unique.

## Error Handling

- Uploading with an empty file throws `IllegalArgumentException` (validate in your controller).
- Downloading/getting content type of a nonexistent key throws `NotFoundException`.
- IO errors during upload/download throw `RuntimeException` — caught by `GlobalExceptionHandler`.
