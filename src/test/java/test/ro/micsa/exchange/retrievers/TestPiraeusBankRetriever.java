/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.PiraeusBankRetriever;

/**
 *
 * @author Alex
 */
public class TestPiraeusBankRetriever {
    
    @Test
    public void testRetrieve() throws Exception {
        PiraeusBankRetriever piraeusRetriever = new PiraeusBankRetriever();
        Assert.assertTrue(piraeusRetriever.getBankName().equals("PiraeusBank"));
        
        List<ExchangeRate> retrieved = piraeusRetriever.getExchangeRates();        
        Assert.assertTrue(retrieved.size() > 0);
    }
}
