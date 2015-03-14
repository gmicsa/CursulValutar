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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Alex
 */
@Component(value="PiraeusDataRetriever")
public class PiraeusBankRetriever implements BankDataRetriever {
    
    public static final String PIRAEUS_BANK_URL = "http://www.piraeusbank.ro/Banca/Unelte/curs_ghiseu.html";

    @Override
    public String getBankName() {
        return "PiraeusBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        
        Document root = Jsoup.parse(new URL(PIRAEUS_BANK_URL), 2000);
        Elements content = root.select("table.trezorerie");
        
        // select line with last update date
        String lastUpdatedDateString = DateUtils.RO_SDF_HH_mm.format(new Date());
        
        Iterator<Element> currencyIterator = content.select("tr:gt(0)").iterator();
        while(currencyIterator.hasNext()) {
            Element newElement = currencyIterator.next();
            
            String currencyName = newElement.select("td:eq(1)").text();
            CurrencyType currencyType = null;
            
            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch(Exception e) {
                continue;   // no problem, continue with next currency
            }
            
            // currency exist in enum, take values for buy and sell and do some replacements
            String currencySellValue = takeDoubleValue(newElement.select("td:eq(3)").text());
            String currencyBuyValue = takeDoubleValue(newElement.select("td:eq(2)").text());
            
            // buy rate
            exchangeRates.add(ExchangeRateHelper.addBuyExchangeRate
                    (currencyType, currencyBuyValue, lastUpdatedDateString));            
            // sell rate
            exchangeRates.add(ExchangeRateHelper.addSellExchangeRate
                    (currencyType, currencySellValue, lastUpdatedDateString));
        }
        
        return exchangeRates;
    }

    private String takeDoubleValue(String text) {
        String[] words = text.split(" ");
        if(words == null || words.length == 0) {
            throw new NumberFormatException();
        }
        
        String doubleValue = words[0].replaceAll(",", ".");
        return doubleValue;
    }
    
}
