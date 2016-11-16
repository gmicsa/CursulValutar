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
    
}
