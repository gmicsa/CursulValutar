/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.utils;

import java.util.List;
import ro.micsa.exchange.dto.ExchangeRate;

/**
 *
 * @author georgian.micsa
 */
public class EntityUtils {

    public static void printExchangeRates(List<ExchangeRate> retrieved) {
        System.out.println("Last updated date is " + retrieved.get(0).getLastChangedAt());
        for (ExchangeRate rate : retrieved) {
            System.out.println("Currency type " + rate.getCurrencyType() + "; transaction type " + rate.getTransactionType()
                    + "; value " + rate.getValue());
        }
    }
}
