import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {HttpModule} from "@angular/http";
import {DatePicker} from "ng2-datepicker/ng2-datepicker";
import {SelectModule} from "ng2-select/ng2-select";
import {ExchangeServiceComponent} from "./exchange-service.component";

@NgModule({
    imports: [BrowserModule, HttpModule, SelectModule],
    declarations: [ExchangeServiceComponent, DatePicker],
    bootstrap: [ExchangeServiceComponent]
})
export class ExchangeServiceModule {
    
}