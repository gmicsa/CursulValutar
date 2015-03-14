/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.GarantiBankDataRetriever;

/**
 *
 * @author georgian
 */
public class TestGarantiBankRetriever {

    @Test
    public void testRetrieve() throws Exception {
        GarantiBankDataRetriever bank = new GarantiBankDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("GarantiBank"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
