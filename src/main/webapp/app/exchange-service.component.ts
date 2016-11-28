import {Component, OnInit} from "@angular/core";
import {RatesService} from "./exchange-service.service";
import {Rate} from "./rate";

@Component({
    selector: 'exchange-service',
    template: `<h1>CursRapid.ro</h1>
        <div>Afli rapid cursul</div>
        <div *ngIf="!error">
            Rates are {{this.rates}}
        </div>
        <div *ngIf="error">
            Error is {{this.error}}
        </div>
    `,
    providers: [RatesService]
})
export class ExchangeServiceComponent extends OnInit {
    rates: Rate[];
    error: any;
    
    constructor(private ratesService : RatesService) {
        super();
    }
    
    ngOnInit(): void {
        this.retrieveRatesFromService();
    }
    
    private retrieveRatesFromService(): void {
        this.ratesService.retrieveRates().then(
           data => {
               this.rates = data;
               this.error = null;
           },
            error => {
                this.error = error;
                this.rates = [];
            }
        )
            
    }
    
}