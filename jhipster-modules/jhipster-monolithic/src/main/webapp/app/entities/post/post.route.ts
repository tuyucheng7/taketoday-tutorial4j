import {Routes} from '@angular/router';

import {PostComponent} from './post.component';
import {PostDetailComponent} from './post-detail.component';
import {PostPopupComponent} from './post-dialog.component';
import {PostDeletePopupComponent} from './post-delete-dialog.component';


export const postRoute: Routes = [
	{
		path: 'post',
		component: PostComponent,
		data: {
			authorities: ['ROLE_USER'],
			pageTitle: 'baeldungApp.post.home.title'
		}
	}, {
		path: 'post/:id',
		component: PostDetailComponent,
		data: {
			authorities: ['ROLE_USER'],
			pageTitle: 'baeldungApp.post.home.title'
		}
	}
];

export const postPopupRoute: Routes = [
	{
		path: 'post-new',
		component: PostPopupComponent,
		data: {
			authorities: ['ROLE_USER'],
			pageTitle: 'baeldungApp.post.home.title'
		},
		outlet: 'popup'
	},
	{
		path: 'post/:id/edit',
		component: PostPopupComponent,
		data: {
			authorities: ['ROLE_USER'],
			pageTitle: 'baeldungApp.post.home.title'
		},
		outlet: 'popup'
	},
	{
		path: 'post/:id/delete',
		component: PostDeletePopupComponent,
		data: {
			authorities: ['ROLE_USER'],
			pageTitle: 'baeldungApp.post.home.title'
		},
		outlet: 'popup'
	}
];
