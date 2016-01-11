/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.retrievers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.utils.DateUtils;
import ro.micsa.exchange.utils.ExchangeRateHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
            exchangeRates.add(ExchangeRateHelper.addBuyExchangeRate(currencyType, dateExchangeRates.buyString, lastUpdateAsString));
            exchangeRates.add(ExchangeRateHelper.addSellExchangeRate(currencyType, dateExchangeRates.sellString, lastUpdateAsString));
        }

        return exchangeRates;
    }

    private String getJsonResponseFromPOSTRequest() throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = configureHttpPost();

        HttpResponse httpResponse = httpClient.execute(httpPost);
        return EntityUtils.toString(httpResponse.getEntity());
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

    private HttpPost configureHttpPost() throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(UNICREDIT_TIRIAC_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("UserId", "");
        httpPost.setHeader("EntityCode", "RO");
        httpPost.setHeader("Language", "RO");
        httpPost.setHeader("SourceSystem", "PWS");
        httpPost.setHeader("Product", "PWS");

        String dateFormattedForJson = formatDateToJson(new Date());
        String jsonToSend = "{'Currency': '*ALL','DateFrom':'" + dateFormattedForJson
                + "','DateTo':'" + dateFormattedForJson + "'}";
        httpPost.setEntity(new ByteArrayEntity(jsonToSend.getBytes("UTF-8")));
        return httpPost;
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
