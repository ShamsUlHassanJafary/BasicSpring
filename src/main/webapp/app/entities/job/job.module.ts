import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Gms4USharedModule } from 'app/shared/shared.module';
import { JobComponent } from './job.component';
import { JobDetailComponent } from './job-detail.component';
import { JobUpdateComponent } from './job-update.component';
import { JobDeleteDialogComponent } from './job-delete-dialog.component';
import { jobRoute } from './job.route';

@NgModule({
  imports: [Gms4USharedModule, RouterModule.forChild(jobRoute)],
  declarations: [JobComponent, JobDetailComponent, JobUpdateComponent, JobDeleteDialogComponent],
  entryComponents: [JobDeleteDialogComponent],
})
export class Gms4UJobModule {}
