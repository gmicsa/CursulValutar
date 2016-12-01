import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {HttpModule} from "@angular/http";
import {Ng2TableModule} from "ng2-table/ng2-table";
import {ExchangeServiceComponent} from "./exchange-service.component";

@NgModule({
    imports: [BrowserModule, HttpModule, Ng2TableModule],
    declarations: [ExchangeServiceComponent],
    bootstrap: [ExchangeServiceComponent]
})
export class ExchangeServiceModule {
    
}