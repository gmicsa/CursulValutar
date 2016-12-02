import {Component, OnInit} from "@angular/core";
import {RatesService} from "./exchange-service.service";
import {AggregatedRate} from "./aggregated-rate";
import {Rate} from "./rate";

@Component({
    selector: 'exchange-service',
    template: `<h1>CursRapid.ro</h1>
        <div>Afli rapid cursul</div>
        <div *ngIf="!error">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th style="width: 40%">Bank</th>
                    <th style="width: 20%">Buy rate</th>
                    <th style="width: 20%">Sell rate</th>
                    <th style="width: 20%">Last update</th>
                </tr>
                </thead>
                <tbody>
                <tr *ngFor="let rate of rates">
                    <td class="text-right">{{rate.bankName}}</td>
                    <td>{{rate.value}}</td>
                    <td>{{rate.transactionType}}</td>
                    <td>{{rate.lastChangedAt}}</td>
                </tr>
                </tbody>
            </table>
        </div>
        <div *ngIf="error">
            Error is {{this.error}}
        </div>
    `,
    providers: [RatesService]
})
export class ExchangeServiceComponent extends OnInit {
    rates: Rate[];
    aggregatedRates: AggregatedRate[];
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
                this.aggregatedRates = [];
            }
        )
    }

    private initColumnsWithHeaders(): Array<any> {
        return [
            {title: 'Bank', name: 'bank'},
            {title: 'Buy rate', name: 'buyRate'},
            {title: 'Sell rate', name: 'sellRate'},
            {title: 'Last update', name: 'lastUpdate'}
        ];
    }
    
}