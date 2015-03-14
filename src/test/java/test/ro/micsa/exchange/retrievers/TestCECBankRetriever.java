/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.CECBankDataRetriever;

/**
 *
 * @author georgian
 */
public class TestCECBankRetriever {

    @Test
    public void testRetrieve() throws Exception {
        CECBankDataRetriever bank = new CECBankDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("CECBank"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
