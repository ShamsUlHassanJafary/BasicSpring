import { NgModule } from '@angular/core';
import { Gms4USharedModule } from 'app/shared/shared.module';
import { SelectCreateVehicleComponent } from './select-create-vehicle.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    Gms4USharedModule,
    RouterModule.forChild([
      {
        path: 'vehicle',
        loadChildren: () => import('../vehicle/vehicle.module').then(m => m.Gms4UVehicleModule),
      },
    ]),
  ],
  declarations: [SelectCreateVehicleComponent],
  exports: [SelectCreateVehicleComponent],
})
export class SelectCreateVehicleModule {}
