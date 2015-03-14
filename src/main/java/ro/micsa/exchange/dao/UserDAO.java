/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.dao;

import com.google.appengine.api.datastore.Key;
import ro.micsa.exchange.model.UserEntity;

/**
 *
 * @author AlexSch
 */
public interface UserDAO {
    
    UserEntity saveUser(UserEntity user);
    
    UserEntity getUserByFacebookId(String facebookId);
    
    // some may want to delete their 'Cursul valutar' accounts
    UserEntity deleteUser(UserEntity user);

    UserEntity getUserByKey(Key userKey);
}
