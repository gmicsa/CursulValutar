package ro.micsa.exchange.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.micsa.exchange.dao.EmailAlertDAO;
import ro.micsa.exchange.dao.NotificationDAO;
import ro.micsa.exchange.dao.UserDAO;
import ro.micsa.exchange.dto.NotificationComparator;
import ro.micsa.exchange.model.ExchangeRateEntity;
import ro.micsa.exchange.model.NotificationEntity;
import ro.micsa.exchange.model.UserEntity;
import ro.micsa.exchange.utils.Converter;
import ro.micsa.exchange.utils.DateUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Georgian
 * at 12.02.2013 21:33
 */
@Component
@Path("/alerts/")
public class QueueAlertsCronJobServiceImpl implements RESTService {

    private static final Logger log = Logger.getLogger("ExchangeRateService");

    @Autowired
    private NotificationDAO notificationDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private MemcacheStore memcacheStore;

    @Autowired
    private EmailAlertDAO emailAlertDAO;
    
    @Autowired
    private EmailAlertQueueService emailAlertQueueService;

    @GET
    @Override
    public Response execute() {
        if(!DateUtils.currentDateIsWeekend()){
            log.info("Send alerts to users ...");

            String now = DateUtils.DATE_SDF.format(new Date());
            List<ExchangeRateEntity> currentExchangeRateEntities = memcacheStore.get(now);

            int nrAlertsQueued = 0;

            for (NotificationEntity notification : notificationDAO.getAllEnabledNotifications()) {
                ExchangeRateEntity matchingRate = ExchangeRateServiceImpl.getByDayBankCurrencyTransactionType(currentExchangeRateEntities, now, notification.getBankName(), notification.getCurrencyType(), notification.getTransactionType());
                if(matchingRate != null && rateMatchesThreshold(matchingRate.getValue(), notification.getComparator(), notification.getThreshold())){
                    UserEntity user = userDAO.getUserByKey(notification.getUserId());
                    log.info("Queue alert for user: " + user.getEmail());
                    try {
                        String alertId = emailAlertDAO.save(Converter.createEmailAlertEntity(user, notification, matchingRate));
                        emailAlertQueueService.queueAlert(alertId);
                    } catch (Exception e) {
                        log.severe("Error queuing alert for user: " + user.getEmail());
                        log.severe(e.toString());
                    }
                    nrAlertsQueued ++;
                }
            }

            String logMsg = nrAlertsQueued + " alerts queued.";
            log.info(logMsg);
            return Response.ok(logMsg).build();
        }else{
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Service can not be called during weekends!").build();
        }
    }

    private boolean rateMatchesThreshold(double rate, NotificationComparator comparator, double threshold) {
        if(comparator.equals(NotificationComparator.GREATER_OR_EQUAL)){
            return rate >= threshold;
        }else if(comparator.equals(NotificationComparator.SMALLER_OR_EQUAL)) {
            return rate <= threshold;
        }
        return false;
    }

    public NotificationDAO getNotificationDAO() {
        return notificationDAO;
    }

    public void setNotificationDAO(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public MemcacheStore getMemcacheStore() {
        return memcacheStore;
    }

    public void setMemcacheStore(MemcacheStore memcacheStore) {
        this.memcacheStore = memcacheStore;
    }

    public EmailAlertDAO getEmailAlertDAO() {
        return emailAlertDAO;
    }

    public void setEmailAlertDAO(EmailAlertDAO emailAlertDAO) {
        this.emailAlertDAO = emailAlertDAO;
    }

    public EmailAlertQueueService getEmailAlertQueueService() {
        return emailAlertQueueService;
    }

    public void setEmailAlertQueueService(EmailAlertQueueService emailAlertQueueService) {
        this.emailAlertQueueService = emailAlertQueueService;
    }
    
}
