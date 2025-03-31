package org.librarymanagementsystem.repository;

import org.librarymanagementsystem.model.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, Long> {
    @Query("SELECT r.roleName FROM UserRoleMapping ur JOIN ur.role r WHERE ur.user.id = :userId")
    List<String> findRolesByUserId(Long userId);
}
