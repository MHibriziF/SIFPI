package id.go.kemenkoinfra.ipfo.sifpi.auth.event;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateOwnerRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;

/**
 * Event published after project owner registration transaction commits.
 * Used to trigger email sending asynchronously after successful DB commit.
 */
public record OwnerRegisteredEvent(User user, CreateOwnerRequest request) {
}
