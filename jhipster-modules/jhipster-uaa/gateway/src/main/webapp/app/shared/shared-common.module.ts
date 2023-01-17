import {NgModule} from '@angular/core';

import {FindLanguageFromKeyPipe, GatewaySharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent} from './';

@NgModule({
	imports: [GatewaySharedLibsModule],
	declarations: [FindLanguageFromKeyPipe, JhiAlertComponent, JhiAlertErrorComponent],
	exports: [GatewaySharedLibsModule, FindLanguageFromKeyPipe, JhiAlertComponent, JhiAlertErrorComponent]
})
export class GatewaySharedCommonModule {
}
