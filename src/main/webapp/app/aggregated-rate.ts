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
    
    private constructor() {
    }

    public static createAggregatedRate(buyRate: Rate, sellRate: Rate): AggregatedRate {
        AggregatedRate.checkMatchingRates(buyRate, sellRate);
        
        var aggregatedRate = new AggregatedRate();
        aggregatedRate.bankName = buyRate.bankName;
        aggregatedRate.currencyType = buyRate.currencyType;
        aggregatedRate.lastChangedAt = buyRate.lastChangedAt;
        this.computeAggregatedBuyRateValues(aggregatedRate, buyRate);
        this.computeAggregatedSellRateValues(aggregatedRate, sellRate);

        return aggregatedRate;
    }

    private static computeAggregatedBuyRateValues(aggregatedRate:AggregatedRate, rate:Rate) {
        aggregatedRate.buyValue = rate.value.toFixed(4);
        aggregatedRate.buyVariance = this.convertNumberToSignedStringNumber(rate.evolution);
        aggregatedRate.buyPercentVariance = this.convertNumberToSignedStringNumber(this.computeVariance(rate.value, rate.evolution));
    }

    private static computeAggregatedSellRateValues(aggregatedRate:AggregatedRate, rate:Rate) {
        aggregatedRate.sellValue = rate.value.toFixed(4);
        aggregatedRate.sellVariance = this.convertNumberToSignedStringNumber(rate.evolution);
        aggregatedRate.sellPercentVariance = this.convertNumberToSignedStringNumber(this.computeVariance(rate.value, rate.evolution));
    }

    private static checkMatchingRates(buyRate:Rate, sellRate:Rate) {
        if(Rate.haveDifferentBankNames(buyRate, sellRate)) {
            throw new Error("Buy rate bank name does not match sell rate bank name");
        }
        if(buyRate.currencyType !== sellRate.currencyType) {
            throw new Error("Buy rate currency type does not match sell rate currency type");
        }
    }

    private static convertNumberToSignedStringNumber(aNumber: number): string
    {
        if(aNumber > 0){
            return "+" + aNumber.toFixed(4);
        }else{
            return aNumber.toFixed(4);
        }
    }

    private static computeVariance(value:number, evolution:number) {
        return (100 * evolution / (value - evolution));
    }
}