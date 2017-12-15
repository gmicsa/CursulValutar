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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Alex
 */
@Component("LibraBankDataRetriever")
public class LibraBankRetriever implements BankDataRetriever {
    
    public static final String LIBRA_BANK_URL = "http://www.librabank.ro/Curs_valutar/";
    
    @Override
    public String getBankName() {
        return "LibraBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        
        Document root = Jsoup.parse(new URL(LIBRA_BANK_URL), 2000);
        Elements content = root.select("div.left_side_container").select("table:eq(2)");
        
        // select line with last update date
        String lastUpdatedDateString = DateUtils.RO_SDF_HH_mm.format(new Date());

        Iterator<Element> currencyIterator = content.select("tr:gt(1)").iterator();
        while(currencyIterator.hasNext()) {
            Element newElement = currencyIterator.next();
            
            Elements el = newElement.select("td:eq(0)").select("span");
            
            // we must replace all '*' literal characters with empty strings
            String currencyName = el.text().replaceAll("[\\d ]+", "");
            CurrencyType currencyType = null;
            
            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch(Exception e) {
                continue;   // no problem, continue with next currency
            }
            
            // currency exist in enum, take values for buy and sell and replace ',' with '.'
            String currencySellValue = newElement.select("td:eq(2)").select("span").text().replace(',', '.');
            String currencyBuyValue = newElement.select("td:eq(1)").select("span").text().replace(',', '.');
            
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
