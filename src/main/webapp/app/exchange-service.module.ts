import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {HttpModule} from "@angular/http";
import {DatePickerModule} from "ng2-datepicker";
import {ExchangeServiceComponent} from "./exchange-service.component";

@NgModule({
    imports: [BrowserModule, HttpModule, DatePickerModule],
    declarations: [ExchangeServiceComponent],
    bootstrap: [ExchangeServiceComponent]
})
export class ExchangeServiceModule {
    
}