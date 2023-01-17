import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {BookstoreSharedModule} from 'app/shared';

import {
    accountState,
    ActivateComponent,
    PasswordComponent,
    PasswordResetFinishComponent,
    PasswordResetInitComponent,
    PasswordStrengthBarComponent,
    RegisterComponent,
    SettingsComponent
} from './';

@NgModule({
    imports: [BookstoreSharedModule, RouterModule.forChild(accountState)],
    declarations: [
        ActivateComponent,
        RegisterComponent,
        PasswordComponent,
        PasswordStrengthBarComponent,
        PasswordResetInitComponent,
        PasswordResetFinishComponent,
        SettingsComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BookstoreAccountModule {
}
