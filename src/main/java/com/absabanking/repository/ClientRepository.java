package com.absabanking.repository;

import com.absabanking.dto.ClientResponseDto;
import com.absabanking.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Boolean existsByClientIDNumber(String clientIDNumber);
    Boolean existsByPassportNumber(String passportNumber);
/*    @Query("select c,a from Client c join Account a on c.id = a.client.id where a.accountNumber =?1")
    Client findClientByAccountNumber(Long accountNumber);*/

    @Query("SELECT new com.absabanking.dto.ClientResponseDto(c.clientName,c.surname,c.clientIDNumber,c.passportNumber,c.dependents,c.eSex,c.dateOfBirth,c.monthlyExpenses,c.ePreferredContactType,c.receiveNotification,c.race,c.clientAddress,c.clientContact , a.accountNumber,a.accountLimit,a.accountBalance,a.accountType,a.servicingDate)  from Client c join c.accounts a where  c.clientIDNumber =?1")
    ClientResponseDto findClientByClientIDNumber(int clientIDNumber);

    @Query("SELECT new com.absabanking.dto.ClientResponseDto(c.clientName,c.surname,c.clientIDNumber,c.passportNumber,c.dependents,c.eSex,c.dateOfBirth,c.monthlyExpenses,c.ePreferredContactType,c.receiveNotification,c.race,c.clientAddress,c.clientContact , a.accountNumber,a.accountLimit,a.accountBalance,a.accountType,a.servicingDate)  from Client c join c.accounts a where  c.passportNumber =?1")
    ClientResponseDto findClientByPassportNumber(String clientPassportNumber);

    @Query(value = "SELECT c.* from client c join  account a  on c.id = a.client_id  where  a.account_number  =:accountNumber",nativeQuery = true)
    Client findClientByAccountNumber(@Param("accountNumber") long accountNumber);

    Client findClientByPassportNumberOrClientIDNumber(String passportNumber, String clientIDNumber);

}
