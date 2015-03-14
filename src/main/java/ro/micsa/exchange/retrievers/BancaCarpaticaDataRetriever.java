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
import ro.micsa.exchange.utils.ExchangeRateHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author georgian
 */
@Component("BancaCarpaticaDataRetriever")
public class BancaCarpaticaDataRetriever implements BankDataRetriever {

    private static String URL = "http://www.carpatica.ro/index.php?option=com_content&view=article&id=291&Itemid=366";

    @Override
    public String getBankName() {
        return "BancaCarpatica";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        String lastUpdateAsString = DateUtils.RO_SDF_HH_mm.format(new Date());
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        Connection con = Jsoup.connect(URL);
        con.header("User-Agent", "Mozilla/5.0 (Ubuntu; X11; Linux x86_64; rv:8.0) Gecko/20100101 Firefox/8.0");
        con.timeout(2000);
        Document doc = con.get();

        Elements table = doc.select("table").get(2).select("tr:gt(1)");

        Iterator<Element> currencyIterator = table.iterator();

        int count = 1;
        while (currencyIterator.hasNext()) {
            if(count > 12){
                break;
            }
            count++;
            Element currencyElement = currencyIterator.next();
            
            String currencyString = currencyElement.child(1).child(0).text().substring(2);
            String buyString = currencyElement.child(4).text().trim().replace(",", ".");
            if(buyString.equals("-")){
                continue;
            }
            String sellString = currencyElement.child(3).text().trim().replace(",", ".");
            
            CurrencyType currencyType = null;
            try {
                if(currencyString.equals("EURO")){
                    currencyType = CurrencyType.EUR;
                }else{
                    currencyType = CurrencyType.valueOf(currencyString);
                }
            } catch (Exception e) {
                continue;
            }

            rates.add(ExchangeRateHelper.addBuyExchangeRate(currencyType, buyString, lastUpdateAsString));
            rates.add(ExchangeRateHelper.addSellExchangeRate(currencyType, sellString, lastUpdateAsString));
        }
        return rates;
    }
}
