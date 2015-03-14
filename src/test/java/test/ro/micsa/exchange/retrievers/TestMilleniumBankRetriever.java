/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.MilleniumBankRetriever;

/**
 *
 * @author Alex
 */
public class TestMilleniumBankRetriever {
    
    @Test
    public void testRetrieve() throws Exception {
        MilleniumBankRetriever milleniumBankRetriever = new MilleniumBankRetriever();
        Assert.assertTrue(milleniumBankRetriever.getBankName().equals("MilleniumBank"));
        
        List<ExchangeRate> retrieved = milleniumBankRetriever.getExchangeRates();        
        Assert.assertTrue(retrieved.size() > 0);
    }
}
