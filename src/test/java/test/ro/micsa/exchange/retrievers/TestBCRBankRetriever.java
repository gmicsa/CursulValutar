/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.BCRBankDataRetriever;

/**
 *
 * @author georgian
 */
public class TestBCRBankRetriever {

    @Test
    public void testRetrieve() throws Exception {
        BCRBankDataRetriever bank = new BCRBankDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("BCR"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
