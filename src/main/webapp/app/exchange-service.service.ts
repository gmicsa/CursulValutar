/**
 * Created by alexands on 16.11.2016.
 */
import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import "rxjs/add/operator/toPromise";
import {Rate} from "./rate";

@Injectable()
export class RatesService {
    private urlGetRates = 'services/rates';
    private urlCurrency = '/EUR';
    
    constructor(private http : Http) {
    }

    retrieveRates(dateFormatted: string): Promise<Rate[]> {
        console.log('Retrieve rates is executing..');
        return this.http.get(this.urlGetRates + '/' + dateFormatted + this.urlCurrency)
            .toPromise()
            .then(response => response.json().rates.map(exchangeRate => new Rate(exchangeRate)))
            .catch(this.handleError);
    }

    private handleError(error: any): Promise<any> {
        console.error('There was an error ' + error);
        return Promise.reject(error.message | error);
    }

}