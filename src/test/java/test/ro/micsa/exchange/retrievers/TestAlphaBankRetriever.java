/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.AlphaBankDataRetriever;

/**
 *
 * @author georgian
 */
public class TestAlphaBankRetriever {

    @Test
    public void testRetrieve() throws Exception {
        AlphaBankDataRetriever bank = new AlphaBankDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("AlphaBank"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
