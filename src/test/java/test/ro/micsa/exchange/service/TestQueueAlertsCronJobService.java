package test.ro.micsa.exchange.service;

import com.google.appengine.api.datastore.Key;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ro.micsa.exchange.dao.EmailAlertDAO;
import ro.micsa.exchange.dao.NotificationDAO;
import ro.micsa.exchange.dao.UserDAO;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.NotificationComparator;
import ro.micsa.exchange.dto.TransactionType;
import ro.micsa.exchange.model.EmailAlertEntity;
import ro.micsa.exchange.model.ExchangeRateEntity;
import ro.micsa.exchange.model.NotificationEntity;
import ro.micsa.exchange.model.UserEntity;
import ro.micsa.exchange.service.MemcacheStore;
import ro.micsa.exchange.service.QueueAlertsCronJobServiceImpl;
import ro.micsa.exchange.utils.DateUtils;
import ro.micsa.exchange.utils.EmailSender;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import ro.micsa.exchange.service.EmailAlertQueueService;

/**
 * Created by Georgian
 * at 17.02.2013 15:52
 */
public class TestQueueAlertsCronJobService {
    public static final String ALERT_ID = "ALERT_ID";

    final String NOW = DateUtils.DATE_SDF.format(new Date());
    private QueueAlertsCronJobServiceImpl service;
    private EmailSender emailSender;
    private NotificationDAO notificationDAO;
    private UserDAO userDAO;
    private MemcacheStore memcacheStore;
    private UserEntity userToSendEmail;
    private ExchangeRateEntity matchingRate;
    private NotificationEntity matchingNotification;
    private EmailAlertDAO emailAlertDAO;
    private EmailAlertQueueService emailAlertQueueService;

    @Before
    public void setUp() {
        service = new QueueAlertsCronJobServiceImpl();

        memcacheStore = mock(MemcacheStore.class);
        service.setMemcacheStore(memcacheStore);
        when(memcacheStore.get(NOW)).thenReturn(createRates());

        notificationDAO = mock(NotificationDAO.class);
        service.setNotificationDAO(notificationDAO);
        when(notificationDAO.getAllEnabledNotifications()).thenReturn(createNotifications());

        userDAO = mock(UserDAO.class);
        service.setUserDAO(userDAO);
        when(userDAO.getUserByKey(any(Key.class))).thenReturn(createUserToSendEmail());

        emailSender = mock(EmailSender.class);

        emailAlertDAO = mock(EmailAlertDAO.class);
        service.setEmailAlertDAO(emailAlertDAO);
        when(emailAlertDAO.save(any(EmailAlertEntity.class))).thenReturn(ALERT_ID);
        
        emailAlertQueueService = mock(EmailAlertQueueService.class);
        service.setEmailAlertQueueService(emailAlertQueueService);
    }

    @Test
    public void testExecuteSendAlertsCronJobService() throws Exception {
        if (!DateUtils.currentDateIsWeekend()) {
            ArgumentCaptor<EmailAlertEntity> emailAlertEntityArgumentCaptor = ArgumentCaptor.forClass(EmailAlertEntity.class);

            Response response = service.execute();

            verify(emailAlertDAO).save(emailAlertEntityArgumentCaptor.capture());
            verify(emailAlertQueueService).queueAlert(ALERT_ID);
            EmailAlertEntity emailAlertEntityCaptured = emailAlertEntityArgumentCaptor.getValue();
            assertThat(emailAlertEntityCaptured.getEmail()).isEqualTo(userToSendEmail.getEmail());
            assertThat(emailAlertEntityCaptured.getThreshold()).isEqualTo(matchingNotification.getThreshold());
            assertThat(emailAlertEntityCaptured.getEvolution()).isEqualTo(matchingRate.getEvolution());
            assertThat(emailAlertEntityCaptured.getStatus()).isEqualTo(EmailAlertEntity.EmailAlertStatus.WAITING);
            assertThat(emailAlertEntityCaptured.getSentDate()).isNull();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getEntity().toString()).isEqualTo("1 alerts queued.");
        }
    }

    @Test
    public void testExecuteSendAlertsCronJobService_dont_execute_on_weekends() {
        if (DateUtils.currentDateIsWeekend()) {
            Response response = service.execute();

            assertThat(response.getStatus()).isEqualTo(406);
        }
    }

    private UserEntity createUserToSendEmail() {
        userToSendEmail = new UserEntity();
        userToSendEmail.setEmail("georgica1985@yahoo.com");
        return userToSendEmail;
    }

    private List<NotificationEntity> createNotifications() {
        List<NotificationEntity> list = new ArrayList<NotificationEntity>();
        matchingNotification = createNotification(CurrencyType.EUR, TransactionType.SELL, 5.0, NotificationComparator.GREATER_OR_EQUAL, "BCR");
        list.add(matchingNotification);
        list.add(createNotification(CurrencyType.USD, TransactionType.SELL, 4.01, NotificationComparator.GREATER_OR_EQUAL, "BCR"));
        return list;
    }

    private NotificationEntity createNotification(CurrencyType currencyType, TransactionType transactionType, double threshold, NotificationComparator comparator, String bank) {
        NotificationEntity notification = new NotificationEntity();
        notification.setCurrencyType(currencyType);
        notification.setTransactionType(transactionType);
        notification.setThreshold(threshold);
        notification.setComparator(comparator);
        notification.setBankName(bank);
        return notification;
    }

    private List<ExchangeRateEntity> createRates() {
        List<ExchangeRateEntity> list = new ArrayList<ExchangeRateEntity>();
        matchingRate = createExchangeRateEntity(CurrencyType.EUR, TransactionType.SELL, 5.0, "BCR");
        list.add(matchingRate);
        list.add(createExchangeRateEntity(CurrencyType.EUR, TransactionType.BUY, 4.99, "BCR"));
        list.add(createExchangeRateEntity(CurrencyType.USD, TransactionType.SELL, 4.0, "BCR"));
        list.add(createExchangeRateEntity(CurrencyType.EUR, TransactionType.REF, 5.0, "BNR"));
        return list;
    }

    private ExchangeRateEntity createExchangeRateEntity(CurrencyType currency, TransactionType transaction, double value, String bank) {
        ExchangeRateEntity rate = new ExchangeRateEntity();
        rate.setCurrencyType(currency);
        rate.setTransactionType(transaction);
        rate.setValue(value);
        rate.setBankName(bank);
        rate.setDate(NOW);
        return rate;
    }
}
