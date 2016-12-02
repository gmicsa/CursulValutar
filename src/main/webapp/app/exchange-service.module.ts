import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {HttpModule} from "@angular/http";
import {DataTableModule} from "angular2-datatable";
import {ExchangeServiceComponent} from "./exchange-service.component";

@NgModule({
    imports: [BrowserModule, HttpModule, DataTableModule],
    declarations: [ExchangeServiceComponent],
    bootstrap: [ExchangeServiceComponent]
})
export class ExchangeServiceModule {
    
}