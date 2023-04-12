package com.absabanking.batch;

import com.absabanking.enums.ETranType;
import com.absabanking.service.EmailService;
import com.absabanking.model.Bank;
import com.absabanking.model.Transaction;
import com.absabanking.service.BankService;
import com.absabanking.service.TransactionService;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Configuration
@EnableBatchProcessing
@Component
public class ProcessReconciliationBatchJob {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ProcessReconciliationBatchJob.class);
    TransactionService transactionService;
    BankService bankService;
    EmailService sendEmailService;
    private static final String SUBJECT = "E.O.D reconciliation process";
    private static final String MAIL_TEXT = "Good day  .Please find attached EOD process file.... Thank you";
    @Value("${spring.mail.username}")
    private String from;
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    @Value("${file.upload-dir}")
    private String baseUrl;

    @Autowired
    public ProcessReconciliationBatchJob(TransactionService transactionService, BankService bankService, EmailService sendEmailService) {
        this.transactionService = transactionService;
        this.bankService = bankService;
        this.sendEmailService = sendEmailService;
    }
    /**
     * This is  a cron   scheduled  to run every day at midnight
     * more  examples here https://crontab.guru/#0_*_*_*
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void runCron() {
        log.info("############################## START BATCH JOB EXECUTION##########################");
        EODProcess();
    }
    /**
     * For  the End of Day process .Get  all the banks  and  check if they have any transactions  for the current day
     * If they have  create an EOD file .If they  don't well do nothing .
     */
    public void EODProcess() {
        List<Bank> listOfAllBanks = bankService.getAllBanks();
        for (Bank bank : listOfAllBanks) {
            List<Transaction> transactionsOfTheDayByBankCode = transactionService.findAllTransactionsByBankCode(bank.getBankCode(), ETranType.ACT_ON_BEHALF);
            if (!transactionsOfTheDayByBankCode.isEmpty()) {
                log.info("start of eod process for bank : {}  ", bank.getBankCode());
                for (Transaction transaction : transactionsOfTheDayByBankCode) {
                    writeEODFiles(bank, transaction);
                    String fileNameToAttachToEmail = writeEODFiles(bank, transaction);
                    String filePathAndName = baseUrl + bank.getName();
                    log.info("retrieved these transactions : {}", transaction.getSenderAccount());
                    sendEODFileAsEmailAttachment(bank.getBankContact().getEmail(), SUBJECT, MAIL_TEXT, from, filePathAndName, bank.getBankContact().getEmail(), fileNameToAttachToEmail);
                }
                log.info("  end of eod process for bank : {}   ", bank.getBankCode());
            }
            log.warn("no transactions  to process for bank : {}", bank.getBankCode());
        }
    }

    /**
     * @param to              mail reciver
     * @param subject         subject of mail
     * @param text            body of email
     * @param from            the sender of email
     * @param filePathAndName where  the  file is located
     * @param cc              who  is copied in email
     * @param outputFileName  the output  file name
     */
    private void sendEODFileAsEmailAttachment(String to, String subject, String text, String from, String filePathAndName, String cc, String outputFileName) {
        try {
            sendEmailService.sendEmailWithAttachment(to, subject, text, from, filePathAndName, cc, outputFileName);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Writing End of day  transactions  to a   csv  file
     *
     * @param transaction the  transaction object
     * @param bank        the  object holding bank details
     */
    public String writeEODFiles(Bank bank, Transaction transaction) {
        String fileHeader = "AcquiringInstitution,TransactionFee,PosType,Narrative,ReceiverAccount,Reference,SenderAccount,TranType,TransactionCharges,TransactionCurrencyCode";
        String dirName = baseUrl + bank.getName();
        String fileName = bank.getName() + "_" + LocalDate.now() + ".csv";
        FileWriter fileWriter = null;
        Path path = Paths.get(dirName);
        if (!Files.exists(path)) {
            log.info("Directory doesnt  exist  creating new directory :{}", dirName);
            try {
                Files.createDirectory(path);
                File file = new File(dirName, fileName);
                fileWriter = new FileWriter(file);
                fileWriter.append(fileHeader);
                writeFiles(transaction, fileWriter);
            } catch (Exception e) {
                System.out.println("Error in CsvFileWriter !!!");
                e.printStackTrace();
            } finally {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("Error while flushing/closing fileWriter !!!");
                    e.printStackTrace();
                }
            }
            log.info("New EOD Transaction file  has  been  created  successfully created", fileName);
            log.info("Directory  successfully created", dirName);
        } else {
            try {
                File file = new File(dirName, fileName);
                fileWriter = new FileWriter(file);
                fileWriter.append(fileHeader);
                //Add a new line separator after the header
                writeFiles(transaction, fileWriter);
            } catch (Exception e) {
                System.out.println("Error in CsvFileWriter !!!");
                e.printStackTrace();
            } finally {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("Error while flushing/closing fileWriter !!!");
                    e.printStackTrace();
                }
            }
            log.info("New EOD Transaction file  has  been  created  successfully created", fileName);
            log.info("Directory  already exists will not create new  Directory ", dirName);
        }
        return Paths.get(fileName).toString();
    }

    /**
     * Util method  to  write to a file
     *
     * @param transaction transaction object
     * @param fileWriter  the  file writer  object
     * @throws IOException exception
     */
    private static void writeFiles(Transaction transaction, FileWriter fileWriter) throws IOException {
        //Add a new line separator after the header
        fileWriter.append(NEW_LINE_SEPARATOR);
        fileWriter.append(transaction.getAcquiringInstitution());
        fileWriter.append(COMMA_DELIMITER);
        fileWriter.append(transaction.getAmountTransactionFee().toString());
        fileWriter.append(COMMA_DELIMITER);
        fileWriter.append(transaction.getNarrative());
        fileWriter.append(COMMA_DELIMITER);
        fileWriter.append(String.valueOf(transaction.getReceiverAccount()));
        fileWriter.append(COMMA_DELIMITER);
        fileWriter.append(transaction.getReference());
        fileWriter.append(COMMA_DELIMITER);
        fileWriter.append(String.valueOf(transaction.getSenderAccount()));
        fileWriter.append(COMMA_DELIMITER);
        fileWriter.append(transaction.getTranType().toString());
        fileWriter.append(COMMA_DELIMITER);
        fileWriter.append(transaction.getTransactionCharges().toString());
        fileWriter.append(COMMA_DELIMITER);
        fileWriter.append(transaction.getTransactionCurrencyCode().toString());
        fileWriter.append(COMMA_DELIMITER);
        log.info("CSV file was created successfully !!!");
    }

    /**
     * A neat  way of  archiving  files . Based on the age of the files Suppose  archive all files 10 days old
     *
     */
    @Scheduled(cron = "0 0 12 1 1/1 ?")
    @Transactional
    void archivingProcess() {
        log.info("**********************************START OF ARCHIVING PROCESS*********************************************");
    }

}