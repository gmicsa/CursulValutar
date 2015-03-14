/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author georgian
 */
@XmlRootElement(name="history")
public class ExchangeRateHistoryContainer implements Serializable {

    private List<ExchangeRateHistory> elements = new ArrayList<ExchangeRateHistory>();
    
    public ExchangeRateHistoryContainer(){
    
    }

    @XmlElement(name="elements")
    public List<ExchangeRateHistory> getElements() {
        return elements;
    }

    public void setElements(List<ExchangeRateHistory> elements) {
        this.elements = elements;
    }
    
    public void add(ExchangeRateHistory element) {
        this.elements.add(element);
    }
    
}
