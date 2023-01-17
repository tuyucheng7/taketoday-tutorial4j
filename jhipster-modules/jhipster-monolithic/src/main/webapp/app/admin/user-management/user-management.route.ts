import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Resolve, RouterStateSnapshot, Routes} from '@angular/router';

import {Principal} from '../../shared';
import {PaginationUtil} from 'ng-jhipster';

import {UserMgmtComponent} from './user-management.component';
import {UserMgmtDetailComponent} from './user-management-detail.component';
import {UserDialogComponent} from './user-management-dialog.component';
import {UserDeleteDialogComponent} from './user-management-delete-dialog.component';


@Injectable()
export class UserResolve implements CanActivate {

	constructor(private principal: Principal) {
	}

	canActivate() {
		return this.principal.identity().then(account => this.principal.hasAnyAuthority(['ROLE_ADMIN']));
	}
}

@Injectable()
export class UserResolvePagingParams implements Resolve<any> {

	constructor(private paginationUtil: PaginationUtil) {
	}

	resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
		let page = route.queryParams['page'] ? route.queryParams['page'] : '1';
		let sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
		return {
			page: this.paginationUtil.parsePage(page),
			predicate: this.paginationUtil.parsePredicate(sort),
			ascending: this.paginationUtil.parseAscending(sort)
		};
	}
}

export const userMgmtRoute: Routes = [
	{
		path: 'user-management',
		component: UserMgmtComponent,
		resolve: {
			'pagingParams': UserResolvePagingParams
		},
		data: {
			pageTitle: 'userManagement.home.title'
		}
	},
	{
		path: 'user-management/:login',
		component: UserMgmtDetailComponent,
		data: {
			pageTitle: 'userManagement.home.title'
		}
	}
];

export const userDialogRoute: Routes = [
	{
		path: 'user-management-new',
		component: UserDialogComponent,
		outlet: 'popup'
	},
	{
		path: 'user-management/:login/edit',
		component: UserDialogComponent,
		outlet: 'popup'
	},
	{
		path: 'user-management/:login/delete',
		component: UserDeleteDialogComponent,
		outlet: 'popup'
	}
];
