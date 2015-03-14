/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.service;

import java.util.List;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.model.ExchangeRateEntity;

/**
 *
 * @author Alex
 */
public interface MemcacheStore {
    
    
    public List<ExchangeRateEntity> get(String key);
    
    public List<ExchangeRateEntity> get(String key, CurrencyType currencyType);
    
    public void put(String key, List<ExchangeRateEntity> list);
    
    public void remove(String key);
}
