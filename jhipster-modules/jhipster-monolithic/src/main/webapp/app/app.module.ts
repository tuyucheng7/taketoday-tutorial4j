import './vendor.ts';

import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {Ng2Webstorage} from 'ng2-webstorage';

import {BaeldungSharedModule, UserRouteAccessService} from './shared';
import {BaeldungHomeModule} from './home/home.module';
import {BaeldungAdminModule} from './admin/admin.module';
import {BaeldungAccountModule} from './account/account.module';
import {BaeldungEntityModule} from './entities/entity.module';

import {
	ActiveMenuDirective,
	ErrorComponent,
	FooterComponent,
	JhiMainComponent,
	LayoutRoutingModule,
	NavbarComponent,
	PageRibbonComponent,
	ProfileService
} from './layouts';
import {customHttpProvider} from './blocks/interceptor/http.provider';
import {PaginationConfig} from './blocks/config/uib-pagination.config';


@NgModule({
	imports: [
		BrowserModule,
		LayoutRoutingModule,
		Ng2Webstorage.forRoot({prefix: 'jhi', separator: '-'}),
		BaeldungSharedModule,
		BaeldungHomeModule,
		BaeldungAdminModule,
		BaeldungAccountModule,
		BaeldungEntityModule
	],
	declarations: [
		JhiMainComponent,
		NavbarComponent,
		ErrorComponent,
		PageRibbonComponent,
		ActiveMenuDirective,
		FooterComponent
	],
	providers: [
		ProfileService,
		{provide: Window, useValue: window},
		{provide: Document, useValue: document},
		customHttpProvider(),
		PaginationConfig,
		UserRouteAccessService
	],
	bootstrap: [JhiMainComponent]
})
export class BaeldungAppModule {
}
