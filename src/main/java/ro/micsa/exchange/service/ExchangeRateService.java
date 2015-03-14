/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.service;

import javax.ws.rs.core.Response;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.ExchangeRate;

/**
 *
 * @author georgian
 */

public interface ExchangeRateService {
    

    Response getDayExchangeRates(String day);
    
    Response getDayCurrencyExchangeRates(String day, CurrencyType currencyType);

    Response getHistoryCurrencyExchangeRates(String day, CurrencyType currencyType, String bank);

    Response saveExchangeRate(ExchangeRate exchangeRate);
    
}
