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
@Component("BCRBankDataRetriever")
public class BCRBankDataRetriever implements BankDataRetriever {

    private static String URL = "http://www.bcr.ro/ro/curs-valutar";

    @Override
    public String getBankName() {
        return "BCR";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        String lastUpdateAsString = DateUtils.RO_SDF_HH_mm.format(new Date());
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Document doc = Jsoup.parse(new URL(URL), 2000);

        Elements table = doc.select(".glaze.fullsize").select("tr:gt(0)");

        Iterator<Element> currencyIterator = table.iterator();

        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();
            
            String currencyString = currencyElement.child(0).child(0).attr("src").substring(23,26);
            String buyString = currencyElement.child(2).text().trim().replaceAll(",", ".");
            String sellString = currencyElement.child(3).text().trim().replaceAll(",", ".");;
            
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
