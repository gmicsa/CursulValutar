/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ro.micsa.exchange.model.ExchangeRateEntity;
import ro.micsa.exchange.model.NotificationEntity;
import ro.micsa.exchange.model.UserEntity;
import ro.micsa.exchange.service.TemplateService;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import ro.micsa.exchange.model.EmailAlertEntity;

/**
 *
 * @author georgian
 */
@Component
public class EmailSenderImpl implements EmailSender {
    
    private static Logger log = Logger.getLogger("ExchangeRateService");
    
    public static final String ADMIN = "admin@cursrapid.ro";
    public static final String GEO = "georgian.micsa@gmail.com";
    public static final String ALEX = "schiaucualex@yahoo.com";
    public static final String[] RECIPIENTS_ERROR_MSG = new String[]{GEO, ALEX};
    
    public static final String SUBJECT = "Currency exchange rate %s for %s";
    public static final String SUBJECT_CRON = "%s failed at %s";

    @Autowired
    private TemplateService templateService;

    @Override
    public void sendGenericMail(String from, String[] tos, String subject, String text) throws Exception {
        log.info("Send mail to " + tos + " with subject " + subject);
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        for(String to : tos){
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
        }
        msg.setSubject(subject);
        msg.setContent(text, "text/html; charset=utf-8");
        Transport.send(msg);

    }

    @Override
    public void sendExchangeRateMail(ExchangeRateEntity exchangeRate, boolean created) {
        StringBuffer emailContent = new StringBuffer();
        emailContent.append("@ ");
        emailContent.append(DateUtils.RO_SDF.format(new Date()));
        emailContent.append(",\n\n");
        emailContent.append(exchangeRate);
        try {
            sendGenericMail(ADMIN, RECIPIENTS_ERROR_MSG, String.format(SUBJECT, created?"created":"modified", exchangeRate.getDate()), emailContent.toString());
        } catch (Exception ex) {
            log.warning("Error sending mail: " + ex);
        }
    }

    @Override
    public void sendCronResultsMail(Long duration, List<String> banksProcessedSuccessfuly, Map<String, Throwable> errors) {
        StringBuffer emailContent = new StringBuffer();
        emailContent.append("<p>");
        emailContent.append("Fetch process took: ");
        emailContent.append(duration);
        emailContent.append(" ms</p>");
        emailContent.append("<p>Successfully processed: ");
        emailContent.append(StringUtils.collectionToCommaDelimitedString(banksProcessedSuccessfuly));
        emailContent.append("</p>");
        if(errors.keySet().size() > 0){
            emailContent.append("<p>");
            emailContent.append("Banks that failed:<br/><br/>");
            for(String bank : errors.keySet()){
                emailContent.append("<span style='color:red;font-weight:bold;'>");
                emailContent.append(bank).append(" -> ").append(errors.get(bank).getMessage());
                emailContent.append("</span><br/>");
                StringWriter sw = new StringWriter();
                errors.get(bank).printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                String exceptionTruncated = exceptionAsString.length() < 1000 ? exceptionAsString : exceptionAsString.substring(0, 999);
                emailContent.append(exceptionTruncated.replaceAll("\n", "<br/>"));
                emailContent.append("<br/><br/>");
            }
            emailContent.append("</p>");
        }

        try {
            String subject = String.format(SUBJECT_CRON, StringUtils.collectionToCommaDelimitedString(errors.keySet()), DateUtils.RO_SDF_HH_mm.format(new Date()));
            sendGenericMail(ADMIN, RECIPIENTS_ERROR_MSG, subject, emailContent.toString());
        } catch (Exception ex) {
            log.warning("Error sending mail: " + ex);
        }
    }

    @Override
    public void sendAlertMail(EmailAlertEntity emailAlertEntity) throws Exception {
        String subject = templateService.getAlertsMailSubject(emailAlertEntity.getLanguage());
        String message = templateService.getAlertsMailContent(emailAlertEntity.getLanguage(), emailAlertEntity.getFbUserName(),
                emailAlertEntity.getTransactionType(), emailAlertEntity.getCurrencyType(), emailAlertEntity.getBankName(),
                emailAlertEntity.getThreshold(), emailAlertEntity.getValue(), emailAlertEntity.getEvolution());
        String[] destination = {emailAlertEntity.getEmail()};

        sendGenericMail(ADMIN, destination, subject, message);
    }
}
