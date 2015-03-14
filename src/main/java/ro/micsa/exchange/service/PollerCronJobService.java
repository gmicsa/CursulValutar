/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.service;

import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.springframework.stereotype.Component;

/**
 *
 * @author georgian.micsa
 */
@Component
@Path("/poll/")
public class PollerCronJobService implements RESTService{

    @GET
    @Override
    public Response execute() {
        return Response.ok(new Date().toString()).build();
    }
    
}
