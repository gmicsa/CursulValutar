package ro.micsa.exchange.dao;

import com.google.appengine.api.datastore.Key;
import ro.micsa.exchange.model.EmailAlertEntity;

/**
 * User: Georgian
 * Date: 09.04.2013
 * Time: 21:27
 */
public interface EmailAlertDAO {

    public String save(EmailAlertEntity entity);

    public EmailAlertEntity getById(String id);

    public void update(EmailAlertEntity emailAlertEntity);
}
