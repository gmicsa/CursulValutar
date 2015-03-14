/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.RaiffeisenBankRetriever;

/**
 *
 * @author Alex
 */
public class TestRaiffeisenBankRetriever {
    
    @Test
    public void testRetrieve() throws Exception {
        RaiffeisenBankRetriever piraeusRetriever = new RaiffeisenBankRetriever();
        Assert.assertTrue(piraeusRetriever.getBankName().equals("RaiffeisenBank"));
        
        List<ExchangeRate> retrieved = piraeusRetriever.getExchangeRates();        
        Assert.assertTrue(retrieved.size() > 0);
    }
}
