package com.absabanking.listener;


import com.absabanking.dto.ClientDto;
import com.absabanking.enums.EPreferredContactType;
import com.absabanking.model.Account;
import com.absabanking.model.Client;
import com.absabanking.repository.ClientRepository;
import com.absabanking.service.EmailService;
import com.absabanking.util.GenderConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AccountCreationListener {

    public AccountCreationListener() {
    }
    EmailService sendEmailService;
    private ClientRepository clientRepository;
    @Autowired
    public AccountCreationListener(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionEventListener.class);
    @EventListener(Account.class)
    void handleTransactionEvent(Account account) {
        logger.info("_________________START account creation log___________________________");
        ClientDto recipient = getClientCommunicationDetails(account.getAccountNumber());
        if (recipient.getPrefferedCommunicationMethod().equalsIgnoreCase(EPreferredContactType.SMS.toString())) {

            logger.info(" Sending SMS   Dear " + GenderConverter.genderConverter(recipient.getSex()) + "  ,Account successfully created "+ recipient.getCellNumber());
        } else
            logger.info(" Sending EMAIL  Dear " + GenderConverter.genderConverter(recipient.getSex()) + " , Account successfully created "+ recipient.getEmail());
        //sendHtmlEmail(String to, String subject, String text, String from, String cc) throws MessagingException {
        //sendEmailService.sendHtmlEmail(recipient.getEmail(),);
        logger.info("_________________END account creation log___________________________");
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
        client.setClientName(bankClient.getClientName());
        client.setClientSurname(bankClient.getSurname());
        client.setCellNumber(bankClient.getClientContact().getCellNumber());
        client.setEmail(bankClient.getClientContact().getEmail());
        client.setPrefferedCommunicationMethod(bankClient.getEPreferredContactType().name());
        client.setSex(bankClient.getESex().toString());
        return client;
    }


}
