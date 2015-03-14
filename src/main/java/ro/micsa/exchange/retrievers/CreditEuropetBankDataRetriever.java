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
 * @author georgian
 */
@Component("CreditEuropeBankDataRetriever")
public class CreditEuropetBankDataRetriever implements BankDataRetriever {

    private static String URL = "http://www.crediteurope.ro/curs-valutar/";

    @Override
    public String getBankName() {
        return "CreditEurope";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        String lastUpdateAsString = DateUtils.RO_SDF_HH_mm.format(new Date());
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Document doc = Jsoup.parse(new URL(URL), 5000);

        Elements table = doc.select("#exchange_rates").get(1).select("tr:gt(0)");

        Iterator<Element> currencyIterator = table.iterator();

        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();
            
            String currencyString = currencyElement.attr("class").split(" ")[1];
            String buyString = currencyElement.child(3).text().trim().replaceAll(",", ".");
            String sellString = currencyElement.child(4).text().trim().replaceAll(",", ".");
            
            CurrencyType currencyType = null;
            try {
                currencyType = CurrencyType.valueOf(currencyString);
            } catch (Exception e) {
                continue;
            }

            rates.add(ExchangeRateHelper.addBuyExchangeRate(currencyType, buyString, lastUpdateAsString));
            rates.add(ExchangeRateHelper.addSellExchangeRate(currencyType, sellString, lastUpdateAsString));
        }
        return rates;
    }
}
