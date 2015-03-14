/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.BNRDataRetriever;

/**
 *
 * @author georgian
 */
public class TestBNRRetriver {
    
    @Test
    public void testRetrieve() throws Exception {
        BNRDataRetriever bnr = new BNRDataRetriever();
        Assert.assertTrue(bnr.getBankName().equals("BNR"));
        Assert.assertTrue(bnr.getExchangeRates().size() > 0);
    }
}
