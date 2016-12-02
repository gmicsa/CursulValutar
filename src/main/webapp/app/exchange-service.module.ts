import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {HttpModule} from "@angular/http";
import {ExchangeServiceComponent} from "./exchange-service.component";

@NgModule({
    imports: [BrowserModule, HttpModule],
    declarations: [ExchangeServiceComponent],
    bootstrap: [ExchangeServiceComponent]
})
export class ExchangeServiceModule {
    
}