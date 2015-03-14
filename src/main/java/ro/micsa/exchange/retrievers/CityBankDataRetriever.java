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
import java.util.*;

/**
 *
 * @author georgian
 */
@Component("CityBankDataRetriever")
public class CityBankDataRetriever implements BankDataRetriever {

    private static String URL = "http://www.citibank.ro/curs-valutar.html";

    @Override
    public String getBankName() {
        return "CityBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        String lastUpdateAsString = null;
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Document doc = Jsoup.parse(new URL(URL), 2000);
        
        String lastChangeString = doc.select("#labelDate").text().replaceAll(",", "");
        StringBuilder sb = new StringBuilder(lastChangeString);
        sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
        lastChangeString = sb.toString();
        
        Date lastChangeDate = new SimpleDateFormat("dd MMMMM yyyy HH:mm", new Locale("ro","RO")).parse(lastChangeString);
        lastUpdateAsString = DateUtils.SDF_HH_mm.format(lastChangeDate);
        
        Elements table = doc.select(".table.stretch").select("tr:gt(0)");

        Iterator<Element> currencyIterator = table.iterator();

        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();
            String currencyString = ((TextNode)currencyElement.child(0).childNode(0)).text().trim();
            String buyString = currencyElement.child(1).text().replaceAll(",", ".");
            String sellString = currencyElement.child(2).text().replaceAll(",", ".");
            
            CurrencyType currencyType = null;
            try {
                currencyType = CurrencyType.valueOf(currencyString);
            } catch (Exception e) {
                continue;
            }

            ExchangeRate rateSell = ExchangeRateHelper.addSellExchangeRate(currencyType, sellString, lastUpdateAsString);
            ExchangeRate rateBuy = ExchangeRateHelper.addBuyExchangeRate(currencyType, buyString, lastUpdateAsString);
         
            rates.add(rateSell);
            rates.add(rateBuy);
        }
        for(ExchangeRate er : rates){
            er.setLastChangedAt(lastUpdateAsString);
        }
        return rates;
    }
}
