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
import ro.micsa.exchange.utils.DateUtils;
import ro.micsa.exchange.utils.ExchangeRateFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Alex
 */
@Component("UniCreditTiriacDataRetriver")
public class UniCreditTiriacDataRetriver implements BankDataRetriever {
    
    public static final String UNICREDIT_TIRIAC_URL = "https://www.unicredit.ro/cwa/GetExchangeRates";

    @Override
    public String getBankName() {
        return "UniCreditTiriac";
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        String jsonResponse = getJsonResponseFromPOSTRequest();

        Map<String, DateExchangeRates> currencyDateExchangeRates = new HashMap<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);
        for(int index = 0; index < jsonArray.length(); index ++) {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            modifyMapWithJsonObject(currencyDateExchangeRates, jsonObject);
        }

        for(String currencyKey : currencyDateExchangeRates.keySet()) {
            CurrencyType currencyType = null;
            try {
                currencyType = CurrencyType.valueOf(currencyKey);
            } catch (Exception e) {
                continue;
            }

            DateExchangeRates dateExchangeRates = currencyDateExchangeRates.get(currencyKey);
            String lastUpdateAsString = DateUtils.RO_SDF_HH_mm.format(dateExchangeRates.date);
            exchangeRates.add(ExchangeRateFactory.createBuyExchangeRate(currencyType, dateExchangeRates.buyString, lastUpdateAsString));
            exchangeRates.add(ExchangeRateFactory.createSellExchangeRate(currencyType, dateExchangeRates.sellString, lastUpdateAsString));
        }

        return exchangeRates;
    }

    private String getJsonResponseFromPOSTRequest() throws IOException {
        HttpURLConnection httpURLConnection = configureHttpURLConnection();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                httpURLConnection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        return response.toString();
    }

    private HttpURLConnection configureHttpURLConnection() throws IOException {
        URL url = new URL(UNICREDIT_TIRIAC_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setReadTimeout(5000);
        httpURLConnection.addRequestProperty("Content-Type", "application/json");
        httpURLConnection.addRequestProperty("UserId", "");
        httpURLConnection.addRequestProperty("EntityCode", "RO");
        httpURLConnection.addRequestProperty("Language", "RO");
        httpURLConnection.addRequestProperty("SourceSystem", "PWS");
        httpURLConnection.addRequestProperty("Product", "PWS");

        String dateFormattedForJson = formatDateToJson(new Date());
        String jsonToSend = "{'Currency': '*ALL','DateFrom':'" + dateFormattedForJson
                + "','DateTo':'" + dateFormattedForJson + "'}";

        httpURLConnection.setDoOutput(true);
        OutputStreamWriter w = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");

        w.write(jsonToSend);
        w.close();

        return httpURLConnection;
    }

    private void modifyMapWithJsonObject(Map<String, DateExchangeRates> currencyDateExchangeRates, JSONObject jsonObject) throws ParseException {
        String currency = jsonObject.getString("CurrencyCode");
        Date dateParsed = parseUniCreditTiriacDateText(getJsonProperty(jsonObject, "ExchangeRateUpdatedTimestamp"));

        DateExchangeRates dateExchangeRatesInMap = currencyDateExchangeRates.get(currency);
        if(dateExchangeRatesInMap == null || dateParsed.after(dateExchangeRatesInMap.date)) {
            currencyDateExchangeRates.put(currency, new DateExchangeRates(dateParsed,
                    getJsonProperty(jsonObject, "PurchaseRate"),
                    getJsonProperty(jsonObject, "SaleRate")));
        }
    }

    private String getJsonProperty(JSONObject jsonObject, String key) {
        return jsonObject.getString(key);
    }

    private String formatDateToJson(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(date);
    }

    /**
     * parse a UniCreditTiriac last updated date text, available in romanian locale
     * and transforms it into a date
     * @param dateTextToParse -- example 2016-01-11T09:00:54.000+02:00
     * @return
     * @throws ParseException 
     */
    private Date parseUniCreditTiriacDateText(String dateTextToParse) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return format.parse(dateTextToParse);
    }

    private class DateExchangeRates {
        Date date;
        String buyString;
        String sellString;

        public DateExchangeRates(Date date, String buyString, String sellString) {
            this.date = date;
            this.buyString = buyString;
            this.sellString = sellString;
        }
    }
}
