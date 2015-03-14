/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.service;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.micsa.exchange.dao.ExchangeRateDAO;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.model.ExchangeRateEntity;

/**
 *
 * @author Alex
 */
@Component
public class MemcacheStoreImpl implements MemcacheStore {
    
    private static Logger log = Logger.getLogger("MemcacheStore");
    
    @Autowired
    private ExchangeRateDAO exchangeRateDAO;
    
    @Override
    public List<ExchangeRateEntity> get(String key) {
        log.fine("Get exchange rates from memcache for day " + key);
        MemcacheService cacheService = MemcacheServiceFactory.getMemcacheService();
        Object cachedList = cacheService.get(key);
        if(cachedList != null) { // if it exists, return it
            return (List<ExchangeRateEntity>)cachedList;
        }
        
        List<ExchangeRateEntity> entities = exchangeRateDAO.getDayExchangeRates(key);
        this.put(key, entities);
        return entities;
    }
    
    @Override
    public List<ExchangeRateEntity> get(String key, CurrencyType currencyType) {
        log.fine("Get exchange rates from memcache for day " + key + " and currency " + currencyType);
        // we take all the list on that day and persist it if not present in cache
        List<ExchangeRateEntity> ratesByDayAndCurrency = this.get(key);
        
        // the list may be altered because it is a mutable copy of the cache
        Iterator<ExchangeRateEntity> iterator = ratesByDayAndCurrency.iterator();
        while(iterator.hasNext()) {
            ExchangeRateEntity newEntity = iterator.next();
            
            if(!newEntity.getCurrencyType().equals(currencyType)) {
                iterator.remove();
            }
        }
        
        return ratesByDayAndCurrency;
    }

    @Override
    public void put(String key, List<ExchangeRateEntity> list) {
        if(list.size()>0){
            log.fine("Put a new entry in the memcache service for day " + key);
            MemcacheService cacheService = MemcacheServiceFactory.getMemcacheService();
            cacheService.put(key, list);
        }
    }

    @Override
    public void remove(String key) {
        log.fine("Remove rates in the memcache service for day " + key);
        MemcacheService cacheService = MemcacheServiceFactory.getMemcacheService();
        cacheService.delete(key);
    }   
    
}
