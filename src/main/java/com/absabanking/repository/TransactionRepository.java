package com.absabanking.repository;

import com.absabanking.enums.ETranType;
import com.absabanking.model.Restriction;
import com.absabanking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction findTransactionBySenderAccount(long accountId);
    List<Transaction> findTransactionByAcquiringInstitutionAndTranType(String bankCode, ETranType eTranType);
}
