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
@Component(value="ProCreditDataRetriever")
public class ProCreditBankRetriever implements BankDataRetriever {
    
    public static final String PROCREDIT_BANK_URL = "http://www.procreditbank.ro/ro/curs-valutar";
    public static final String NOT_AVAILABLE = "N/A";

    @Override
    public String getBankName() {
        return "ProCreditBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        
        Document root = Jsoup.parse(new URL(PROCREDIT_BANK_URL), 2000);
        Elements content = root.select("table.licitatii");
        
        // select line with last update date
        Elements lastUpdateElements = content.select("tr:eq(5)");
        String dateTextToParse = lastUpdateElements.select("td:eq(9)").text()+ " " +
                lastUpdateElements.select("td:eq(10)").text();
        String lastUpdatedDateString = DateUtils.SDF_HH_mm.format(parseProCreditDateText(dateTextToParse));
        
        Iterator<Element> currencyIterator = content.select("tr:gt(2)").iterator();
        while(currencyIterator.hasNext()) {
            Element newElement = currencyIterator.next();
            
            String currencyName = newElement.select("td:eq(0)").text();
            CurrencyType currencyType = null;
            
            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch(Exception e) {
                continue;   // no problem, continue with next currency
            }
            
            // currency exist in enum, take values for buy and sell
            String currencySellValue = newElement.select("td:eq(8)").text();
            String currencyBuyValue = newElement.select("td:eq(7)").text();
            
            if(currencySellValue.contains(NOT_AVAILABLE) || 
                    currencyBuyValue.contains(NOT_AVAILABLE)) {
                continue;
            }
            
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
     * parse a ProCredit last updated date text, available in romanian locale
     * and transforms it into a date
     * @param dateTextToParse
     * @return
     * @throws ParseException 
     */
    private Date parseProCreditDateText(String dateTextToParse) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return format.parse(dateTextToParse);
    }
}
