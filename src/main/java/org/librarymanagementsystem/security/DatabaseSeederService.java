package org.librarymanagementsystem.security;


import lombok.RequiredArgsConstructor;
import org.librarymanagementsystem.emun.UserRole;
import org.librarymanagementsystem.model.Role;
import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.model.UserRoleMapping;
import org.librarymanagementsystem.repository.RoleRepository;
import org.librarymanagementsystem.repository.UserRepository;
import org.librarymanagementsystem.repository.UserRoleMappingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class DatabaseSeederService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleMappingRepository userRoleMappingRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Set up authentication for system operations
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("SYSTEM", null));

        Role userRole = roleRepository.findByRoleName(UserRole.ROLE_STUDENT)
                .orElseGet(() -> roleRepository.save(new Role(UserRole.ROLE_STUDENT)));

        Role sellerRole = roleRepository.findByRoleName(UserRole.ROLE_SELLER)
                .orElseGet(() -> roleRepository.save(new Role(UserRole.ROLE_SELLER)));

        Role adminRole = roleRepository.findByRoleName(UserRole.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(UserRole.ROLE_ADMIN)));


        createUserIfNotExists("Mahitosh", "mahitoshgiri287@gmail.com", "pass@123", userRole);
        createUserIfNotExists("Admin", "admin.runtime@gmail.com", "admin@123", adminRole);
        createUserIfNotExists("Seller", "seller.runtime@gmail.com", "seller@123", sellerRole);

        // Clear the context after initialization
        SecurityContextHolder.clearContext();

    }

    private void createUserIfNotExists(String username, String email, String password, Role role) {
        if (!userRepository.existsByUserName(username)) { // Ensure repo method matches
            User user = new User(username, email, passwordEncoder.encode(password));
            setDefaultUserProperties(user);
            user = userRepository.save(user); // Save User first

            // 🔹 Now Create Role Mapping
            UserRoleMapping userRoleMapping = new UserRoleMapping(user, role);
            userRoleMappingRepository.save(userRoleMapping);
        }
    }

    private void setDefaultUserProperties(User user) {
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        user.setIsTwoFactorEnabled(false);
        user.setSignUpMethod("email");
    }
}
