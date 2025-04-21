package org.librarymanagementsystem.repository;

import org.librarymanagementsystem.model.Fines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinesRepository extends JpaRepository<Fines, Long> {
}
