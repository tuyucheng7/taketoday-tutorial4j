import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {BookstoreSharedModule} from 'app/shared';
import {
    BookComponent,
    BookDeleteDialogComponent,
    BookDeletePopupComponent,
    BookDetailComponent,
    bookPopupRoute,
    bookRoute,
    BookUpdateComponent
} from './';

const ENTITY_STATES = [...bookRoute, ...bookPopupRoute];

@NgModule({
    imports: [BookstoreSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [BookComponent, BookDetailComponent, BookUpdateComponent, BookDeleteDialogComponent, BookDeletePopupComponent],
    entryComponents: [BookComponent, BookUpdateComponent, BookDeleteDialogComponent, BookDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BookstoreBookModule {
}
