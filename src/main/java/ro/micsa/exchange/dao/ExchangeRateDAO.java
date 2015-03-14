/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.dao;

import java.util.List;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.model.ExchangeRateEntity;

/**
 *
 * @author georgian
 */

public interface ExchangeRateDAO {

    List<ExchangeRateEntity> getDayExchangeRates(String day);
    
    List<ExchangeRateEntity> getDayCurrencyExchangeRates(String day, CurrencyType currencyType);

    ExchangeRateEntity saveExchangeRate(ExchangeRateEntity exchangeRate);
    
    ExchangeRateEntity updateExchangeRate(ExchangeRateEntity exchangeRate);
    
}
