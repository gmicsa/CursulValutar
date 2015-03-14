/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.BancaCarpaticaDataRetriever;

/**
 *
 * @author georgian
 */
public class TestBancaCarpaticaRetriever {

    @Test
    public void testRetrieve() throws Exception {
        BancaCarpaticaDataRetriever bank = new BancaCarpaticaDataRetriever();
        Assert.assertTrue(bank.getBankName().equals("BancaCarpatica"));
        List<ExchangeRate> listExchange = bank.getExchangeRates();
        Assert.assertTrue(listExchange.size() > 0);
    }
}
