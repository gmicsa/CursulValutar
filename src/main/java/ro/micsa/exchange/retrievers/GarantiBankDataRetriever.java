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
import java.util.List;

/**
 *
 * @author georgian
 */
@Component("GarantiBankDataRetriever")
public class GarantiBankDataRetriever implements BankDataRetriever {

    private static String URL = "https://ebank.garantibank.ro/isube/yatirimislemleri/doviz/dovizkur";

    @Override
    public String getBankName() {
        return "GarantiBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        String lastUpdateAsString = DateUtils.RO_SDF_HH_mm.format(new Date());
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Document doc = Jsoup.parse(new URL(URL), 2000);

        Elements table = doc.select("table").select("tr:gt(1)");

        for(int i = 0; i < table.size(); i+=2) {
            Element currencyBuyElement = table.get(i);
            Element currencySellElement = table.get(i+1);
            
            String currencyString = currencyBuyElement.child(0).text();
            String buyString = currencyBuyElement.child(2).text().trim().replace(",", ".");
            String sellString = currencySellElement.child(1).text().trim().replace(",", ".");
            
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
