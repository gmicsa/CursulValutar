package ro.micsa.exchange.service;

import com.restfb.types.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.micsa.exchange.dao.NotificationDAO;
import ro.micsa.exchange.dao.UserDAO;
import ro.micsa.exchange.dto.Notification;
import ro.micsa.exchange.dto.TransactionType;
import ro.micsa.exchange.model.NotificationEntity;
import ro.micsa.exchange.model.UserEntity;
import ro.micsa.exchange.retrievers.BNRDataRetriever;
import ro.micsa.exchange.utils.Converter;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Georgian
 * at 26.01.2013 15:29
 */
@Component
@Path("/notification/")
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private NotificationDAO notificationDAO;

    @Autowired
    private FacebookService facebookService;

    @GET
    @Produces({"application/json","application/xml"})
    @Override
    public Response getNotification(@QueryParam("accessToken") String accessToken) {
        User myFBUser = facebookService.getFBUser(accessToken);
        if(myFBUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Facebook OAuth failed").build();
        }
        
        UserEntity myUser = getOrCreateUserEntity(myFBUser);
        List<NotificationEntity> listOfNotifications = notificationDAO.getNotificationForUser(myUser);
        if(listOfNotifications == null || listOfNotifications.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No notification yet available").build();
        }
        
        Notification dtoNotification = Converter.notificationToDTO(listOfNotifications.get(0)); // for now only one notification per user
        return Response.ok(dtoNotification).build();            
    }

    @POST
    @Consumes({"application/json","application/xml"})
    @Override
    public Response saveNotification(@QueryParam("accessToken") String accessToken, Notification notification) {
        User myFBUser = facebookService.getFBUser(accessToken);
        if(myFBUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Facebook OAuth failed").build();
        }
        
        // some checks before trying to save a notification
        if(notification.getThreshold() <= 0){
            return Response.status(Response.Status.BAD_REQUEST).entity("Threshold should be > 0").build();
        }
        if(notification.getBankName().equals(BNRDataRetriever.BNR) && !notification.getTransactionType().equals(TransactionType.REF)){
            return Response.status(Response.Status.BAD_REQUEST).entity("BNR notifications do not support buy/sell rates").build();
        }
        
        UserEntity myUser = getOrCreateUserEntity(myFBUser);
        List<NotificationEntity> listOfNotifications = notificationDAO.getNotificationForUser(myUser);
        if(listOfNotifications != null && !listOfNotifications.isEmpty()) {
            notificationDAO.deleteNotification(listOfNotifications.get(0));  // since we must have only one notification per user
        }
        
        NotificationEntity newNotificationEntity = Converter.notificationToEntity(notification);        
        newNotificationEntity.setUserId(myUser.getId()); // registrating the notification to the correct user
        notificationDAO.saveNotification(newNotificationEntity); // saving into the database
        
        return Response.ok(notification).build();
    }
    
    private UserEntity getOrCreateUserEntity(User userFB) {
        UserEntity userEntity = userDAO.getUserByFacebookId(userFB.getId());
        
        if(userEntity == null) {
            userEntity = new UserEntity();
            userEntity.setEmail(userFB.getEmail());
            userEntity.setFbUserName(userFB.getFirstName() + " " + userFB.getLastName());
            userEntity.setFbId(userFB.getId());
            
            userEntity = userDAO.saveUser(userEntity);
        }
        
        return userEntity;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public NotificationDAO getNotificationDAO() {
        return notificationDAO;
    }

    public void setNotificationDAO(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public FacebookService getFacebookService() {
        return facebookService;
    }

    public void setFacebookService(FacebookService facebookService) {
        this.facebookService = facebookService;
    }
}
