package com.absabanking.repository;

import com.absabanking.model.Account;
import com.absabanking.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Card findTopByOrderByCardNumberDesc();

}