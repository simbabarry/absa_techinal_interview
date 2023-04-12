package com.absabanking.repository;

import com.absabanking.model.Account;
import com.absabanking.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@NoRepositoryBean
public interface AddressRepository extends JpaRepository<Address, Long> {
}
