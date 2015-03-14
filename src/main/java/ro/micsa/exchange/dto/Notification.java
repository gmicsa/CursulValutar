package ro.micsa.exchange.dto;

import ro.micsa.exchange.retrievers.BNRDataRetriever;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by Georgian
 * at 26.01.2013 15:08
 */
@XmlRootElement
public class Notification implements Serializable {
    private static final long serialVersionUID = 3L;

    private String bankName = BNRDataRetriever.BNR;
    private CurrencyType currencyType = CurrencyType.EUR;
    private TransactionType transactionType = TransactionType.REF;
    private double threshold;
    private NotificationComparator comparator = NotificationComparator.GREATER_OR_EQUAL;
    private boolean enabled = true;
    private Language language = Language.RO;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public NotificationComparator getComparator() {
        return comparator;
    }

    public void setComparator(NotificationComparator comparator) {
        this.comparator = comparator;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
