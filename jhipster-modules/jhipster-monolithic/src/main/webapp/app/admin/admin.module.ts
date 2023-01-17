import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {BaeldungSharedModule} from '../shared';

import {
	adminState,
	AuditsComponent,
	AuditsService,
	JhiConfigurationComponent,
	JhiConfigurationService,
	JhiDocsComponent,
	JhiHealthCheckComponent,
	JhiHealthModalComponent,
	JhiHealthService,
	JhiMetricsMonitoringComponent,
	JhiMetricsMonitoringModalComponent,
	JhiMetricsService,
	LogsComponent,
	LogsService,
	UserDeleteDialogComponent,
	UserDialogComponent,
	UserMgmtComponent,
	UserMgmtDeleteDialogComponent,
	UserMgmtDetailComponent,
	UserMgmtDialogComponent,
	UserModalService,
	UserResolve,
	UserResolvePagingParams
} from './';


@NgModule({
	imports: [
		BaeldungSharedModule,
		RouterModule.forRoot(adminState, {useHash: true})
	],
	declarations: [
		AuditsComponent,
		UserMgmtComponent,
		UserDialogComponent,
		UserDeleteDialogComponent,
		UserMgmtDetailComponent,
		UserMgmtDialogComponent,
		UserMgmtDeleteDialogComponent,
		LogsComponent,
		JhiConfigurationComponent,
		JhiHealthCheckComponent,
		JhiHealthModalComponent,
		JhiDocsComponent,
		JhiMetricsMonitoringComponent,
		JhiMetricsMonitoringModalComponent
	],
	entryComponents: [
		UserMgmtDialogComponent,
		UserMgmtDeleteDialogComponent,
		JhiHealthModalComponent,
		JhiMetricsMonitoringModalComponent,
	],
	providers: [
		AuditsService,
		JhiConfigurationService,
		JhiHealthService,
		JhiMetricsService,
		LogsService,
		UserResolvePagingParams,
		UserResolve,
		UserModalService
	],
	schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BaeldungAdminModule {
}
