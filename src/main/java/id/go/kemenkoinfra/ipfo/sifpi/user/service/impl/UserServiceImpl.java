package id.go.kemenkoinfra.ipfo.sifpi.user.service.impl;

import org.springframework.data.domain.Page;
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

        Page<User> userPage = userRepository.findAllByFilters(
                role, isVerified, isActive, organisasiParam, searchTerm, pageable
        );

        log.debug("Retrieved {} users for page {}", userPage.getNumberOfElements(), pageable.getPageNumber());

        return userPage.map(userMapper::toUserDTO);
    }
}
