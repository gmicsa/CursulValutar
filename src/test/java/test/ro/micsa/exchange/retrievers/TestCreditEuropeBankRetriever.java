/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.CreditEuropetBankDataRetriever;

/**
 *
 * @author georgian
 */
public class TestCreditEuropeBankRetriever {

    @Test
    public void testRetrieve() throws Exception {
        CreditEuropetBankDataRetriever bank = new CreditEuropetBankDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("CreditEurope"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
