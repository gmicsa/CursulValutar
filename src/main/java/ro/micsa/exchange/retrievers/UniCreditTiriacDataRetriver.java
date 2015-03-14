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
@Component("UniCreditTiriacDataRetriver")
public class UniCreditTiriacDataRetriver implements BankDataRetriever {
    
    public static final String UNICREDIT_TIRIAC_URL = "http://www.unicredit-tiriac.ro/exchp";
    private static final int DATE_UPDATED_BEGIN_INDEX = 19;

    @Override
    public String getBankName() {
        return "UniCreditTiriac";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        
        Document root = Jsoup.parse(new URL(UNICREDIT_TIRIAC_URL), 2000);
        Elements content = root.select("div.c-2-2");
        
        // select line with last update date
        Elements lastUpdateElements = content.select("h2:eq(0)");
        String dateTextToParse = lastUpdateElements.text().substring(DATE_UPDATED_BEGIN_INDEX); 
        String lastUpdatedDateString = DateUtils.SDF_HH_mm.format(parseUniCreditTiriacDateText(dateTextToParse));
        
        Iterator<Element> currencyIterator = content.select("table").select("tr:gt(14)").iterator();
        while(currencyIterator.hasNext()) {
            Element newElement = currencyIterator.next();
            
            // we must replace all '*' literal characters with empty strings
            String currencyName = newElement.select("td:eq(0)").text().replaceAll("\\*", "");
            CurrencyType currencyType = null;
            
            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch(Exception e) {
                continue;   // no problem, continue with next currency
            }
            
            // currency exist in enum, take values for buy and sell
            String currencySellValue = newElement.select("td:eq(2)").text();
            String currencyBuyValue = newElement.select("td:eq(1)").text();
            
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
     * parse a UniCreditTiriac last updated date text, available in romanian locale
     * and transforms it into a date
     * @param dateTextToParse
     * @return
     * @throws ParseException 
     */
    private Date parseUniCreditTiriacDateText(String dateTextToParse) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(dateTextToParse);
    }
}
