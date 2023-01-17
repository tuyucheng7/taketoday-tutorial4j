import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {NgbDateAdapter} from '@ng-bootstrap/ng-bootstrap';

import {NgbDateMomentAdapter} from './util/datepicker-adapter';
import {GatewaySharedCommonModule, GatewaySharedLibsModule, HasAnyAuthorityDirective, JhiLoginModalComponent} from './';

@NgModule({
	imports: [GatewaySharedLibsModule, GatewaySharedCommonModule],
	declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
	providers: [{provide: NgbDateAdapter, useClass: NgbDateMomentAdapter}],
	entryComponents: [JhiLoginModalComponent],
	exports: [GatewaySharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
	schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GatewaySharedModule {
}
