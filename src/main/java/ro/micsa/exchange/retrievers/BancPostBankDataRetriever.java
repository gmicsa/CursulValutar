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
@Component("BancpostBankDataRetriever")
public class BancPostBankDataRetriever implements BankDataRetriever {

    private static String URL = "http://www.bancpost.ro/Bancpost/Financiare/Istoric-Curs-Valutar";

    @Override
    public String getBankName() {
        return "Bancpost";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        String lastUpdateAsString = DateUtils.RO_SDF_HH_mm.format(new Date());
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Document doc = Jsoup.parse(new URL(URL), 2000);

        Elements table = doc.select(".forex tbody").select("tr");

        Iterator<Element> currencyIterator = table.iterator();

        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();
            
            String currencyString = currencyElement.attr("class").toUpperCase();
            String buyString = currencyElement.child(2).text().split(" ")[0].replaceAll(",", ".");
            String sellString = currencyElement.child(3).text().split(" ")[0].replaceAll(",", ".");
            
            CurrencyType currencyType = null;
            try {
                currencyType = CurrencyType.valueOf(currencyString);
            } catch (Exception e) {
                continue;
            }

            rates.add(ExchangeRateFactory.createBuyExchangeRate(currencyType, buyString, lastUpdateAsString));
            rates.add(ExchangeRateFactory.createSellExchangeRate(currencyType, sellString, lastUpdateAsString));
        }
        return rates;
    }
}
