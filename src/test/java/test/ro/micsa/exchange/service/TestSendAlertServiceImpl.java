/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.service;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ro.micsa.exchange.dao.EmailAlertDAO;
import ro.micsa.exchange.model.EmailAlertEntity;
import ro.micsa.exchange.service.SendAlertServiceImpl;
import ro.micsa.exchange.utils.EmailSender;

/**
 *
 * @author Georgian
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSendAlertServiceImpl {
    public static final String ALERT_ID = "123";
    
    @InjectMocks
    private SendAlertServiceImpl sendAlertServiceImpl = new SendAlertServiceImpl();
    
    @Mock
    private EmailAlertDAO emailAlertDAO;
    
    @Mock
    private EmailSender emailSender;
    
    @Mock
    private UriInfo uriInfo;
    
    @Before
    public void setUp(){
    }
    
    @Test
    public void test_GivenSendAlertService_WhenNullAlertId_ThenBadRequestIsReturned(){
        setAlertId(null);
        Assertions.assertThat(sendAlertServiceImpl.execute().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }
    
    @Test
    public void test_GivenSendAlertService_WhenEmptyAlertId_ThenBadRequestIsReturned(){
        setAlertId("  ");
        Assertions.assertThat(sendAlertServiceImpl.execute().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }
    
    @Test
    public void test_GivenSendAlertService_WhenAlertNotFound_Then404IsReturned(){
        setAlertId(ALERT_ID);
        Assertions.assertThat(sendAlertServiceImpl.execute().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }
    
    @Test
    public void test_GivenSendAlertService_WhenAlertFound_ThenEmailSentAnd200IsReturned() throws Exception{
        setAlertId(ALERT_ID);
        EmailAlertEntity alertEntity = new EmailAlertEntity();
        Mockito.when(emailAlertDAO.getById(ALERT_ID)).thenReturn(alertEntity);
        final Response response = sendAlertServiceImpl.execute();
        
        Mockito.verify(emailSender).sendAlertMail(alertEntity);
        Assertions.assertThat(alertEntity.getSentDate()).isNotNull();
        Assertions.assertThat(alertEntity.getStatus()).isEqualTo(EmailAlertEntity.EmailAlertStatus.SENT);
        Assertions.assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }
    
    @Test
    public void test_GivenSendAlertService_WhenEmailSendingFails_ThenEmailStatusIsErrorAnd500IsReturned() throws Exception{
        setAlertId(ALERT_ID);
        EmailAlertEntity alertEntity = new EmailAlertEntity();
        Mockito.when(emailAlertDAO.getById(ALERT_ID)).thenReturn(alertEntity);
        Mockito.doThrow(new Exception()).when(emailSender).sendAlertMail(alertEntity);
        final Response response = sendAlertServiceImpl.execute();
        
        Mockito.verify(emailSender).sendAlertMail(alertEntity);
        Assertions.assertThat(alertEntity.getSentDate()).isNotNull();
        Assertions.assertThat(alertEntity.getStatus()).isEqualTo(EmailAlertEntity.EmailAlertStatus.ERROR);
        Assertions.assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    private void setAlertId(String id) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("id", id);
        Mockito.when(uriInfo.getQueryParameters()).thenReturn(params);
    }
    
}
