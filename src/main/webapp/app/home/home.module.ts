import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Gms4USharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { DashboardComponent } from '../entities/dashboard/dashboard.component';

@NgModule({
  imports: [Gms4USharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent, DashboardComponent],
})
export class Gms4UHomeModule {}
