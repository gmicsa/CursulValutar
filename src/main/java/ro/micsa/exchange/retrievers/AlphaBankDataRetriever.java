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
 * @author georgian
 */
@Component("AlphaBankDataRetriever")
public class AlphaBankDataRetriever implements BankDataRetriever {

    private static String URL = "https://www.alphabank.ro/ro/rate/rate_si_dobanzi.php";

    @Override
    public String getBankName() {
        return "AlphaBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        String lastUpdateAsString = DateUtils.RO_SDF_HH_mm.format(new Date());
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Document doc = Jsoup.parse(new URL(URL), 2000);

        Elements table = doc.select("table table").get(7).select("tr[height=18]");

        Iterator<Element> currencyIterator = table.iterator();

        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();
            
            String currencyString = currencyElement.child(0).text();
            String buyString = currencyElement.child(4).text();
            if(buyString.equals("-")){
                continue;
            }
            String sellString = currencyElement.child(5).text();
            
            CurrencyType currencyType = null;
            try {
                currencyType = CurrencyType.valueOf(currencyString.substring(currencyString.indexOf("(")+1, currencyString.indexOf(")")));
            } catch (Exception e) {
                continue;
            }
            
            rates.add(ExchangeRateFactory.createBuyExchangeRate(currencyType, buyString, lastUpdateAsString));
            rates.add(ExchangeRateFactory.createSellExchangeRate(currencyType, sellString, lastUpdateAsString));
        }
        return rates;
    }
}
