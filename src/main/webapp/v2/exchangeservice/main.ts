import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import {ExchangeServiceModule} from "./exchange-service.module";
const platform = platformBrowserDynamic();
platform.bootstrapModule(ExchangeServiceModule);