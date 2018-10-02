/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.BRDDataRetriever;

/**
 *
 * @author Alex
 */
public class TestBRDRetriever {
    
    @Test
    public void testRetrieve() throws Exception {
        BRDDataRetriever brdDataRetriever = new BRDDataRetriever();
        Assert.assertTrue(brdDataRetriever.getBankName().equals("BRD"));
        
        List<ExchangeRate> retrieved = brdDataRetriever.getExchangeRates();        
        Assert.assertTrue(retrieved.size() > 0);
    }
}
