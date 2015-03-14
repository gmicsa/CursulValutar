/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.dao;

import com.google.appengine.api.datastore.Key;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.micsa.exchange.model.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author AlexSch
 */
@Repository
@Transactional
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public UserEntity saveUser(UserEntity user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    public UserEntity deleteUser(UserEntity user) {
        UserEntity attachedUser = entityManager.find(UserEntity.class, user.getId());
        
        if(attachedUser != null) {
            entityManager.remove(attachedUser);
        }
        return attachedUser;
    }

    @Override
    public UserEntity getUserByKey(Key userKey) {
        return entityManager.find(UserEntity.class, userKey);
    }

    @Override
    public UserEntity getUserByFacebookId(String facebookId) {
        UserEntity user;
        
        String queryString = "select u from UserEntity u where u.fbId = :fbId";
        Query query = entityManager.createQuery(queryString);
        query.setParameter("fbId", facebookId);
        
        try {
            Object obj = query.getSingleResult();
            user = (UserEntity)obj;
        } catch (NoResultException nre) {
            user = null;
        }
        
        return user;
    }
    
}
