import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'booking',
        loadChildren: () => import('./booking/booking.module').then(m => m.Gms4UBookingModule),
      },
      {
        path: 'customer',
        loadChildren: () => import('./customer/customer.module').then(m => m.Gms4UCustomerModule),
      },
      {
        path: 'garage',
        loadChildren: () => import('./garage/garage.module').then(m => m.Gms4UGarageModule),
      },
      {
        path: 'garage-admin',
        loadChildren: () => import('./garage-admin/garage-admin.module').then(m => m.Gms4UGarageAdminModule),
      },
      {
        path: 'invoice',
        loadChildren: () => import('./invoice/invoice.module').then(m => m.Gms4UInvoiceModule),
      },
      {
        path: 'job',
        loadChildren: () => import('./job/job.module').then(m => m.Gms4UJobModule),
      },
      {
        path: 'quote',
        loadChildren: () => import('./quote/quote.module').then(m => m.Gms4UQuoteModule),
      },
      {
        path: 'vehicle',
        loadChildren: () => import('./vehicle/vehicle.module').then(m => m.Gms4UVehicleModule),
      },
      {
        path: 'reminder',
        loadChildren: () => import('./reminder/reminder.module').then(m => m.Gms4UReminderModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class Gms4UEntityModule {}
