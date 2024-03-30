package com.evgeniyfedorchenko.expAssistant.repositories;

import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {

    @Query(value = "SELECT * FROM limits WHERE id = (SELECT MAX(id) FROM limits)", nativeQuery = true)
    Optional<Limit> findByMaxId();
}
