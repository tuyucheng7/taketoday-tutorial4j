import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {JhiLanguageService} from 'ng-jhipster';
import {JhiLanguageHelper} from 'app/core';
import {GatewaySharedModule} from 'app/shared';
/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */
import {
	adminState,
	AuditsComponent,
	JhiConfigurationComponent,
	JhiDocsComponent,
	JhiGatewayComponent,
	JhiHealthCheckComponent,
	JhiHealthModalComponent,
	JhiMetricsMonitoringComponent,
	JhiMetricsMonitoringModalComponent,
	LogsComponent,
	UserMgmtComponent,
	UserMgmtDeleteDialogComponent,
	UserMgmtDetailComponent,
	UserMgmtUpdateComponent
} from './';

@NgModule({
	imports: [
		GatewaySharedModule,
		RouterModule.forChild(adminState)
		/* jhipster-needle-add-admin-module - JHipster will add admin modules here */
	],
	declarations: [
		AuditsComponent,
		UserMgmtComponent,
		UserMgmtDetailComponent,
		UserMgmtUpdateComponent,
		UserMgmtDeleteDialogComponent,
		LogsComponent,
		JhiConfigurationComponent,
		JhiHealthCheckComponent,
		JhiHealthModalComponent,
		JhiDocsComponent,
		JhiGatewayComponent,
		JhiMetricsMonitoringComponent,
		JhiMetricsMonitoringModalComponent
	],
	entryComponents: [UserMgmtDeleteDialogComponent, JhiHealthModalComponent, JhiMetricsMonitoringModalComponent],
	schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GatewayAdminModule {
	constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
		this.languageHelper.language.subscribe((languageKey: string) => {
			if (languageKey !== undefined) {
				this.languageService.changeLanguage(languageKey);
			}
		});
	}
}
