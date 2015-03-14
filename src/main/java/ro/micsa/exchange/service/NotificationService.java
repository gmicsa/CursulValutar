/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.service;

import ro.micsa.exchange.dto.Notification;

import javax.ws.rs.core.Response;

/**
 *
 * @author georgian
 */

public interface NotificationService {

    // check security accessToken, return 404 if notification not found
    Response getNotification(String accessToken);

    //create or update notification, also doing proper validation
    Response saveNotification(String accessToken, Notification notification);
    
}
