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

    private static String URL = "https://www.cec.ro/curs-valutar.aspx";

    @Override
    public String getBankName() {
        return "CECBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        String lastUpdateAsString = null;
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Document doc = Jsoup.parse(new URL(URL), 2000);
        
        lastUpdateAsString = ((TextNode)doc.select(".section").get(9).childNodes().get(8)).text().substring(27).trim();
        Date date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(lastUpdateAsString);
        lastUpdateAsString = DateUtils.SDF_HH_mm.format(date);
        Elements table = doc.select(".list_table").get(0).select("tr:gt(0)");

        Iterator<Element> currencyIterator = table.iterator();

        
        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();
            
            String currencyString = currencyElement.child(0).text();
            String buyString = currencyElement.child(3).text().split(" ")[0];
            String sellString = currencyElement.child(4).text().split(" ")[0];

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
