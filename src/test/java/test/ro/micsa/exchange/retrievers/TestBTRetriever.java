/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.BTDataRetriever;

/**
 *
 * @author georgian
 */
public class TestBTRetriever {

    @Test
    public void testRetrieve() throws Exception {
        BTDataRetriever bank = new BTDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("BT"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
