package com.absabanking.listener;

import com.absabanking.dto.ClientDto;
import com.absabanking.enums.EPreferredContactType;
import com.absabanking.model.Client;
import com.absabanking.model.Transaction;
import com.absabanking.repository.ClientRepository;
import com.absabanking.util.GenderConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class TransactionEventListener {
    public TransactionEventListener() {
    }
    private ClientRepository clientRepository;
    @Autowired
    public TransactionEventListener(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionEventListener.class);
    @EventListener(Transaction.class)
    void handleTransactionEvent(Transaction transactionEvent) {
        logger.info("_________________START of internal transfer log___________________________");
        ClientDto recipient = getClientCommunicationDetails(transactionEvent.getSenderAccount());
        if (recipient.getPrefferedCommunicationMethod().equalsIgnoreCase(EPreferredContactType.SMS.toString())) {
            logger.info(" Sending SMS  : {} Dear " + GenderConverter.genderConverter(recipient.getSex()) + " , a transaction of : {} has been send to your account :{}", recipient.getCellNumber(), recipient.getClientSurname(), transactionEvent.getTransactionAmount(), LocalDateTime.now());
        } else
            logger.info(" Sending EMAIL : {} Dear " + GenderConverter.genderConverter(recipient.getSex()) + " , a transaction of : {} has been send to your account :{}", recipient.getEmail(), recipient.getClientSurname(), transactionEvent.getTransactionAmount(), LocalDateTime.now());
        logger.info("_________________END of internal transfer log___________________________");
    }
    /**
     * Get communication details of a bank client by passing in their account number
     *
     * @param accountNumber bank client account number
     * @return a bank client communication object
     */
    public ClientDto getClientCommunicationDetails(long accountNumber) {
        Client bankClient = clientRepository.findClientByAccountNumber(accountNumber);
        ClientDto client = new ClientDto();
        client.setClientName(bankClient.getName());
        client.setClientSurname(bankClient.getSurname());
        client.setCellNumber(bankClient.getClientContact().getCellNumber());
        client.setEmail(bankClient.getClientContact().getEmail());
        client.setPrefferedCommunicationMethod(bankClient.getEPreferredContactType().name());
        client.setSex(bankClient.getESex().toString());
        return client;
    }
}