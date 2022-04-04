import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Gms4USharedModule } from 'app/shared/shared.module';
import { ReminderComponent } from './reminder.component';
import { ReminderDetailComponent } from './reminder-detail.component';
import { ReminderUpdateComponent } from './reminder-update.component';
import { ReminderDeleteDialogComponent } from './reminder-delete-dialog.component';
import { reminderRoute } from './reminder.route';
import { SelectCreateCustomerModule } from '../select-create-customer/select-create-customer.module';
import { SelectCreateVehicleModule } from '../select-create-vehicle/select-create-vehicle.module';

@NgModule({
  imports: [Gms4USharedModule, SelectCreateVehicleModule, SelectCreateCustomerModule, RouterModule.forChild(reminderRoute)],
  declarations: [ReminderComponent, ReminderDetailComponent, ReminderUpdateComponent, ReminderDeleteDialogComponent],
  entryComponents: [ReminderDeleteDialogComponent],
})
export class Gms4UReminderModule {}
