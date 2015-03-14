package ro.micsa.exchange.service;

import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.Language;
import ro.micsa.exchange.dto.TransactionType;

/**
 * Created with IntelliJ IDEA.
 * User: Georgian
 * Date: 24.02.2013
 * Time: 21:01
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TemplateServiceImpl implements TemplateService{


    @Autowired
    private MessageSource messageSource;

    private static NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
    
    static{
        numberFormat.setMinimumFractionDigits(4);
    }

    @Override
    public String getAlertsMailSubject(Language lang){
        return getMessage("title", lang);
    }

    @Override
    public String getAlertsMailContent(Language lang, String userName, TransactionType transactionType, CurrencyType currencyType, String bankName, double threshold, double value, double evolution) {
        String template = getMessage("content", lang);

        template = template.replaceAll("\\$user", userName);
        template = template.replaceAll("\\$bank", bankName);
        if(transactionType.equals(TransactionType.BUY)){
            template = template.replaceAll("\\$transaction", getMessage("transaction_buy", lang));
        }else{
            if(transactionType.equals(TransactionType.SELL)){
                template = template.replaceAll("\\$transaction", getMessage("transaction_sell", lang));
            }else{
                template = template.replaceAll("\\$transaction ", "");
            }
        }
        template = template.replaceAll("\\$currency", currencyType.toString());
        if(threshold > value){
            template = template.replaceAll("\\$comparator", getMessage("comparator_less", lang));
        }else if(threshold < value){
            template = template.replaceAll("\\$comparator", getMessage("comparator_greater", lang));
        }else{
            template = template.replaceAll("\\$comparator", getMessage("comparator_equal", lang));
        }
        template = template.replaceAll("\\$alert_value", threshold+"");
        template = template.replaceAll("\\$value", value+"");
        template = template.replaceAll("\\$evolution", numberFormat.format(evolution));

        return template;
    }

    private String getMessage(String key, Language lang){
        return messageSource.getMessage(key, null, lang.equals(Language.EN)?Locale.ENGLISH: new Locale("ro"));
    }
}
