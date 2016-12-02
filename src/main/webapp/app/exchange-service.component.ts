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
                <tr *ngFor="let aggregateRate of aggregatedRates">
                    <td class="text-right">{{aggregateRate.bankName}}</td>
                    <td>{{aggregateRate.buyValue}}</td>
                    <td>{{aggregateRate.sellValue}}</td>
                    <td>{{aggregateRate.lastChangedAt}}</td>
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
    readonly BNR_BANK_NAME = 'BNR';

    rates: Rate[];
    bnrReferenceRate: Rate;
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
               this.computeAggregatedRates();
           },
            error => {
                this.error = error;
                this.rates = [];
                this.aggregatedRates = [];
            }
        )
    }

    private computeAggregatedRates(): void {
        var bankNames: string[] = [];
        this.rates.forEach((rate) => {
            if(this.isNewElement(rate.bankName, bankNames)) {
                bankNames.push(rate.bankName);
            }
        });
        this.aggregatedRates = [];
        bankNames.forEach((bankName) => {
            this.addAggregatedRateForBankName(bankName);
        });
    }

    private addAggregatedRateForBankName(bankName) {
        if(bankName === this.BNR_BANK_NAME) {
            this.bnrReferenceRate = this.findBnrReferenceRate();
        } else {
            this.aggregatedRates.push(new AggregatedRate(this.findRateWithBankAndTransactionType(bankName, 'BUY'),
                this.findRateWithBankAndTransactionType(bankName, 'SELL')));
        }
    }

    private isNewElement(element: string, arrayOfElements: string[]) {
        return arrayOfElements.find(item => item === element) === undefined;
    }

    private findRateWithBankAndTransactionType(bankName: string, transactionType: string): Rate {
        return this.rates.find(rate => rate.bankName === bankName && rate.transactionType === transactionType);
    }

    private findBnrReferenceRate() {
        return this.rates.find(rate => rate.bankName === this.BNR_BANK_NAME && rate.transactionType === 'REF');
    }
}