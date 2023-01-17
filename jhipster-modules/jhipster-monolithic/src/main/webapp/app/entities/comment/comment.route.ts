import {Routes} from '@angular/router';

import {CommentComponent} from './comment.component';
import {CommentDetailComponent} from './comment-detail.component';
import {CommentPopupComponent} from './comment-dialog.component';
import {CommentDeletePopupComponent} from './comment-delete-dialog.component';


export const commentRoute: Routes = [
	{
		path: 'comment',
		component: CommentComponent,
		data: {
			authorities: ['ROLE_USER'],
			pageTitle: 'baeldungApp.comment.home.title'
		}
	}, {
		path: 'comment/:id',
		component: CommentDetailComponent,
		data: {
			authorities: ['ROLE_USER'],
			pageTitle: 'baeldungApp.comment.home.title'
		}
	}
];

export const commentPopupRoute: Routes = [
	{
		path: 'comment-new',
		component: CommentPopupComponent,
		data: {
			authorities: ['ROLE_USER'],
			pageTitle: 'baeldungApp.comment.home.title'
		},
		outlet: 'popup'
	},
	{
		path: 'comment/:id/edit',
		component: CommentPopupComponent,
		data: {
			authorities: ['ROLE_USER'],
			pageTitle: 'baeldungApp.comment.home.title'
		},
		outlet: 'popup'
	},
	{
		path: 'comment/:id/delete',
		component: CommentDeletePopupComponent,
		data: {
			authorities: ['ROLE_USER'],
			pageTitle: 'baeldungApp.comment.home.title'
		},
		outlet: 'popup'
	}
];
