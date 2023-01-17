import {Routes} from '@angular/router';

import {
	auditsRoute,
	configurationRoute,
	docsRoute,
	healthRoute,
	logsRoute,
	metricsRoute,
	userDialogRoute,
	userMgmtRoute
} from './';

import {UserRouteAccessService} from '../shared';

let ADMIN_ROUTES = [
	auditsRoute,
	configurationRoute,
	docsRoute,
	healthRoute,
	logsRoute,
	...userMgmtRoute,
	metricsRoute
];


export const adminState: Routes = [{
	path: '',
	data: {
		authorities: ['ROLE_ADMIN']
	},
	canActivate: [UserRouteAccessService],
	children: ADMIN_ROUTES
},
	...userDialogRoute
];
