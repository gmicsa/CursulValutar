/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.UniCreditTiriacDataRetriver;

/**
 *
 * @author Alex
 */
public class TestUniCreditTiriacRetriever {
    
    @Test
    public void testRetrieve() throws Exception {
        UniCreditTiriacDataRetriver uniCreditTiriacDataRetriever = new UniCreditTiriacDataRetriver();
        Assert.assertTrue(uniCreditTiriacDataRetriever.getBankName().equals("UniCreditTiriac"));
        
        List<ExchangeRate> retrieved = uniCreditTiriacDataRetriever.getExchangeRates();        
        Assert.assertTrue(retrieved.size() > 0);
    }
}
