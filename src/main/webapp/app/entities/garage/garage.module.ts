import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Gms4USharedModule } from 'app/shared/shared.module';
import { GarageComponent } from './garage.component';
import { GarageDetailComponent } from './garage-detail.component';
import { GarageUpdateComponent } from './garage-update.component';
import { GarageDeleteDialogComponent } from './garage-delete-dialog.component';
import { garageRoute } from './garage.route';

@NgModule({
  imports: [Gms4USharedModule, RouterModule.forChild(garageRoute)],
  declarations: [GarageComponent, GarageDetailComponent, GarageUpdateComponent, GarageDeleteDialogComponent],
  entryComponents: [GarageDeleteDialogComponent],
})
export class Gms4UGarageModule {}
