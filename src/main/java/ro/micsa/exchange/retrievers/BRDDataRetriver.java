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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        Elements content = root.select("div.exchange-rates-table");
        
        // select line with last update date
        Elements lastUpdateElements = root.select("div.input-date-piker");
        String dateTextToParse = lastUpdateElements.select("input").attr("value");        
        String lastUpdatedDateString = DateUtils.SDF_HH_mm.format(parseBRDDateText(dateTextToParse));
          
        Iterator<Element> currencyIterator = content.select("table:eq(0)").select("tr:gt(1)").iterator();
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
            String currencyBuyValue = newElement.select("td:eq(5)").text();
            String currencySellValue = newElement.select("td:eq(6)").text();
            
            // buy rate
            exchangeRates.add(ExchangeRateHelper.addBuyExchangeRate
                    (currencyType, currencyBuyValue, lastUpdatedDateString));            
            // sell rate
            exchangeRates.add(ExchangeRateHelper.addSellExchangeRate
                    (currencyType, currencySellValue, lastUpdatedDateString));
        }
        
        return exchangeRates;
    }

    /**
     * parse a BRD last updated date text, available in romanian locale
     * and transforms it into a date
     * @param dateTextToParse
     * @return
     * @throws ParseException 
     */
    private Date parseBRDDateText(String dateTextToParse) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", new Locale("ro","RO"));
        return format.parse(dateTextToParse);
    }
    
}
