/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.retrievers;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.ExchangeRate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author georgian
 */
@Component("BNRDataRetriever")
public class BNRDataRetriever implements BankDataRetriever {

    public static final String BNR = "BNR";

    private static String BNR_URL = "http://www.bnr.ro/nbrfxrates.xml";
    
    @Override
    public String getBankName() {
        return BNR;
    }
    
    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        URLConnection urlConnection = new URL(BNR_URL).openConnection();
        urlConnection.setReadTimeout(2000);
        InputSource inputSource = new InputSource(urlConnection.getInputStream());
        
        Document doc = db.parse(inputSource);
        
        NodeList nodes = doc.getElementsByTagName("Rate");
        
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);            
            String currencyName = node.getAttributes().getNamedItem("currency").getNodeValue();
            String value = node.getTextContent();
            try {
                CurrencyType ct = CurrencyType.valueOf(currencyName);
                Double currencyValue = Double.parseDouble(value);
                ExchangeRate rate = new ExchangeRate();
                rate.setCurrencyType(ct);
                rate.setValue(currencyValue);
                rates.add(rate);
            } catch (IllegalArgumentException i) {

            }
        }
        
        return rates;
    }
}
