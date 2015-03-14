/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.dao;

import org.junit.Before;
import org.junit.Test;
import ro.micsa.exchange.dao.UserDAO;
import ro.micsa.exchange.model.UserEntity;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author AlexSch
 */
public class TestUserDAO extends AbstractDAOTest<UserDAO> {
    
    private UserEntity firstUser;
    private UserEntity secondUser;
    
    public TestUserDAO() {
        super(UserDAO.class);
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

        // saving users in DB
        repository.saveUser(firstUser);
        repository.saveUser(secondUser);
    }
    
    @Test
    public void testGetUserByFacebookId() {
        String facebookId = secondUser.getFbId();
        UserEntity userFromRepo = repository.getUserByFacebookId(facebookId);
        assertThat(userFromRepo.equals(secondUser)).isTrue();
    }
    
    @Test
    public void testDeletedUser() {
        String facebookId = secondUser.getFbId();
        repository.deleteUser(secondUser);
        assertThat(repository.getUserByFacebookId(facebookId)).isNull();
    }
    
}
