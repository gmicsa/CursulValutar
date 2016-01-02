/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.VolksbankRetriever;

/**
 *
 * @author Alex
 */

@Ignore
public class TestVolksbankRetriever {
    
     @Test
    public void testRetrieve() throws Exception {
        VolksbankRetriever piraeusRetriever = new VolksbankRetriever();
        Assert.assertTrue(piraeusRetriever.getBankName().equals("Volksbank"));
        
        List<ExchangeRate> retrieved = piraeusRetriever.getExchangeRates();        
        Assert.assertTrue(retrieved.size() > 0);
    }
}
