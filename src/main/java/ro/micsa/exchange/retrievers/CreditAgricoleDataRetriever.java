/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.retrievers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
@Component("CreditAgricoleDataRetriever")
public class CreditAgricoleDataRetriever implements BankDataRetriever {

    private static String URL = "http://credit-agricole.ro/curs-valutar";

    @Override
    public String getBankName() {
        return "CreditAgricoleBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Document doc = Jsoup.parse(new URL(URL), 2000);

        String lastUpdateAsString = DateUtils.RO_SDF_HH_mm.format(new Date());

        Iterator<Element> currencyIterator = doc.select(".content_box").select("table").select("tr:gt(0)").iterator();

        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();
            String currencyName = currencyElement.child(0).select("img").attr("alt");
            CurrencyType currencyType = null;
            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch (Exception e) {
                continue;
            }

            String currencyBuyValue = currencyElement.child(1).text();
            String currencySellValue = currencyElement.child(2).text();
            
            rates.add(ExchangeRateFactory.createBuyExchangeRate(currencyType, currencyBuyValue, lastUpdateAsString));
            rates.add(ExchangeRateFactory.createSellExchangeRate(currencyType, currencySellValue, lastUpdateAsString));
        }
        return rates;
    }
}
