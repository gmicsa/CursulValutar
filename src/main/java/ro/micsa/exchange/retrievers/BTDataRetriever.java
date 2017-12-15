/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.retrievers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.utils.DateUtils;
import ro.micsa.exchange.utils.ExchangeRateFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author georgian
 */
@Component("BTDataRetriever")
public class BTDataRetriever implements BankDataRetriever {

    private static String URL = "http://www.bancatransilvania.ro/bt/curs-valutar-spot/";

    @Override
    public String getBankName() {
        return "BT";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        String lastUpdateAsString = null;
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Connection con = Jsoup.connect(URL);
        // added this header, otherwise we get the mobile version of the page
        con.header("User-Agent", "Mozilla/5.0 (Ubuntu; X11; Linux x86_64; rv:8.0) Gecko/20100101 Firefox/8.0");
        con.timeout(2000);
        Document doc = con.get();
        
        Elements table = doc.select(".left_col_inv").get(0).select(".bg_1").get(0).select("table").eq(1).select("tr");
        Iterator<Element> currencyIterator = table.iterator();

        int index = 0;
        while (currencyIterator.hasNext()) {
            Element currencyElement = currencyIterator.next();
            index++;
            if(index == 28){
                Date date = new SimpleDateFormat("dd MMM yyyy - HH:mm:ss").parse(currencyElement.child(1).text());
                lastUpdateAsString = DateUtils.SDF_HH_mm.format(date);
                break;
            }

            String currencyString = currencyElement.child(0).text().split(" ")[0];
            String buyString = currencyElement.child(2).text();
            String sellString = currencyElement.child(3).text();

            CurrencyType currencyType = null;
            try {
                currencyType = CurrencyType.valueOf(currencyString);
            } catch (Exception e) {
                continue;
            }
           
            rates.add(ExchangeRateFactory.createBuyExchangeRate(currencyType, buyString, lastUpdateAsString));
            rates.add(ExchangeRateFactory.createSellExchangeRate(currencyType, sellString, lastUpdateAsString));
        }
        for(ExchangeRate er : rates){
            er.setLastChangedAt(lastUpdateAsString);
        }
        return rates;
    }
}
