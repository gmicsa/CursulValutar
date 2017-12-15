/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.retrievers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
 *
 * S-a eliminat Volksbank, deoarece de la 1 ianuarie 2016 a devenit aceeasi banca cu Banca Transilvania
 * @see http://www.volksbank.ro/
 *
 */
//@Component(value="VolksbankDataRetriever")
public class VolksbankRetriever implements BankDataRetriever {

    public static final String VOLKSBANK_BANK_URL = "http://www.volksbank.ro/ro/AfisareCursValutar";
    
    @Override
    public String getBankName() {
        return "Volksbank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        
        Document root = Jsoup.parse(new URL(VOLKSBANK_BANK_URL), 2000);
        Elements content = root.select("table#tr-no-line").select("tr:eq(2)")
                .select("td:eq(1)").select("tr:eq(1)").select("table");
        
        // select line with last update date
        String lastUpdatedDateString = DateUtils.RO_SDF_HH_mm.format(new Date());
        
        Iterator<Element> currencyIterator = content.select("tr:gt(0)").iterator();
        while(currencyIterator.hasNext()) {
            Element newElement = currencyIterator.next();
            
            String currencyName = newElement.select("td:eq(0)").select("span").text().split("/")[0];
            CurrencyType currencyType = null;
            
            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch(Exception e) {
                continue;   // no problem, continue with next currency
            }
            
            // currency exist in enum, take values for buy and sell and do some replacements
            String currencySellValue = newElement.select("td:eq(2)").select("span").text().replace(",", ".");
            String currencyBuyValue = newElement.select("td:eq(1)").select("span").text().replace(",", ".");
            
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
