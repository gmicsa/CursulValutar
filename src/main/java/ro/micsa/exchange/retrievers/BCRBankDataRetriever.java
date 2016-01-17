/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.retrievers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.utils.ExchangeRateHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author georgian
 */
@Component("BCRBankDataRetriever")
public class BCRBankDataRetriever implements BankDataRetriever {

    private static String BCR_URL = "https://www.bcr.ro/bin/erstegroup/fx?institute=785";

    @Override
    public String getBankName() {
        return "BCR";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> rates = new ArrayList<>();
        JSONArray jsonArray = getJSonArrayOfCurrencies();

        for(int index = 0; index < jsonArray.length(); index ++) {
            JSONObject currencyJsonObject = jsonArray.getJSONObject(index);

            CurrencyType currencyType = null;
            try {
                currencyType = CurrencyType.valueOf(currencyJsonObject.getString("currency"));
            } catch (Exception e) {
                continue;
            }

            JSONObject exchangeRateObject = currencyJsonObject.getJSONObject("exchangeRate");
            String buyString = String.valueOf(exchangeRateObject.getDouble("buy"));
            String sellString = String.valueOf(exchangeRateObject.getDouble("sell"));
            Date lastModified = parseBCRDateText(exchangeRateObject.getString("lastModified"));

            rates.add(ExchangeRateHelper.addBuyExchangeRate(currencyType, buyString, lastModified.toString()));
            rates.add(ExchangeRateHelper.addSellExchangeRate(currencyType, sellString, lastModified.toString()));
        }

        return rates;
    }

    private JSONArray getJSonArrayOfCurrencies() throws IOException {
        URL url = new URL(BCR_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        String jsonResponse = getJsonResponseFromGETRequest(httpURLConnection);

        return new JSONObject(jsonResponse).getJSONArray("fx");
    }

    private Date parseBCRDateText(String dateTextToParse) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return format.parse(dateTextToParse);
    }

    private String getJsonResponseFromGETRequest(HttpURLConnection httpURLConnection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                httpURLConnection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        return response.toString();
    }

}
