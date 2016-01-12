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
 * @author Alex
 */
@Component("OTPBankDataRetriver")
public class OTPBankRetriever implements BankDataRetriever {

    public static final String OTP_BANK_URL = "http://www.otpbank.ro/ro/curs-valutar.html";
    
    @Override
    public String getBankName() {
        return "OTPBank";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        
        Document root = Jsoup.parse(new URL(OTP_BANK_URL), 2000);
        Elements content = root.select("table.tabelcurs");
        
        // select line with last update date
        String lastUpdatedDateString = DateUtils.RO_SDF_HH_mm.format(new Date());
        
        Iterator<Element> currencyIterator = content.select("tbody").select("tr").iterator();
        while(currencyIterator.hasNext()) {
            Element newElement = currencyIterator.next();

            String currencyName = newElement.select("td:eq(0)").text();
            String currencyBuyValue = newElement.select("td:eq(2)").text();
            String currencySellValue = newElement.select("td:eq(3)").text();

            CurrencyType currencyType = null;
            try {
                currencyType = CurrencyType.valueOf(currencyName);
            } catch(Exception e) {
                continue;   // no problem, continue with next currency
            }
            
            // buy rate
            exchangeRates.add(ExchangeRateHelper.addBuyExchangeRate
                    (currencyType, currencyBuyValue, lastUpdatedDateString));            
            // sell rate
            exchangeRates.add(ExchangeRateHelper.addSellExchangeRate
                    (currencyType, currencySellValue, lastUpdatedDateString));
        }
        
        return exchangeRates;
    }
    
}
