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
@Component(value="RaiffeisenDataRetriever")
public class RaiffeisenBankRetriever implements BankDataRetriever {

    public static final String RAIFFEISEN_BANK_URL = "http://www.raiffeisen.ro/wps/portal/internet/curs-valutar";
    private static final int DATE_UPDATED_BEGIN_INDEX = 20;
    
    @Override
    public String getBankName() {
        return "RaiffeisenBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        
        Document root = Jsoup.parse(new URL(RAIFFEISEN_BANK_URL), 2000);
        Elements content = root.select("div.rzbContentTextNormal");
        
        // select line with last update date
        Elements lastUpdateElements = content.select("div:eq(0)");
        String dateTextToParse = lastUpdateElements.text().substring(DATE_UPDATED_BEGIN_INDEX);
        String lastUpdatedDateString = DateUtils.SDF_HH_mm.format(parseRaiffeisenDateText(dateTextToParse));
        
        Iterator<Element> currencyIterator = content.select("table:eq(3)").select("tr:gt(1)").iterator();

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
            String currencySellValue = newElement.select("td:eq(4)").text();
            String currencyBuyValue = newElement.select("td:eq(3)").text();

            // buy rate
            exchangeRates.add(ExchangeRateFactory.createBuyExchangeRate
                    (currencyType, currencyBuyValue, lastUpdatedDateString));
            // sell rate
            exchangeRates.add(ExchangeRateFactory.createSellExchangeRate
                    (currencyType, currencySellValue, lastUpdatedDateString));
        }
        
        return exchangeRates;
    }
   
    /**
     * parse a Raiffeisen last updated date text, available in romanian locale
     * and transforms it into a date
     * @param dateTextToParse
     * @return
     * @throws ParseException 
     */
    private Date parseRaiffeisenDateText(String dateTextToParse) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return format.parse(dateTextToParse);
    }
}
