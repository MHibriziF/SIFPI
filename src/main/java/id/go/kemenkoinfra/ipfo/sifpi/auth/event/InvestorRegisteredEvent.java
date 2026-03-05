package id.go.kemenkoinfra.ipfo.sifpi.auth.event;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateInvestorRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;

/**
 * Event published after investor registration transaction commits.
 * Used to trigger email sending asynchronously after successful DB commit.
 */
public record InvestorRegisteredEvent(User user, CreateInvestorRequest request) {
}
