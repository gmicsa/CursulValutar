/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.micsa.exchange.model.NotificationEntity;
import ro.micsa.exchange.model.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 *
 * @author AlexSch
 */
@Repository
@Transactional
public class NotificationImpl implements NotificationDAO {

    @PersistenceContext
    private EntityManager entityManager;
    
   @Override
    public NotificationEntity saveNotification(NotificationEntity notification) {
        entityManager.persist(notification);
        return notification;
    }

    @Override
    public List<NotificationEntity> getNotificationForUser(UserEntity user) {
        String queryString = "select n from NotificationEntity n where n.userId = :uId";
        Query query = entityManager.createQuery(queryString);
        query.setParameter("uId", user.getId());
        return query.getResultList();
    }

    @Override
    public NotificationEntity deleteNotification(NotificationEntity notification) {
        NotificationEntity attachedNotification = entityManager.find(NotificationEntity.class, notification.getId());
        
        if(attachedNotification != null) {
            entityManager.remove(attachedNotification);
        }
        
        return attachedNotification;
    }

    @Override
    public NotificationEntity updateNotification(NotificationEntity notification) {
        notification = entityManager.merge(notification);
        return notification;
    }

    @Override
    public List<NotificationEntity> getAllNotifications() {
        String queryString = "select n from NotificationEntity n";
        Query query =  entityManager.createQuery(queryString);
        return query.getResultList();
    }

    @Override
    public List<NotificationEntity> getAllEnabledNotifications() {
        String queryString = "select n from NotificationEntity n where n.enabled = true";
        Query query =  entityManager.createQuery(queryString);
        return query.getResultList();
    }

}
