/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.CreditAgricoleDataRetriever;

/**
 *
 * @author georgian
 */
public class TestCreditAgricoleRetriever {

    @Test
    public void testRetrieve() throws Exception {
        CreditAgricoleDataRetriever bank = new CreditAgricoleDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("CreditAgricoleBank"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
