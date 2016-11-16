import {Component, OnInit} from "@angular/core";
import {RatesService} from "./exchange-service.service";
import {Rate} from "./rate";

@Component({
    selector: 'exchange-service',
    template: `<h1>Hello</h1>
        <div>What's up</div>
    `,
    providers: [RatesService]
})
export class ExchangeServiceComponent extends OnInit {
    rates: Rate[];
    error: any;
    
    constructor(private ratesService : RatesService) {
    }
    
    ngOnInit(): void {
        this.retrieveRatesFromService();
    }
    
    private retrieveRatesFromService(): void {
        this.ratesService.retrieveRates().then(
           rates => {
               this.rates = rates;
               this.error = null;
           },
            error => {
                this.error = error;
                this.rates = [];
            }
        )
            
    }
    
}