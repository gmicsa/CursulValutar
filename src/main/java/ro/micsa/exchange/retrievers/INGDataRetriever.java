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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static ro.micsa.exchange.utils.ExchangeRateFactory.*;

@Component("INGDataRetriever")
public class INGDataRetriever implements BankDataRetriever {

    private static String ING_URL = "https://www.ing.ro/ing-in-romania/informatii-utile/curs-valutar";
    private static SimpleDateFormat ING_SDF = new SimpleDateFormat("HH:mm/dd.MM.yyyy");

    @Override
    public String getBankName() {
        return "ING";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        Document doc = Jsoup.parse(new URL(ING_URL), 2000);

        Elements lastUpdateParagraph = doc.select("p.last-update");
        Date lastUpdate = ING_SDF.parse(lastUpdateParagraph.text().substring(20));
        String lastUpdateAsString = DateUtils.SDF_HH_mm.format(lastUpdate);

        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Iterator<Element> currencyIterator = doc.select("#exchange-first-tab .exchange-rates-table tbody tr").iterator();

        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();
            String currencyName = currencyElement.select("td.code").text();
            CurrencyType currencyType;
            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch (Exception e) {
                continue;
            }

            String currencySellValue = currencyElement.select("td.sell").text();
            String currencyBuyValue = currencyElement.select("td.buy").text();

            rates.add(createBuyExchangeRate(currencyType, currencyBuyValue, lastUpdateAsString));
            rates.add(createSellExchangeRate(currencyType, currencySellValue, lastUpdateAsString));
        }

        return rates;
    }
}
