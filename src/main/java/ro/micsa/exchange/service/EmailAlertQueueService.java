/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.service;

/**
 *
 * @author Georgian
 */
public interface EmailAlertQueueService {
    
    public void queueAlert(String alertIdAsString);
    
}
