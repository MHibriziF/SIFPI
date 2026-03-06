package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service;

import java.util.List;

import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BulkInsertResultDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BulkInsertUserRequest;

public interface BulkUserImportService {
    BulkInsertResultDTO bulkInsertUsers(List<BulkInsertUserRequest> requests);
}
