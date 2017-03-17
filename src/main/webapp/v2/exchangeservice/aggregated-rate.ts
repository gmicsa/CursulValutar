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
    buyClassForCssLayout: string;

    sellValue: string;
    sellVariance: string;
    sellPercentVariance: string;
    sellClassForCssLayout: string;
    
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
        this.buyClassForCssLayout = rate.evolution >= 0 ? "greenSmall" : "redSmall";
        this.buyPercentVariance = ExchangeServiceUtils.convertNumberToSignedStringNumber(ExchangeServiceUtils.computeVariance(rate.value, rate.evolution));
    }

    private computeAggregatedSellRateValues(rate:Rate) {
        this.sellValue = rate.value.toFixed(4);
        this.sellVariance = ExchangeServiceUtils.convertNumberToSignedStringNumber(rate.evolution);
        this.sellClassForCssLayout = rate.evolution >= 0 ? "greenSmall" : "redSmall";
        this.sellPercentVariance = ExchangeServiceUtils.convertNumberToSignedStringNumber(ExchangeServiceUtils.computeVariance(rate.value, rate.evolution));
    }

}