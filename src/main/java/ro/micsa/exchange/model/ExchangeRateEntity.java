/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.TransactionType;

/**
 *
 * @author georgian
 */
@Entity
public class ExchangeRateEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;// yyyy-MM-dd
    private double value;
    private CurrencyType currencyType = CurrencyType.EUR;
    private TransactionType transactionType = TransactionType.SELL;
    private String bankName;
    private double evolution;
    private String lastChangedAt;// yyyy-MM-dd HH:mm

    public ExchangeRateEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getEvolution() {
        return evolution;
    }

    public void setEvolution(double evolution) {
        this.evolution = evolution;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getLastChangedAt() {
        return lastChangedAt;
    }

    public void setLastChangedAt(String lastChangedAt) {
        this.lastChangedAt = lastChangedAt;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        hash = 79 * hash + (this.currencyType != null ? this.currencyType.hashCode() : 0);
        hash = 79 * hash + (this.transactionType != null ? this.transactionType.hashCode() : 0);
        hash = 79 * hash + (this.bankName != null ? this.bankName.hashCode() : 0);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.evolution) ^ (Double.doubleToLongBits(this.evolution) >>> 32));
        hash = 79 * hash + (this.lastChangedAt != null ? this.lastChangedAt.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExchangeRateEntity other = (ExchangeRateEntity) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.date == null) ? (other.date != null) : !this.date.equals(other.date)) {
            return false;
        }
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        if (this.currencyType != other.currencyType) {
            return false;
        }
        if (this.transactionType != other.transactionType) {
            return false;
        }
        if ((this.bankName == null) ? (other.bankName != null) : !this.bankName.equals(other.bankName)) {
            return false;
        }
        if (Double.doubleToLongBits(this.evolution) != Double.doubleToLongBits(other.evolution)) {
            return false;
        }
        if ((this.lastChangedAt == null) ? (other.lastChangedAt != null) : !this.lastChangedAt.equals(other.lastChangedAt)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Date: ");
        sb.append(date);
        sb.append("\n");
        sb.append("Bank: ");
        sb.append(bankName);
        sb.append("\n");
        sb.append("Currency: ");
        sb.append(currencyType);
        sb.append("\n");
        sb.append("Transaction: ");
        sb.append(transactionType);
        sb.append("\n");
        sb.append("Last change: ");
        sb.append(lastChangedAt);
        sb.append("\n");
        sb.append("Value: ");
        sb.append(value);
        sb.append("\n");
        sb.append("Evolution: ");
        sb.append(evolution);
        sb.append("\n");
        return sb.toString();
    }
}
