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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author georgian
 */
@Component("INGDataRetriever")
public class INGDataRetriever implements BankDataRetriever {

    private static String ING_URL = "http://www.ing.ro/ing/ro/curs-valutar.html";
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm/dd.MM.yyyy");

    @Override
    public String getBankName() {
        return "ING";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Document doc = Jsoup.parse(new URL(ING_URL), 2000);

        Elements tabs = doc.select(".tabbertab:eq(2)");

        Elements lastUpdateElems = tabs.select("div.more[align=center]");

        Date lastUpdate = sdf.parse(lastUpdateElems.text().substring(19));
        String lastUpdateAsString = DateUtils.SDF_HH_mm.format(lastUpdate);

        Iterator<Element> currencyIterator = tabs.select("div.liner:gt(1)").iterator();

        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();
            String currencyName = currencyElement.select("div.leftsider").get(0).text();
            CurrencyType currencyType = null;
            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch (Exception e) {
                continue;
            }

            String currencySellValue = currencyElement.select("div div.leftsider").get(0).text().trim().replaceAll("\u00a0", "");
            String currencyBuyValue = currencyElement.select("div div.leftsider").get(1).text().trim().replaceAll("\u00a0", "");
            
            rates.add(ExchangeRateHelper.addBuyExchangeRate(currencyType, currencyBuyValue, lastUpdateAsString));            
            rates.add(ExchangeRateHelper.addSellExchangeRate(currencyType, currencySellValue, lastUpdateAsString));
        }
        return rates;
    }
}
