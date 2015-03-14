/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.ProCreditBankRetriever;

/**
 *
 * @author Alex
 */
public class TestProCreditRetriever {
    
    @Test
    public void testRetrieve() throws Exception {
        ProCreditBankRetriever piraeusRetriever = new ProCreditBankRetriever();
        Assert.assertTrue(piraeusRetriever.getBankName().equals("ProCreditBank"));
        
        List<ExchangeRate> retrieved = piraeusRetriever.getExchangeRates();        
        Assert.assertTrue(retrieved.size() > 0);
    }
}
