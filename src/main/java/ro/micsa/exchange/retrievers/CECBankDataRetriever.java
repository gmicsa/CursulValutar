/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.retrievers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
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
@Component("CECBankDataRetriever")
public class CECBankDataRetriever implements BankDataRetriever {

    private static String URL = "https://www.cec.ro/curs-valutar";

    @Override
    public String getBankName() {
        return "CECBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Document doc = Jsoup.parse(new URL(URL), 2000);
        
        String lastUpdateAsString = DateUtils.RO_SDF_HH_mm.format(new Date());
        Elements table = doc.select("table.views-table").get(0).select("tr");

        Iterator<Element> currencyIterator = table.iterator();

        
        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();

            String currencyString = currencyElement.children().select("td:eq(0)").select("div").text().trim();
            String buyString = currencyElement.children().select("td:eq(3)").text().trim();
            String sellString = currencyElement.children().select("td:eq(4)").text().trim();

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
