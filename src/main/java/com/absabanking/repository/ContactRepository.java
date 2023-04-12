package com.absabanking.repository;

import com.absabanking.model.Account;
import com.absabanking.model.Client;
import com.absabanking.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;
@NoRepositoryBean
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Contact findContactByCellNumber(long cellNumber);
}
