/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.IntesaSanPaoloBankRetriever;

/**
 *
 * @author Alex
 */
public class TestIntesaSanPaoloBank {
 
    @Test
    public void testRetrieve() throws Exception {
        IntesaSanPaoloBankRetriever libraBankDataRetriever = new IntesaSanPaoloBankRetriever();
        Assert.assertTrue(libraBankDataRetriever.getBankName().equals("IntesaSanPaoloBank"));
        
        List<ExchangeRate> retrieved = libraBankDataRetriever.getExchangeRates();        
        Assert.assertTrue(retrieved.size() > 0);
    }
}
