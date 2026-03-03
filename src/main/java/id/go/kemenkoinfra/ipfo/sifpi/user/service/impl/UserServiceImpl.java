package id.go.kemenkoinfra.ipfo.sifpi.user.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.user.dto.UserDTO;
import id.go.kemenkoinfra.ipfo.sifpi.user.mapper.UserMapper;
import id.go.kemenkoinfra.ipfo.sifpi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable, String role, Boolean isVerified,
                                    Boolean isActive, String organisasi, String search) {

        String searchTerm = (search != null && !search.isBlank()) ? search : "";
        String organisasiParam = (organisasi != null) ? organisasi.toLowerCase() : null;

        // Strip sort from pageable — custom @Query methods carry hardcoded ORDER BY clauses
        Pageable unpaged = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        boolean sortByStatus = pageable.getSort().stream()
                .anyMatch(order -> "status".equalsIgnoreCase(order.getProperty())
                        || "isActive".equalsIgnoreCase(order.getProperty()));
        boolean descending = pageable.getSort().stream()
                .anyMatch(org.springframework.data.domain.Sort.Order::isDescending);

        Page<User> userPage;
        if (sortByStatus) {
            userPage = descending
                    ? userRepository.findAllByFiltersOrderByStatusDesc(role, isVerified, isActive, organisasiParam, searchTerm, unpaged)
                    : userRepository.findAllByFiltersOrderByStatusAsc(role, isVerified, isActive, organisasiParam, searchTerm, unpaged);
        } else {
            userPage = userRepository.findAllByFilters(role, isVerified, isActive, organisasiParam, searchTerm, unpaged);
        }

        log.debug("Retrieved {} users for page {}", userPage.getNumberOfElements(), pageable.getPageNumber());

        return userPage.map(userMapper::toUserDTO);
    }
}
