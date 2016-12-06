import {Component, OnInit} from "@angular/core";
import {RatesService} from "./exchange-service.service";
import {AggregatedRate} from "./aggregated-rate";
import * as moment from 'moment';
import {Rate} from "./rate";

@Component({
    selector: 'exchange-service',
    templateUrl: 'html/exchangerates.html',
    providers: [RatesService],
    styles: [`
    datepicker-ionic {
      display: inline-block;
      margin-left: 150px;
    }
  `]
})
export class ExchangeServiceComponent extends OnInit {
    readonly BNR_BANK_NAME = 'BNR';
    readonly DATE_FORMAT = "YYYY-MM-DD";
    
    public currencyTypes: Array<string> = ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'];

    dateSelected: moment.Moment;
    currencySelected: string;
    
    rates: Rate[];
    bnrReferenceRate: Rate;
    aggregatedRates: AggregatedRate[];
    error: any;

    constructor(private ratesService : RatesService) {
        super();
    }
    
    ngOnInit(): void {
        this.dateSelected = moment(new Date());
        this.currencySelected = 'EUR';
        console.log("Current date is ", this.dateSelected.format(this.DATE_FORMAT));
        this.retrieveRatesFromService();
    }

    public currencyChangedEvent(event: string) {
        this.currencySelected = event;
    }
    
    public dateChangedEvent(event: moment.Moment) {
        this.dateSelected = event;
        console.log("New date selected is ", this.dateSelected.format(this.DATE_FORMAT));
    }
    
    private retrieveRatesFromService(): void {
        this.ratesService.retrieveRates(this.dateSelected.format(this.DATE_FORMAT), this.currencySelected).then(
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