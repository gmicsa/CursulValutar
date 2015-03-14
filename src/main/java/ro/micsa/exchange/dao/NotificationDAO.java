/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.dao;

import ro.micsa.exchange.model.NotificationEntity;
import ro.micsa.exchange.model.UserEntity;

import java.util.List;

/**
 *
 * @author AlexSch
 */
public interface NotificationDAO {
    
    NotificationEntity saveNotification(NotificationEntity notification);
    
    List<NotificationEntity> getAllNotifications();

    List<NotificationEntity> getAllEnabledNotifications();
    
    // for now a single notification will be allowed from frontend, but we prepare for multiple notifications
    List<NotificationEntity> getNotificationForUser(UserEntity user);
    
    NotificationEntity deleteNotification(NotificationEntity notification);
    
    NotificationEntity updateNotification(NotificationEntity notification);
        
}
