package com.absabanking.repository;

import com.absabanking.model.Notification;
import com.absabanking.model.Restriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestrictionRepository extends JpaRepository<Restriction, Long> {
    Restriction findRestrictionById(long id);
}
