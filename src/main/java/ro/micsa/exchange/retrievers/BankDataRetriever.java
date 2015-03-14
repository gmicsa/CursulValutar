/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.retrievers;

import java.util.List;
import ro.micsa.exchange.dto.ExchangeRate;

/**
 *
 * @author georgian
 */
public interface BankDataRetriever {
    
    String getBankName();
    
    List<ExchangeRate> getExchangeRates() throws Exception;
    
}
