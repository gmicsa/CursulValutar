/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.retrievers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.utils.DateUtils;
import ro.micsa.exchange.utils.ExchangeRateHelper;

import java.net.URL;
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
        
        Document root = Jsoup.parse(new URL(LEUMI_BANK_URL), 2000);
        Elements tableAllExchanges = root.select("table");   //.get(2);

       // for(Element e : tableAllExchanges)
        System.out.println(root);
        
        // select line with last update date
//        String lastUpdatedDateString = DateUtils.RO_SDF_HH_mm.format(new Date());
//
//        Iterator<Element> buyExchangesElements = tableAllExchanges.select("tr:lt(6)").iterator();
//        Iterator<Element> sellExchangesElements = tableAllExchanges.select("tr:gt(6)").iterator();
//
//        addExchangeRates(exchangeRates, lastUpdatedDateString, buyExchangesElements, true);
//        addExchangeRates(exchangeRates, lastUpdatedDateString, sellExchangesElements, false);
        
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
                exchangeRates.add(ExchangeRateHelper.addBuyExchangeRate
                    (currencyType, currencyValue, lastUpdatedDateString));
            } else {
                exchangeRates.add(ExchangeRateHelper.addSellExchangeRate
                    (currencyType, currencyValue, lastUpdatedDateString));
            }
            
        }
    }
}
