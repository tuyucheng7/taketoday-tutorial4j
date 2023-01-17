import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {GatewaySharedModule} from 'app/shared';
import {
	QuoteComponent,
	QuoteDeleteDialogComponent,
	QuoteDeletePopupComponent,
	QuoteDetailComponent,
	quotePopupRoute,
	quoteRoute,
	QuoteUpdateComponent
} from './';

const ENTITY_STATES = [...quoteRoute, ...quotePopupRoute];

@NgModule({
	imports: [GatewaySharedModule, RouterModule.forChild(ENTITY_STATES)],
	declarations: [QuoteComponent, QuoteDetailComponent, QuoteUpdateComponent, QuoteDeleteDialogComponent, QuoteDeletePopupComponent],
	entryComponents: [QuoteComponent, QuoteUpdateComponent, QuoteDeleteDialogComponent, QuoteDeletePopupComponent],
	schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GatewayQuoteModule {
}
