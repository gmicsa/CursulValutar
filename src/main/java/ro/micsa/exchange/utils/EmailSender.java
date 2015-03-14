/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.utils;

import ro.micsa.exchange.model.ExchangeRateEntity;
import ro.micsa.exchange.model.NotificationEntity;
import ro.micsa.exchange.model.UserEntity;

import java.util.List;
import java.util.Map;
import ro.micsa.exchange.model.EmailAlertEntity;

/**
 *
 * @author georgian
 */
public interface EmailSender {
    
    void sendGenericMail(String from, String[] tos, String subject, String text) throws Exception;
    
    void sendExchangeRateMail(ExchangeRateEntity exchangeRate, boolean created);
    
    void sendCronResultsMail(Long duration, List<String> banksProcessedSuccessfuly, Map<String, Throwable> errors);

    void sendAlertMail(EmailAlertEntity emailAlertEntity) throws Exception;
}
