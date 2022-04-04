import { NgModule } from '@angular/core';
import { Gms4USharedModule } from 'app/shared/shared.module';
import { SelectCreateCustomerComponent } from './select-create-customer.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    Gms4USharedModule,
    RouterModule.forChild([
      {
        path: 'customer',
        loadChildren: () => import('../customer/customer.module').then(m => m.Gms4UCustomerModule),
      },
    ]),
  ],
  declarations: [SelectCreateCustomerComponent],
  exports: [SelectCreateCustomerComponent],
})
export class SelectCreateCustomerModule {}
