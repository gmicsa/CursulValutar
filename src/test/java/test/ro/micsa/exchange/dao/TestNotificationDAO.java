package test.ro.micsa.exchange.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ro.micsa.exchange.dao.NotificationDAO;
import ro.micsa.exchange.dao.UserDAO;
import ro.micsa.exchange.model.NotificationEntity;
import ro.micsa.exchange.model.UserEntity;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author AlexSch
 */
public class TestNotificationDAO extends AbstractDAOTest<NotificationDAO> {
    
    private UserEntity firstUser;
    private UserEntity secondUser;
    
    private NotificationEntity notificationUser1;
    private NotificationEntity notificationUser2;
    
    @Autowired
    private UserDAO repositoryForUsers;
    
    public TestNotificationDAO() {
        super(NotificationDAO.class);
    }    
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        
        firstUser = new UserEntity();
        firstUser.setFbId("12345111");
        firstUser.setEmail("firstuser@netbeans.org");
        firstUser.setFbUserName("FirstUser");

        secondUser = new UserEntity();
        secondUser.setFbId("12345222");
        secondUser.setEmail("secondtuser@netbeans.org");
        secondUser.setFbUserName("SecondUser");

        notificationUser1 = new NotificationEntity();
        notificationUser1.setThreshold(4.5);
        
        notificationUser2 = new NotificationEntity();
        notificationUser2.setThreshold(4.8);
        
        // saving users in DB
        repositoryForUsers.saveUser(firstUser);
        repositoryForUsers.saveUser(secondUser);
      
        notificationUser1.setUserId(firstUser.getId());
        notificationUser2.setUserId(secondUser.getId());
        
        // saving notifications in DB
        repository.saveNotification(notificationUser1);
        repository.saveNotification(notificationUser2);
    }
    
    @Test
    public void testSaveNotification() {
        assertThat(repository.getAllNotifications()).isNotNull();
    }
    
    @Test
    public void testUserContainsNotification() {
        List<NotificationEntity> listOfNotifications = repository.getNotificationForUser(firstUser);
        assertThat(listOfNotifications).isNotEmpty();
    }
    
    @Test
    public void testUserDeletesAllNotification() {
        List<NotificationEntity> listOfNotifications = repository.getNotificationForUser(firstUser);
        for(NotificationEntity notification : listOfNotifications) {
            repository.deleteNotification(notification);
        }
        
        assertThat(repository.getNotificationForUser(firstUser)).isEmpty();
    }
    
    // etc etc other tests to come and then implement
}
