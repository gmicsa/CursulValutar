/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.model;

import com.google.appengine.api.datastore.Key;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.Language;
import ro.micsa.exchange.dto.NotificationComparator;
import ro.micsa.exchange.dto.TransactionType;
import ro.micsa.exchange.retrievers.BNRDataRetriever;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author AlexSch
 */
@Entity
public class NotificationEntity implements Serializable {

    private static final long serialVersionUID = 3L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;
    // implementing the unowned relationship via user Key
    private Key userId;
    private CurrencyType currencyType = CurrencyType.EUR;
    private TransactionType transactionType = TransactionType.REF;
    private String bankName = BNRDataRetriever.BNR;
    // threshold at which the notification is activated
    private double threshold;
    // boolean to state if value has to be greater or smaller than threshold; default higher
    @Enumerated(EnumType.ORDINAL)
    private NotificationComparator comparator = NotificationComparator.GREATER_OR_EQUAL;
    //is this rule taken into account when sending notifications
    private boolean enabled = true;
    // notification language; by default RO
    private Language language = Language.RO;

    public NotificationEntity() {
    }

    public NotificationEntity(Key userId) {
        this.userId = userId;
    }

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public Key getUserId() {
        return userId;
    }

    public void setUserId(Key userId) {
        this.userId = userId;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationEntity that = (NotificationEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", currencyType=" + currencyType +
                ", transactionType=" + transactionType +
                ", bankName='" + bankName + '\'' +
                ", threshold=" + threshold +
                ", comparator=" + comparator +
                ", enabled=" + enabled +
                ", language=" + language +
                '}';
    }
}
