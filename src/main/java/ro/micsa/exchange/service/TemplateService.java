package ro.micsa.exchange.service;

import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.Language;
import ro.micsa.exchange.dto.TransactionType;

/**
 * Created with IntelliJ IDEA.
 * User: Georgian
 * Date: 25.02.2013
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
public interface TemplateService {

    public String getAlertsMailSubject(Language lang);

    public String getAlertsMailContent(Language lang, String userName, TransactionType transactionType,
                                       CurrencyType currencyType, String bankName,
                                       double threshold, double value, double evolution);
}
