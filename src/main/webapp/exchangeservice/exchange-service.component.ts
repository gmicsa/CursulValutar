import {Component, OnInit} from "@angular/core";
import {RatesService} from "./exchange-service.service";
import {ExchangeServiceUtils} from "./exchange-service.utils";
import {AggregatedRate} from "./aggregated-rate";
import {Rate} from "./rate";

@Component({
    selector: 'exchange-service',
    template: `<div>
    <div>Select date:</div>
    <div style="display: inline-block; min-height: 290px;">
        <datepicker class="datepicker" [minDate]="minDate" [maxDate]="maxDate" [(ngModel)]="dateSelected"></datepicker>
    </div>
</div>
<div>
    <div>Select currency:</div>
    <!-- Single button -->
    <div class="btn-group" dropdown >
        <button id="single-button" type="button" class="btn btn-primary" dropdownToggle>
            {{currencySelected}} <span class="caret"></span>
        </button>
        <ul dropdownMenu role="menu" aria-labelledby="single-button">
            <li *ngFor="let currency of currencyTypes">
                <a class="dropdown-item" href="#">{{currency}}</a>
            </li>
        </ul>
    </div>
</div>
<br>
<h2>BNR Reference rate: {{computeBnrValue()}} <span class="greenSmall">{{computeBnrVariance()}}</span> <span class="greenSmall">{{computeBnrPercentVariance()}}</span></h2>
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
            <td>{{aggregateRate.buyValue}} <span class="greenSmall">{{aggregateRate.buyVariance}}</span> <span class="greenSmall">{{aggregateRate.buyPercentVariance}}</span></td>
            <td>{{aggregateRate.sellValue}} <span class="greenSmall">{{aggregateRate.sellVariance}}</span> <span class="greenSmall">{{aggregateRate.sellPercentVariance}}</span></td>
            <td>{{aggregateRate.lastChangedAt}}</td>
        </tr>
        </tbody>
    </table>
</div>
<div *ngIf="error">
    Error is {{this.error}}
</div>`,
    providers: [RatesService],
    styleUrls: [ 'css/exchangerates.css' ]
})
export class ExchangeServiceComponent extends OnInit {
    public currencyTypes: Array<string> = ExchangeServiceUtils.getCurrencyTypes();
    private minDate: Date = ExchangeServiceUtils.getMinimumDateForExchanges();
    private maxDate: Date = ExchangeServiceUtils.getMaximumDateForExchanges();

    private dateSelected: Date;
    private currencySelected: string;

    rates: Rate[];
    bnrReferenceRate: Rate;
    aggregatedRates: AggregatedRate[];
    error: any;

    constructor(private ratesService : RatesService) {
        super();
    }
    
    ngOnInit(): void {
        this.dateSelected = new Date();
        this.currencySelected = "EUR";
        console.log("Current date is ", ExchangeServiceUtils.formatDate(this.dateSelected));
        this.retrieveRatesFromService();
    }

    private retrieveRatesFromService(): void {
        this.ratesService.retrieveRates(ExchangeServiceUtils.formatDate(this.dateSelected), this.currencySelected).then(
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
            if(ExchangeServiceUtils.isNewElement(rate.bankName, bankNames)) {
                bankNames.push(rate.bankName);
            }
        });
        this.aggregatedRates = [];
        bankNames.forEach((bankName) => { this.addAggregatedRateForBankName(bankName); });
    }

    private addAggregatedRateForBankName(bankName) {
        if(bankName === ExchangeServiceUtils.BNR_BANK_NAME) {
            this.bnrReferenceRate = this.findBnrReferenceRate();
        } else {
            this.addAggregatedRateIfBuyAndSellRateAvailable(bankName);
        }
    }

    private addAggregatedRateIfBuyAndSellRateAvailable(bankName) {
        var buyRate = this.findRateWithBankAndTransactionType(bankName, 'BUY');
        var sellRate = this.findRateWithBankAndTransactionType(bankName, 'SELL');
        if(buyRate && sellRate) {
            this.aggregatedRates.push(new AggregatedRate(buyRate, sellRate));
        }
    }
    
    private findRateWithBankAndTransactionType(bankName: string, transactionType: string): Rate {
        return this.rates.find(rate => rate.bankName === bankName && rate.transactionType === transactionType);
    }

    private findBnrReferenceRate() {
        return this.rates.find(rate => rate.bankName === ExchangeServiceUtils.BNR_BANK_NAME && rate.transactionType === 'REF');
    }
    
    private computeBnrVariance(): string {
        return this.bnrReferenceRate ? ExchangeServiceUtils.convertNumberToSignedStringNumber(this.bnrReferenceRate.evolution) : "";
    }
    
    private computeBnrPercentVariance(): string {
        return this.bnrReferenceRate ? ExchangeServiceUtils.convertNumberToSignedStringNumber(
            ExchangeServiceUtils.computeVariance(this.bnrReferenceRate.value, this.bnrReferenceRate.evolution)) : "";
    }

    private computeBnrValue(): number {
        return this.bnrReferenceRate ? this.bnrReferenceRate.value : 0;
    }

}