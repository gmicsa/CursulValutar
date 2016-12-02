/**
 * Created by alexands on 16.11.2016.
 */
export class Rate {
    id: number;
    date: string;
    value: number;
    currencyType: string;
    transactionType: string;
    bankName: string;
    evolution: number;
    lastChangedAt: string;

    constructor(item: Object) {
        Object.assign(this, item);
    }

    public static haveSameBankName(rate1:Rate, rate2:Rate) {
        return rate1.bankName == rate2.bankName;
    }

    static haveDifferentBankNames(rate1:Rate, rate2:Rate) {
        return ! Rate.haveSameBankName(rate1, rate2);
    }
    
}
