/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author georgian
 */
@XmlRootElement
public class ErrorEntity {

    public enum ErrorCode {

        NO_RATES_FOUND,
        INVALID_BANK,
        INVALID_DATE,
        INVALID_VALUE,
        RATE_EXISTS
    }
    private ErrorCode error;

    public ErrorEntity() {
    }

    public ErrorEntity(ErrorCode error) {
        this.error = error;
    }

    public ErrorCode getErrorCode() {
        return error;
    }

    public void setErrorCode(ErrorCode error) {
        this.error = error;
    }
}
