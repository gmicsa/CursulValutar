import {Component, OnInit} from "@angular/core";
import {RatesService} from "./exchange-service.service";
import {ExchangeServiceUtils} from "./exchange-service.utils";
import {AggregatedRate} from "./aggregated-rate";
import {Rate} from "./rate";

@Component({
    selector: 'exchange-service',
    templateUrl: 'html/exchangerates.html',
    providers: [RatesService],
    styleUrls: [ 'css/exchangerates.css' ]
})
export class ExchangeServiceComponent extends OnInit {
    public currencyTypes: Array<string> = ExchangeServiceUtils.getCurrencyTypes();
    private minDate: Date = ExchangeServiceUtils.getMinimumDateForExchanges();
    private maxDate: Date = ExchangeServiceUtils.getMaximumDateForExchanges();

    private dateSelected: Date;
    private isCollapsed:boolean = true;
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
        console.log("Current date is ", this.getDateFormatted());
        this.retrieveRatesFromService();
    }

    public collapsed(event:any):void {
    }

    public expanded(event:any):void {
    }

    public getDateFormatted(): string {
        return ExchangeServiceUtils.formatDate(this.dateSelected);
    }

    currencyChanged(currencyElement) {
        this.currencySelected = currencyElement.innerHTML;
    }

    private retrieveRatesFromService(): void {
        this.ratesService.retrieveRates(this.getDateFormatted(), this.currencySelected).then(
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