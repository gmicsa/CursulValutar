package ro.micsa.exchange.service;

import com.restfb.DefaultFacebookClient;
import com.restfb.types.User;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Georgian
 * Date: 01.03.2013
 * Time: 21:03
 * To change this template use File | Settings | File Templates.
 */
@Component
public class FacebookServiceImpl implements FacebookService {

    @Override
    public User getFBUser(String accessToken){
        User myFBUser;
        try{
            myFBUser = new DefaultFacebookClient(accessToken).fetchObject("me", User.class);
        }catch(Exception e){
            return null;
        }
        return myFBUser;
    }

}
