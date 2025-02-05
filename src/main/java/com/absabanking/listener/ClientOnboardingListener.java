package com.absabanking.listener;


import com.absabanking.enums.EPreferredContactType;
import com.absabanking.model.Client;
import com.absabanking.repository.ClientRepository;
import com.absabanking.util.GenderConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ClientOnboardingListener {

    public ClientOnboardingListener() {
    }
    private ClientRepository clientRepository;
    @Autowired
    public ClientOnboardingListener(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionEventListener.class);
    @EventListener(Client.class)
    void handleClientOnboarding(Client client) {
        logger.info("_________________START customer onboarding log___________________________");
        if (client.getEPreferredContactType().toString().equalsIgnoreCase(EPreferredContactType.SMS.toString())) {
            logger.info(" Sending SMS   Dear " + GenderConverter.genderConverter(client.getESex().name()) + " , Welcome to Our Bank  : "+ client.getClientContact().getCellNumber());
        } else
            logger.info(" Sending EMAIL  Dear " + GenderConverter.genderConverter(client.getESex().name()) + " ,Welcome to Our Bank : "+ client.getClientContact().getCellNumber());
        logger.info("_________________End customer onboarding log___________________________");
    }


}
