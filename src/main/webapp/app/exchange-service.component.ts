import {Component, OnInit} from "@angular/core";
import {NgTableComponent} from "ng2-table/ng2-table";
import {RatesService} from "./exchange-service.service";
import {Rate} from "./rate";

@Component({
    selector: 'exchange-service',
    template: `<h1>CursRapid.ro</h1>
        <div>Afli rapid cursul</div>
        <div *ngIf="!error">
            <ng-table [config]="config" [rows]="rows" [columns]="columns">
            </ng-table>
        </div>
        <div *ngIf="error">
            Error is {{this.error}}
        </div>
    `,
    directives: [NgTableComponent],
    providers: [RatesService]
})
export class ExchangeServiceComponent extends OnInit {
    rows: Array<Rate> = [];
    columns: Array<any> = this.initColumnsWithHeaders();
    rates: Rate[];
    error: any;

    public config: any = {
      paging: false,
      sorting: {columns: this.columns},
      className: ['table-striped', 'table-bordered']
    };
    
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
           },
            error => {
                this.error = error;
                this.rates = [];
            }
        )
            
    }

    private initColumnsWithHeaders(): Array<any> {
        return [
            {title: 'Bank', name: 'bank'},
            {title: 'Buy rate', name: 'buyRate'},
            {title: 'Sell rate', name: 'sellRate'},
            {title: 'Last update', name: 'lastUpdate'}
        ];
    }
    
}