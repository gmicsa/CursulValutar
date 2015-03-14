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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Alex
 */
@Component("MilleniumDataRetriver")
public class MilleniumBankRetriever implements BankDataRetriever {
    
    public static final String MILLENIUM_BANK_URL = "http://ind.millenniumbank.ro/ro/Public/Pages/welcome.aspx";
    
    @Override
    public String getBankName() {
        return "MilleniumBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        
        Document root = Jsoup.parse(new URL(MILLENIUM_BANK_URL), 2000);
        Elements content = root.select("div#rates").select("table");
        
        // select line with last update date
        Elements lastUpdateElements = content.select("tfoot tr").select("td");
        String dateTextToParse = lastUpdateElements.text();
        String lastUpdatedDateString = DateUtils.SDF_HH_mm.format(parseMilleniumDateText(dateTextToParse));
        
        Iterator<Element> currencyIterator = content.select("tbody tr").iterator();
        while(currencyIterator.hasNext()) {
            Element newElement = currencyIterator.next();
            
            String currencyName = newElement.select("td:eq(0)").text();
            CurrencyType currencyType = null;
            
            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch(Exception e) {
                continue;   // no problem, continue with next currency
            }
            
            // currency exist in enum, take values for buy and sell and replace ',' with '.'
            String currencySellValue = newElement.select("td:eq(2)").text().replace(',', '.');
            String currencyBuyValue = newElement.select("td:eq(1)").text().replace(',', '.');
            
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
     * parse a Millenium last updated date text, available in romanian locale
     * and transforms it into a date
     * @param dateTextToParse
     * @return
     * @throws ParseException 
     */
    private Date parseMilleniumDateText(String dateTextToParse) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy - HH:mm");
        return format.parse(dateTextToParse);
    }
    
}
