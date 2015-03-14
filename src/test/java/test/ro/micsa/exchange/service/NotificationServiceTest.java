/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.service;

import com.restfb.types.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ro.micsa.exchange.dao.NotificationDAO;
import ro.micsa.exchange.dao.UserDAO;
import ro.micsa.exchange.dto.Notification;
import ro.micsa.exchange.model.NotificationEntity;
import ro.micsa.exchange.model.UserEntity;
import ro.micsa.exchange.service.FacebookService;
import ro.micsa.exchange.service.NotificationService;
import ro.micsa.exchange.service.NotificationServiceImpl;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 *
 * @author AlexSch
 */
public class NotificationServiceTest {

    private NotificationService notificationService;

    private UserDAO userDAO;
    private NotificationDAO notificationDAO;
    private FacebookService facebookService;

    private User facebookUser;
    private final String FAKE_ACCESS_TOKEN = "xyz";
    private final String EMAIL = "georgica1985@yahoo.com";
    private final String FAKE_FB_USER_ID = "123";
    
    @Before
    public void mockAllDependenciesOfNotificationService() {
        notificationService = new NotificationServiceImpl();
        userDAO = mock(UserDAO.class);
        notificationDAO = mock(NotificationDAO.class);

        facebookService = mock(FacebookService.class);
        facebookUser = mock(User.class);
        when(facebookUser.getEmail()).thenReturn(EMAIL);
        when(facebookUser.getId()).thenReturn(FAKE_FB_USER_ID);

        when(facebookService.getFBUser(FAKE_ACCESS_TOKEN)).thenReturn(facebookUser);

        ((NotificationServiceImpl)notificationService).setUserDAO(userDAO);
        ((NotificationServiceImpl)notificationService).setNotificationDAO(notificationDAO);
        ((NotificationServiceImpl)notificationService).setFacebookService(facebookService);
    }

    @Test
    public void testGetNotificationWhenBadAccessTokenThenUnauthorizedResponse(){
        when(facebookService.getFBUser(FAKE_ACCESS_TOKEN)).thenReturn(null);

        assertThat(notificationService.getNotification(FAKE_ACCESS_TOKEN).getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
        verify(facebookService).getFBUser(FAKE_ACCESS_TOKEN);
    }

    @Test
    public void testGetNotificationWhenUserNotInDBThenSaveItInDB(){
        when(userDAO.getUserByFacebookId(FAKE_FB_USER_ID)).thenReturn(null);

        notificationService.getNotification(FAKE_ACCESS_TOKEN);

        ArgumentCaptor<UserEntity> argument = ArgumentCaptor.forClass(UserEntity.class);
        verify(userDAO).saveUser(argument.capture());
        assertThat(argument.getValue().getEmail()).isEqualTo(EMAIL);
        assertThat(argument.getValue().getFbId()).isEqualTo(FAKE_FB_USER_ID);
    }

    @Test
    public void testGetNotificationWhenNoNotificationFoundThenReturn404NotFound(){
        when(notificationDAO.getNotificationForUser(any(UserEntity.class))).thenReturn(Collections.EMPTY_LIST);

        assertThat(notificationService.getNotification(FAKE_ACCESS_TOKEN).getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetNotificationWhenOneNotificationThenReturnIt(){
        List<NotificationEntity> entities = new ArrayList<NotificationEntity>();
        NotificationEntity entity = new NotificationEntity();
        double threshold = 9.99;
        entity.setThreshold(threshold);
        entities.add(entity);
        when(notificationDAO.getNotificationForUser(any(UserEntity.class))).thenReturn(entities);

        Response response = notificationService.getNotification(FAKE_ACCESS_TOKEN);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(((Notification)response.getEntity()).getThreshold()).isEqualTo(threshold);
    }

    @Test
    public void testGetNotificationWhenManyNotificationsThenReturnFirstOne(){
        List<NotificationEntity> entities = new ArrayList<NotificationEntity>();
        NotificationEntity entity = new NotificationEntity();
        double threshold = 9.99;
        entity.setThreshold(threshold);
        entities.add(entity);
        NotificationEntity entity2 = new NotificationEntity();
        double threshold2 = 1.11;
        entity2.setThreshold(threshold2);
        entities.add(entity2);
        when(notificationDAO.getNotificationForUser(any(UserEntity.class))).thenReturn(entities);

        Response response = notificationService.getNotification(FAKE_ACCESS_TOKEN);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(((Notification)response.getEntity()).getThreshold()).isEqualTo(threshold);
    }


    @Test
    public void testSaveNotificationWhenInvalidAccessTokenThenReturnUnauthorizedResponse(){
        when(facebookService.getFBUser(FAKE_ACCESS_TOKEN)).thenReturn(null);
        final int statusCodeUnauthorized = Response.Status.UNAUTHORIZED.getStatusCode();
        
        assertThat(notificationService.saveNotification(FAKE_ACCESS_TOKEN, any(Notification.class)).getStatus()).isEqualTo(statusCodeUnauthorized);
    }

    @Test
    public void testSaveNotificationWhenThresholdLessOrEqualTo0ThenReturnBadRequestResponse(){
        Notification myNotification = mock(Notification.class);
        myNotification.setThreshold(-3.0);
        
        final int statusCodeBadRequest = Response.Status.BAD_REQUEST.getStatusCode();
        
        // already facebook service delivers mocked facebook user when called with FAKE_ACCESS_TOKEN        
        assertThat(notificationService.saveNotification(FAKE_ACCESS_TOKEN, myNotification).getStatus()).isEqualTo(statusCodeBadRequest);
    }

    @Test
    public void testSaveNotificationWhenNotificationAlreadyInDBThenDeleteNotification(){
        Notification myNotification = mock(Notification.class);
        UserEntity myEntity = mock(UserEntity.class);
        
        ArrayList<NotificationEntity> retNot = new ArrayList<NotificationEntity>();
        retNot.add(mock(NotificationEntity.class));
        
        when(myNotification.getThreshold()).thenReturn(3.5);
        when(myNotification.getBankName()).thenReturn("ING");
        when(userDAO.saveUser(any(UserEntity.class))).thenReturn(myEntity);
        when(notificationDAO.getNotificationForUser(myEntity)).thenReturn(retNot);
        
        int firstSize, secondSize;
        
        notificationService.saveNotification(FAKE_ACCESS_TOKEN, myNotification);
        firstSize = notificationDAO.getNotificationForUser(myEntity).size();
        notificationService.saveNotification(FAKE_ACCESS_TOKEN, myNotification);
        secondSize = notificationDAO.getNotificationForUser(myEntity).size();
        
        assertThat(firstSize == secondSize && secondSize == 1).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void testSaveNotificationWhenNotificationSavedSuccessfulyThenReturnOK(){
        Notification myNotification = mock(Notification.class);
        when(myNotification.getThreshold()).thenReturn(2.4);
        when(myNotification.getBankName()).thenReturn("ING");
        when(userDAO.saveUser(any(UserEntity.class))).thenReturn(mock(UserEntity.class));
        
        final int statusCodeOk = Response.Status.OK.getStatusCode();
        
        assertThat(notificationService.saveNotification(FAKE_ACCESS_TOKEN, myNotification).getStatus()).isEqualTo(statusCodeOk);
    }
}
