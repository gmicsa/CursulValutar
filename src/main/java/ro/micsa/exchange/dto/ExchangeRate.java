/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author georgian
 */
@XmlRootElement(name="rate")
public class ExchangeRate implements Serializable {

    private String date;// yyyy-MM-dd
    private double value;
    private CurrencyType currencyType = CurrencyType.EUR;
    private TransactionType transactionType = TransactionType.REF;
    private String bankName;
    private double evolution;
    private String lastChangedAt;// yyyy-MM-dd HH:mm

    public ExchangeRate() {
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
