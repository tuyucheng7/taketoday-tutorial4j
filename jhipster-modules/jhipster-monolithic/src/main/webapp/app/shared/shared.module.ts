import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {DatePipe} from '@angular/common';

import {CookieService} from 'angular2-cookie/services/cookies.service';
import {
	AccountService,
	AuthServerProvider,
	AuthService,
	BaeldungSharedCommonModule,
	BaeldungSharedLibsModule,
	CSRFService,
	HasAnyAuthorityDirective,
	JhiLoginModalComponent,
	LoginModalService,
	LoginService,
	Principal,
	StateStorageService,
	UserService
} from './';

@NgModule({
	imports: [
		BaeldungSharedLibsModule,
		BaeldungSharedCommonModule
	],
	declarations: [
		JhiLoginModalComponent,
		HasAnyAuthorityDirective
	],
	providers: [
		CookieService,
		LoginService,
		LoginModalService,
		AccountService,
		StateStorageService,
		Principal,
		CSRFService,
		AuthServerProvider,
		AuthService,
		UserService,
		DatePipe
	],
	entryComponents: [JhiLoginModalComponent],
	exports: [
		BaeldungSharedCommonModule,
		JhiLoginModalComponent,
		HasAnyAuthorityDirective,
		DatePipe
	],
	schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class BaeldungSharedModule {
}
