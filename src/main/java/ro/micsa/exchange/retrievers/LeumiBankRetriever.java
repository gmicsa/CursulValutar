/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.retrievers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.utils.DateUtils;
import ro.micsa.exchange.utils.ExchangeRateFactory;

import java.util.*;

/**
 *
 * @author Alex
 */
@Component("LeumiBankRetriever")
public class LeumiBankRetriever implements BankDataRetriever {
 
    public static final String LEUMI_BANK_URL = "https://www.leumi.ro/leumi-ro/cursuri.html";
    
    private static Map<String, String> currencyAssociates;
    
    static {
        currencyAssociates = new HashMap<String, String>() {
            {
                put("Euro", "EUR");
                put("Dolar USA", "USD");
                put("Lira sterlina", "GBP");
                put("Franc elvetian", "CHF");
                put("Forint maghiar (100 unit.)", "HUF");
            }
        };
    }
    
    @Override
    public String getBankName() {
        return "LeumiBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        
        Connection connection = Jsoup.connect(LEUMI_BANK_URL);
        connection.header("User-Agent", "Mozilla/5.0 (Ubuntu; X11; Linux x86_64; rv:8.0) Gecko/20100101 Firefox/8.0");
        connection.timeout(2000);
        Document root = connection.get();
        Element tableAllExchanges = root.select("table").get(2);

        // select line with last update date
        String lastUpdatedDateString = DateUtils.RO_SDF_HH_mm.format(new Date());

        Iterator<Element> buyExchangesElements = tableAllExchanges.select("tr:lt(6)").iterator();
        Iterator<Element> sellExchangesElements = tableAllExchanges.select("tr:gt(6)").iterator();

        addExchangeRates(exchangeRates, lastUpdatedDateString, buyExchangesElements, true);
        addExchangeRates(exchangeRates, lastUpdatedDateString, sellExchangesElements, false);
        
        return exchangeRates;
    }
    
    private void addExchangeRates(List<ExchangeRate> exchangeRates, String lastUpdatedDateString,
            Iterator<Element> exchangeElements, boolean buyType) {
        
        boolean firstRow = true;
        while(exchangeElements.hasNext()) {
            Element newExchange = exchangeElements.next();
            if(firstRow && buyType) {
                firstRow = !firstRow;
                continue;   // skip parsing the first row for buy
            }
            
            String currencyName = newExchange.select("td:eq(1)").select("font").text();
            CurrencyType currencyType = null;
            
            try {
                currencyType = CurrencyType.valueOf(currencyAssociates.get(currencyName));
            } catch(Exception e) {
                continue;   // no problem, continue with next currency
            }
            String currencyValue = newExchange.select("td:eq(2)")
                    .select("div font b").text();
            
            if(buyType) {
                exchangeRates.add(ExchangeRateFactory.createBuyExchangeRate
                    (currencyType, currencyValue, lastUpdatedDateString));
            } else {
                exchangeRates.add(ExchangeRateFactory.createSellExchangeRate
                    (currencyType, currencyValue, lastUpdatedDateString));
            }
            
        }
    }
}
