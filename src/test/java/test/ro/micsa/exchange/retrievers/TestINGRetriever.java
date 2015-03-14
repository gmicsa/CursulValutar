/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.INGDataRetriever;

/**
 *
 * @author georgian
 */
public class TestINGRetriever {

    @Test
    public void testRetrieve() throws Exception {
        INGDataRetriever bank = new INGDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("ING"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
