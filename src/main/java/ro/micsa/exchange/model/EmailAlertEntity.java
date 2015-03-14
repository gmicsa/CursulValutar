/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.model;

import com.google.appengine.api.datastore.Key;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.Language;
import ro.micsa.exchange.dto.TransactionType;
import ro.micsa.exchange.retrievers.BNRDataRetriever;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Georgian
 */
@Entity
public class EmailAlertEntity implements Serializable {

    private static final long serialVersionUID = 4L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;

    private String email;
    private String fbUserName;
    private Language language = Language.RO;

    private CurrencyType currencyType = CurrencyType.EUR;
    private TransactionType transactionType = TransactionType.REF;
    private String bankName = BNRDataRetriever.BNR;

    private double value;
    private double evolution;
    private double threshold;

    private String sentDate;
    private EmailAlertStatus status = EmailAlertStatus.WAITING;

    public enum EmailAlertStatus{
        SENT, ERROR, WAITING
    }

    public EmailAlertEntity() {

    }

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFbUserName() {
        return fbUserName;
    }

    public void setFbUserName(String fbUserName) {
        this.fbUserName = fbUserName;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getEvolution() {
        return evolution;
    }

    public void setEvolution(double evolution) {
        this.evolution = evolution;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public EmailAlertStatus getStatus() {
        return status;
    }

    public void setStatus(EmailAlertStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailAlertEntity that = (EmailAlertEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "EmailAlertEntity{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fbUserName='" + fbUserName + '\'' +
                ", language=" + language +
                ", currencyType=" + currencyType +
                ", transactionType=" + transactionType +
                ", bankName='" + bankName + '\'' +
                ", value=" + value +
                ", evolution=" + evolution +
                ", threshold=" + threshold +
                ", sentDate='" + sentDate + '\'' +
                ", status=" + status +
                '}';
    }
}
