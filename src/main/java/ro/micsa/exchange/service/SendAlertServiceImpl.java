/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ro.micsa.exchange.dao.EmailAlertDAO;
import ro.micsa.exchange.model.EmailAlertEntity;
import ro.micsa.exchange.utils.DateUtils;
import ro.micsa.exchange.utils.EmailSender;

/**
 *
 * @author Georgian
 */
@Component
@Path("/send_alert/")
public class SendAlertServiceImpl implements RESTService{
    
    private static final Logger log = Logger.getLogger("ExchangeRateService");
    
    @Context 
    private UriInfo uriInfo;
    
    @Autowired
    private EmailAlertDAO emailAlertDAO;
    
    @Autowired
    private EmailSender emailSender;

    @GET
    @Override
    public Response execute() {
        String alertId = uriInfo.getQueryParameters().getFirst("id");
        if(!StringUtils.hasText(alertId)){
            return Response.status(Response.Status.BAD_REQUEST).entity("id is required").build();
        }
        EmailAlertEntity emailAlertEntity = emailAlertDAO.getById(alertId);
        if(emailAlertEntity == null){
            return Response.status(Response.Status.NOT_FOUND).entity("no alert found with this id").build();
        }
        
        Response.Status status = Response.Status.OK;
        
        try {
            log.info("Send alert to email: " + emailAlertEntity.getEmail());
            emailAlertEntity.setSentDate(DateUtils.getCurrentTimeInSDFFormat());
            emailSender.sendAlertMail(emailAlertEntity);
            emailAlertEntity.setStatus(EmailAlertEntity.EmailAlertStatus.SENT);
        } catch (Exception ex) {
            log.severe("Error sending alert to email: " + emailAlertEntity.getEmail());
            log.severe(ex.toString());
            emailAlertEntity.setStatus(EmailAlertEntity.EmailAlertStatus.ERROR);
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }
        emailAlertDAO.update(emailAlertEntity);
        
        return Response.status(status).build();
    }

    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public EmailAlertDAO getEmailAlertDAO() {
        return emailAlertDAO;
    }

    public void setEmailAlertDAO(EmailAlertDAO emailAlertDAO) {
        this.emailAlertDAO = emailAlertDAO;
    }

    public EmailSender getEmailSender() {
        return emailSender;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }
}
