/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.utils;

import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.dto.TransactionType;

public class ExchangeRateFactory {
    
    public static ExchangeRate createBuyExchangeRate(CurrencyType type, String doubleValue, String lastModified) {
        ExchangeRate rateBuy = new ExchangeRate();
        rateBuy.setCurrencyType(type);
        rateBuy.setLastChangedAt(lastModified);
        rateBuy.setTransactionType(TransactionType.BUY);
        rateBuy.setValue(Double.parseDouble(doubleValue));
        
        multiplyWith100HUFJPYIfNeeded(rateBuy);
        
        return rateBuy;
    }
    
    public static ExchangeRate createSellExchangeRate(CurrencyType type, String doubleValue, String lastModified) {
        ExchangeRate rateSell = new ExchangeRate();
        rateSell.setCurrencyType(type);
        rateSell.setLastChangedAt(lastModified);
        rateSell.setTransactionType(TransactionType.SELL);
        rateSell.setValue(Double.parseDouble(doubleValue));
            
        multiplyWith100HUFJPYIfNeeded(rateSell);
        
        return rateSell;
    }
    
    // Dirty Fix: some banks give the rate for 100 Units others for one unit for HUF and JPY
    // We will use 100 units rates as BNR does this
    private static void multiplyWith100HUFJPYIfNeeded(ExchangeRate rate){
        if(rate.getCurrencyType().equals(CurrencyType.HUF)||rate.getCurrencyType().equals(CurrencyType.JPY)){
            if(rate.getValue() < 1){
                rate.setValue(rate.getValue() * 100);
            }
        }
    }
}
