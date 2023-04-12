package com.absabanking.repository;

import com.absabanking.model.Account;
import com.absabanking.model.IntegrationSystems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationSystemsRepository extends JpaRepository<IntegrationSystems, Long> {
    IntegrationSystems findIntegrationSystemsById(long id);
}
