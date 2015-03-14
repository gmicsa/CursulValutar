/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.BancPostBankDataRetriever;

/**
 *
 * @author georgian
 */
public class TestBancpostBankRetriever {

    @Test
    public void testRetrieve() throws Exception {
        BancPostBankDataRetriever bank = new BancPostBankDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("Bancpost"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
