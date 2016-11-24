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
    private urlDateAndCurrency = '/2016-11-24/EUR';
    
    constructor(private http : Http) {
    }

    retrieveRates(): Promise<Rate[]> {
        console.log('Retrieve rates is executing..');
        return this.http.get(this.urlGetRates + this.urlDateAndCurrency)
            .toPromise()
            .then(response => response.json().map(item => new Rate(item)))
            .catch(this.handleError);
    }

    private handleError(error: any): Promise<any> {
        console.error('There was an error ' + error);
        return Promise.reject(error.message | error);
    }

}