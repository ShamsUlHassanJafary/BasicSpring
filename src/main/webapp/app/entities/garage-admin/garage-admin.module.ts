import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Gms4USharedModule } from 'app/shared/shared.module';
import { GarageAdminComponent } from './garage-admin.component';
import { GarageAdminDetailComponent } from './garage-admin-detail.component';
import { GarageAdminUpdateComponent } from './garage-admin-update.component';
import { GarageAdminDeleteDialogComponent } from './garage-admin-delete-dialog.component';
import { garageAdminRoute } from './garage-admin.route';

@NgModule({
  imports: [Gms4USharedModule, RouterModule.forChild(garageAdminRoute)],
  declarations: [GarageAdminComponent, GarageAdminDetailComponent, GarageAdminUpdateComponent, GarageAdminDeleteDialogComponent],
  entryComponents: [GarageAdminDeleteDialogComponent],
})
export class Gms4UGarageAdminModule {}
