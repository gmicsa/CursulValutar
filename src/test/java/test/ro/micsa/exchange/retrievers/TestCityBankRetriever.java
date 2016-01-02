/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ro.micsa.exchange.retrievers.CityBankDataRetriever;

/**
 *
 * @author georgian
 */
@Ignore
public class TestCityBankRetriever {

    @Test
    public void testRetrieve() throws Exception {
        CityBankDataRetriever bank = new CityBankDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("CityBank"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
