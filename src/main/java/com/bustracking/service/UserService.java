package com.bustracking.service;

import com.bustracking.entity.Role;
import com.bustracking.entity.User;
import com.bustracking.exception.ResourceNotFoundException;
import com.bustracking.repository.RoleRepository;
import com.bustracking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String username, String rawPassword, String fullName, String email, String phone, String roleName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already registered.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' is already registered.");
        }

        User user = new User(username, passwordEncoder.encode(rawPassword), fullName, email, phone);

        String targetRole = (roleName != null && !roleName.trim().isEmpty()) ? roleName : "ROLE_PASSENGER";
        Role role = roleRepository.findByName(targetRole)
                .orElseGet(() -> roleRepository.save(new Role(targetRole)));
        user.addRole(role);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
