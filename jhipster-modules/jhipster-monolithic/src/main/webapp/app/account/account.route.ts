import {Routes} from '@angular/router';

import {
	activateRoute,
	passwordResetFinishRoute,
	passwordResetInitRoute,
	passwordRoute,
	registerRoute,
	settingsRoute
} from './';

let ACCOUNT_ROUTES = [
	activateRoute,
	passwordRoute,
	passwordResetFinishRoute,
	passwordResetInitRoute,
	registerRoute,
	settingsRoute
];

export const accountState: Routes = [{
	path: '',
	children: ACCOUNT_ROUTES
}];
