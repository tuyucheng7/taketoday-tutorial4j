import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {NgbDateAdapter} from '@ng-bootstrap/ng-bootstrap';

import {NgbDateMomentAdapter} from './util/datepicker-adapter';
import {
    BookstoreSharedCommonModule,
    BookstoreSharedLibsModule,
    HasAnyAuthorityDirective,
    JhiLoginModalComponent
} from './';

@NgModule({
    imports: [BookstoreSharedLibsModule, BookstoreSharedCommonModule],
    declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
    providers: [{provide: NgbDateAdapter, useClass: NgbDateMomentAdapter}],
    entryComponents: [JhiLoginModalComponent],
    exports: [BookstoreSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BookstoreSharedModule {
    static forRoot() {
        return {
            ngModule: BookstoreSharedModule
        };
    }
}
