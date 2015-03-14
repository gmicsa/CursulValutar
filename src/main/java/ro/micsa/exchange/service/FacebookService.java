package ro.micsa.exchange.service;

import com.restfb.types.User;

/**
 * Created with IntelliJ IDEA.
 * User: Georgian
 * Date: 01.03.2013
 * Time: 21:03
 * To change this template use File | Settings | File Templates.
 */
public interface FacebookService {

    public User getFBUser(String accessToken);

}
