/**
 * Created by alexands on 09.12.2016.
 */
import * as moment from "moment";
    
export class ExchangeServiceUtils {
    static readonly BNR_BANK_NAME = "BNR";
    static readonly DATE_FORMAT = "YYYY-MM-DD";
    static readonly MIN_DATE_STRING = "2012-03-01";
    
    static getMinimumDateForExchanges(): Date {
        return moment(ExchangeServiceUtils.MIN_DATE_STRING, ExchangeServiceUtils.DATE_FORMAT).toDate();
    }

    static getMaximumDateForExchanges(): Date {
        return moment().toDate();
    }
    
    static formatDate(date: Date): string {
        return moment(date).format(ExchangeServiceUtils.DATE_FORMAT).toString();
    }
    
    static getCurrencyTypes(): Array<string> {
        return ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'];
    }

    static convertNumberToSignedStringNumber(aNumber: number): string
    {
        if(aNumber > 0){
            return "+" + aNumber.toFixed(4);
        }else{
            return aNumber.toFixed(4);
        }
    }

    static computeVariance(value: number, evolution: number) {
        return (100 * evolution / (value - evolution));
    }

    static isNewElement(element: string, arrayOfElements: string[]) {
        return arrayOfElements.find(item => item === element) === undefined;
    }
    
}