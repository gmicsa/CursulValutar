import {Rate} from "./rate";
import {ExchangeServiceUtils} from "./exchange-service.utils";

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
    
    constructor(buyRate: Rate, sellRate: Rate) {
        Rate.checkMatchingRates(buyRate, sellRate);

        this.bankName = buyRate.bankName;
        this.currencyType = buyRate.currencyType;
        this.lastChangedAt = buyRate.lastChangedAt;
        this.computeAggregatedBuyRateValues(buyRate);
        this.computeAggregatedSellRateValues(sellRate);
    }

    private computeAggregatedBuyRateValues(rate:Rate) {
        this.buyValue = rate.value.toFixed(4);
        this.buyVariance = ExchangeServiceUtils.convertNumberToSignedStringNumber(rate.evolution);
        this.buyPercentVariance = ExchangeServiceUtils.convertNumberToSignedStringNumber(ExchangeServiceUtils.computeVariance(rate.value, rate.evolution));
    }

    private computeAggregatedSellRateValues(rate:Rate) {
        this.sellValue = rate.value.toFixed(4);
        this.sellVariance = this.convertNumberToSignedStringNumber(rate.evolution);
        this.sellPercentVariance = this.convertNumberToSignedStringNumber(ExchangeServiceUtils.computeVariance(rate.value, rate.evolution));
    }

}