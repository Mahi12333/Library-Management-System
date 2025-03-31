package org.librarymanagementsystem.repository;

import org.librarymanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findById(Long id);

    Boolean existsByUserName(String username);

    Optional<User> findByUserName(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}