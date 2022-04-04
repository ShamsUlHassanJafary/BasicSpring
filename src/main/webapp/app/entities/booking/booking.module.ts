import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Gms4USharedModule } from 'app/shared/shared.module';
import { BookingComponent } from './booking.component';
import { BookingDetailComponent } from './booking-detail.component';
import { BookingUpdateComponent } from './booking-update.component';
import { BookingDeleteDialogComponent } from './booking-delete-dialog.component';
import { bookingRoute } from './booking.route';
import { SelectCreateVehicleModule } from '../select-create-vehicle/select-create-vehicle.module';
import { SelectCreateCustomerModule } from '../select-create-customer/select-create-customer.module';
@NgModule({
  imports: [Gms4USharedModule, SelectCreateVehicleModule, SelectCreateCustomerModule, RouterModule.forChild(bookingRoute)],
  declarations: [BookingComponent, BookingDetailComponent, BookingUpdateComponent, BookingDeleteDialogComponent],
  entryComponents: [BookingDeleteDialogComponent],
})
export class Gms4UBookingModule {}
