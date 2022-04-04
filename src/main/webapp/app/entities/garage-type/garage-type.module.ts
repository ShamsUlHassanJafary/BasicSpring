import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Gms4USharedModule } from 'app/shared/shared.module';
import { GarageTypeComponent } from './garage-type.component';
import { GarageTypeDetailComponent } from './garage-type-detail.component';
import { garageTypeRoute } from './garage-type.route';

@NgModule({
  imports: [Gms4USharedModule, RouterModule.forChild(garageTypeRoute)],
  declarations: [GarageTypeComponent, GarageTypeDetailComponent],
})
export class Gms4UGarageTypeModule {}
