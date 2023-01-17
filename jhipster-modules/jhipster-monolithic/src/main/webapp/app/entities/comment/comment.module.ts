import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {BaeldungSharedModule} from '../../shared';

import {
	CommentComponent,
	CommentDeleteDialogComponent,
	CommentDeletePopupComponent,
	CommentDetailComponent,
	CommentDialogComponent,
	CommentPopupComponent,
	commentPopupRoute,
	CommentPopupService,
	commentRoute,
	CommentService,
} from './';

let ENTITY_STATES = [
	...commentRoute,
	...commentPopupRoute,
];

@NgModule({
	imports: [
		BaeldungSharedModule,
		RouterModule.forRoot(ENTITY_STATES, {useHash: true})
	],
	declarations: [
		CommentComponent,
		CommentDetailComponent,
		CommentDialogComponent,
		CommentDeleteDialogComponent,
		CommentPopupComponent,
		CommentDeletePopupComponent,
	],
	entryComponents: [
		CommentComponent,
		CommentDialogComponent,
		CommentPopupComponent,
		CommentDeleteDialogComponent,
		CommentDeletePopupComponent,
	],
	providers: [
		CommentService,
		CommentPopupService,
	],
	schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BaeldungCommentModule {
}
