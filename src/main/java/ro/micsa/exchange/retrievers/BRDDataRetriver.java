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
import ro.micsa.exchange.utils.ExchangeRateFactory;

import java.net.URL;
import java.util.*;

/**
 *
 * @author Alex
 */
@Component(value="BRDDataRetriver")
public class BRDDataRetriver implements BankDataRetriever {
    
    public static final String BRD_URL = "http://www.brd.ro/instrumente-utile/curs-valutar";

    @Override
    public String getBankName() {
        return "BRD";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        
        Document root = Jsoup.parse(new URL(BRD_URL), 2000);
        Elements content = root.select("div.exchange-rates-table").select("table").select("tr:gt(1)");

        // select line with last update date
        String lastUpdatedDateString = DateUtils.SDF_HH_mm.format(new Date());

        Iterator<Element> currencyIterator = content.iterator();
        while(currencyIterator.hasNext()) {
            Element newElement = currencyIterator.next();
            String currencyName = newElement.select("td:eq(1)").text();
            CurrencyType currencyType = null;

            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch(Exception e) {
                continue;   // no problem, continue with next currency
            }

            // currency exist in enum, take values for buy and sell
            String currencyBuyValue = newElement.select("td:eq(2)").text();
            String currencySellValue = newElement.select("td:eq(3)").text();
            
            // buy rate
            exchangeRates.add(ExchangeRateFactory.createBuyExchangeRate
                    (currencyType, currencyBuyValue, lastUpdatedDateString));            
            // sell rate
            exchangeRates.add(ExchangeRateFactory.createSellExchangeRate
                    (currencyType, currencySellValue, lastUpdatedDateString));
        }
        
        return exchangeRates;
    }

}
