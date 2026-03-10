package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service;

import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request.BatchUpdateUserRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BatchUpdateUserRoleResultDTO;

public interface UpdateUserRoleService {
    BatchUpdateUserRoleResultDTO batchUpdateUserRole(BatchUpdateUserRoleRequest request, String adminEmail);
}
