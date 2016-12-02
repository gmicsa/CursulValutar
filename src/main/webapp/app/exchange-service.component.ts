import {Component, OnInit} from "@angular/core";
import {RatesService} from "./exchange-service.service";
import {Rate} from "./rate";

@Component({
    selector: 'exchange-service',
    template: `<h1>CursRapid.ro</h1>
        <div>Afli rapid cursul</div>
        <div *ngIf="!error">
            <table class="table table-striped" [mfData]="rates" #exportData="mfDataTable">
                <thead>
                <tr>
                    <th style="width: 40%">
                        <mfDefaultSorter by="bank">Bank</mfDefaultSorter>
                    </th>
                    <th style="width: 20%">
                        <mfDefaultSorter by="buyRate">Buy rate</mfDefaultSorter>
                    </th>
                    <th style="width: 20%">
                        <mfDefaultSorter by="sellRate">Sell rate</mfDefaultSorter>
                    </th>
                    <th style="width: 20%">
                        <mfDefaultSorter by="lastUpdate">Last update</mfDefaultSorter>
                    </th>
                </tr>
                </thead>
                <tbody>
                <tr *ngFor="let bank of exportData.data">
                    <td class="text-right">{{bank.bankName}}</td>
                    <td>{{bank.value}}</td>
                    <td>{{bank.transactionType}}</td>
                    <td>{{bank.lastChangedAt}}</td>
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

    private initColumnsWithHeaders(): Array<any> {
        return [
            {title: 'Bank', name: 'bank'},
            {title: 'Buy rate', name: 'buyRate'},
            {title: 'Sell rate', name: 'sellRate'},
            {title: 'Last update', name: 'lastUpdate'}
        ];
    }
    
}