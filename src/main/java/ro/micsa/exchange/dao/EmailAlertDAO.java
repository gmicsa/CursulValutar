package ro.micsa.exchange.dao;

import com.google.appengine.api.datastore.Key;
import ro.micsa.exchange.model.EmailAlertEntity;

/**
 * User: Georgian
 * Date: 09.04.2013
 * Time: 21:27
 */
public interface EmailAlertDAO {

    String save(EmailAlertEntity entity);

    EmailAlertEntity getById(String id);

    void update(EmailAlertEntity emailAlertEntity);
}
