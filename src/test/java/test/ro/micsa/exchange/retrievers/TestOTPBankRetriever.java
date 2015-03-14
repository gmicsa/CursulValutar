/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.OTPBankRetriever;

/**
 *
 * @author Alex
 */
public class TestOTPBankRetriever {
    
    @Test
    public void testRetrieve() throws Exception {
        OTPBankRetriever otpBankRetriever = new OTPBankRetriever();
        Assert.assertTrue(otpBankRetriever.getBankName().equals("OTPBank"));
        
        List<ExchangeRate> retrieved = otpBankRetriever.getExchangeRates();        
        Assert.assertTrue(retrieved.size() > 0);
    }
}
