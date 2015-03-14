/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.retrievers.BancaRomaneascaDataRetriever;

/**
 *
 * @author georgian
 */
public class TestBancaRomaneascaRetriever {

    @Test
    public void testRetrieve() throws Exception {
        BancaRomaneascaDataRetriever bank = new BancaRomaneascaDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("BancaRomaneasca"));
        Assert.assertTrue(bank.getExchangeRates().size() > 0);
    }
}
