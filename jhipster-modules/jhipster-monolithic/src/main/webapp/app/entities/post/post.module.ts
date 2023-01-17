import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {BaeldungSharedModule} from '../../shared';
import {BaeldungAdminModule} from '../../admin/admin.module';

import {
	PostComponent,
	PostDeleteDialogComponent,
	PostDeletePopupComponent,
	PostDetailComponent,
	PostDialogComponent,
	PostPopupComponent,
	postPopupRoute,
	PostPopupService,
	postRoute,
	PostService,
} from './';

let ENTITY_STATES = [
	...postRoute,
	...postPopupRoute,
];

@NgModule({
	imports: [
		BaeldungSharedModule,
		BaeldungAdminModule,
		RouterModule.forRoot(ENTITY_STATES, {useHash: true})
	],
	declarations: [
		PostComponent,
		PostDetailComponent,
		PostDialogComponent,
		PostDeleteDialogComponent,
		PostPopupComponent,
		PostDeletePopupComponent,
	],
	entryComponents: [
		PostComponent,
		PostDialogComponent,
		PostPopupComponent,
		PostDeleteDialogComponent,
		PostDeletePopupComponent,
	],
	providers: [
		PostService,
		PostPopupService,
	],
	schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BaeldungPostModule {
}
