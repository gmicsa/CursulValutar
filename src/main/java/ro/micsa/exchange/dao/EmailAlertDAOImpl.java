package ro.micsa.exchange.dao;

import com.google.appengine.api.datastore.KeyFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.micsa.exchange.model.EmailAlertEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * User: Georgian
 * Date: 09.04.2013
 * Time: 21:28
 */
@Repository
@Transactional
public class EmailAlertDAOImpl implements EmailAlertDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String save(EmailAlertEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        return entity.getId() != null ? KeyFactory.keyToString(entity.getId()) : "empty";
    }

    @Override
    @Transactional(readOnly = true)
    public EmailAlertEntity getById(String id) {
        return entityManager.find(EmailAlertEntity.class, KeyFactory.stringToKey(id));
    }

    @Override
    public void update(EmailAlertEntity emailAlertEntity) {
        entityManager.merge(emailAlertEntity);
    }
}
