import {Rate} from "./rate";

/**
 * Created by alexands on 02.12.2016.
 */
export class AggregatedRate {
    bankName: string;
    currencyType: string;
    lastChangedAt: string;

    buyValue: string;
    buyVariance: string;
    buyPercentVariance: string;

    sellValue: string;
    sellVariance: string;
    sellPercentVariance: string;
    
    private constructor() {}
    
    constructor(buyRate: Rate, sellRate: Rate) {
        return this.createAggregatedRate(buyRate, sellRate);
    }

    private createAggregatedRate(buyRate: Rate, sellRate: Rate): AggregatedRate {
        Rate.checkMatchingRates(buyRate, sellRate);

        var aggregatedRate = new AggregatedRate();
        aggregatedRate.bankName = buyRate.bankName;
        aggregatedRate.currencyType = buyRate.currencyType;
        aggregatedRate.lastChangedAt = buyRate.lastChangedAt;
        this.computeAggregatedBuyRateValues(aggregatedRate, buyRate);
        this.computeAggregatedSellRateValues(aggregatedRate, sellRate);

        return aggregatedRate;
    }

    private computeAggregatedBuyRateValues(aggregatedRate:AggregatedRate, rate:Rate) {
        aggregatedRate.buyValue = rate.value.toFixed(4);
        aggregatedRate.buyVariance = this.convertNumberToSignedStringNumber(rate.evolution);
        aggregatedRate.buyPercentVariance = this.convertNumberToSignedStringNumber(this.computeVariance(rate.value, rate.evolution));
    }

    private computeAggregatedSellRateValues(aggregatedRate:AggregatedRate, rate:Rate) {
        aggregatedRate.sellValue = rate.value.toFixed(4);
        aggregatedRate.sellVariance = this.convertNumberToSignedStringNumber(rate.evolution);
        aggregatedRate.sellPercentVariance = this.convertNumberToSignedStringNumber(this.computeVariance(rate.value, rate.evolution));
    }

    private convertNumberToSignedStringNumber(aNumber: number): string
    {
        if(aNumber > 0){
            return "+" + aNumber.toFixed(4);
        }else{
            return aNumber.toFixed(4);
        }
    }

    private computeVariance(value:number, evolution:number) {
        return (100 * evolution / (value - evolution));
    }
}