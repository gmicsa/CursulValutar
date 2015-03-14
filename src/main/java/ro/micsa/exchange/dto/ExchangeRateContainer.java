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
@XmlRootElement(name="response")
public class ExchangeRateContainer implements Serializable {

    private List<ExchangeRate> elements = new ArrayList<ExchangeRate>();
    
    public ExchangeRateContainer(){
    
    }

    @XmlElement(name="rates")
    public List<ExchangeRate> getElements() {
        return elements;
    }

    public void setElements(List<ExchangeRate> elements) {
        this.elements = elements;
    }
    
    public void add(ExchangeRate element) {
        this.elements.add(element);
    }
    
}
