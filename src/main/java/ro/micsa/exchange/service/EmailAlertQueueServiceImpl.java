/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.service;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 *
 * @author Georgian
 */
@Service
public class EmailAlertQueueServiceImpl implements EmailAlertQueueService {
    
    private static final Logger log = Logger.getLogger("ExchangeRateService");

    @Override
    public void queueAlert(String alertIdAsString) {
        Queue queue = QueueFactory.getQueue("alerts-queue");
        log.info("Add alert to queue with alert id " + alertIdAsString);
        queue.add(TaskOptions.Builder.withUrl("/services/send_alert").method(TaskOptions.Method.GET).param("id", alertIdAsString));
    }
}
