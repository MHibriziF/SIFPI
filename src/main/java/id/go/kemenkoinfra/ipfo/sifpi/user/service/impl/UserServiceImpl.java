package id.go.kemenkoinfra.ipfo.sifpi.user.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
                                    Boolean isActive, String organisasi, String search,
                                    String sortBy, String sortDirection) {

        String searchTerm = (search != null && !search.isBlank()) ? search : "";
        String organisasiParam = (organisasi != null) ? organisasi.toLowerCase() : null;
        String roleParam = (role != null) ? role.toUpperCase() : null;

        Sort sort = buildSort(sortBy, sortDirection);
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<User> userPage = userRepository.findAllByFilters(
                roleParam, isVerified, isActive, organisasiParam, searchTerm, pageRequest);

        log.debug("Retrieved {} users for page {} [sortBy={}, sortDirection={}]",
                userPage.getNumberOfElements(), pageable.getPageNumber(), sortBy, sortDirection);

        return userPage.map(userMapper::toUserDTO);
    }

    private Sort buildSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        if ("status".equalsIgnoreCase(sortBy)) {
            return Sort.by(direction, "active");
        } else if ("role".equalsIgnoreCase(sortBy)) {
            return Sort.by(direction, "role.name");
        } else {
            return Sort.by(Sort.Direction.DESC, "createdAt"); // default
        }
    }
}

