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
import java.util.*;

/**
 *
 * @author Alex
 */
@Component(value="BRDDataRetriver")
public class BRDDataRetriever implements BankDataRetriever {
    
    private static final String BRD_URL = "https://www.brd.ro/curs-valutar-si-dobanzi-de-referinta";

    @Override
    public String getBankName() {
        return "BRD";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        
        Document root = Jsoup.parse(new URL(BRD_URL), 2000);
        Elements content = root.select("div#tabAccountExchangeRates").select("div.tabel");

        Elements currencyCodes = content.select("div:eq(3)").select("p:gt(0)");
        Iterator<Element> currencyBuyIterator = content.select("div:eq(5)").select("p:gt(0)").iterator();
        Iterator<Element> currencySellIterator = content.select("div:eq(6)").select("p:gt(0)").iterator();

        // select line with last update date
        String lastUpdatedDateString = DateUtils.SDF_HH_mm.format(new Date());

        for (Element newElement : currencyCodes) {
            String currencyName = newElement.text();
            String currencyBuyValue = currencyBuyIterator.next().text();
            String currencySellValue = currencySellIterator.next().text();
            CurrencyType currencyType;

            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch (Exception e) {
                continue;   // no problem, continue with next currency
            }

            // currency exist in enum, take values for buy and sell
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
