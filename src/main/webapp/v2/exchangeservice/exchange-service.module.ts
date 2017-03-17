import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {HttpModule} from "@angular/http";
import {Ng2BootstrapModule} from "ng2-bootstrap/ng2-bootstrap";

import {ExchangeServiceComponent} from "./exchange-service.component";

@NgModule({
    imports: [BrowserModule, HttpModule, FormsModule, Ng2BootstrapModule],
    declarations: [ExchangeServiceComponent],
    bootstrap: [ExchangeServiceComponent]
})
export class ExchangeServiceModule {
}