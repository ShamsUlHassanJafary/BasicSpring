import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Gms4USharedModule } from 'app/shared/shared.module';
import { QuoteComponent } from './quote.component';
import { QuoteDetailComponent } from './quote-detail.component';
import { QuoteUpdateComponent } from './quote-update.component';
import { QuoteDeleteDialogComponent } from './quote-delete-dialog.component';
import { quoteRoute } from './quote.route';
import { SelectCreateVehicleModule } from '../select-create-vehicle/select-create-vehicle.module';
import { SelectCreateCustomerModule } from '../select-create-customer/select-create-customer.module';
@NgModule({
  imports: [Gms4USharedModule, SelectCreateVehicleModule, SelectCreateCustomerModule, RouterModule.forChild(quoteRoute)],
  declarations: [QuoteComponent, QuoteDetailComponent, QuoteUpdateComponent, QuoteDeleteDialogComponent],
  entryComponents: [QuoteDeleteDialogComponent],
})
export class Gms4UQuoteModule {}
