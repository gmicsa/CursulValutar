/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.LeumiBankRetriever;

/**
 *
 * @author Alex
 */
public class TestLeumiBankRetriever {
    
    @Test
    public void testRetrieve() throws Exception {
        LeumiBankRetriever leumiBankDataRetriever = new LeumiBankRetriever();
        Assert.assertTrue(leumiBankDataRetriever.getBankName().equals("LeumiBank"));
        
        List<ExchangeRate> retrieved = leumiBankDataRetriever.getExchangeRates();        
        Assert.assertTrue(retrieved.size() > 0);
    }
}
