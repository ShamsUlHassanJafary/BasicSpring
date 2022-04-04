import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap, map } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IJob, Job } from 'app/shared/model/job.model';
import { JobService } from './job.service';
import { JobComponent } from './job.component';
import { JobDetailComponent } from './job-detail.component';
import { JobUpdateComponent } from './job-update.component';
import { AccountService } from 'app/core/auth/account.service';
import { IGarage } from 'app/shared/model/garage.model';
import { GarageAdminService } from '../garage-admin/garage-admin.service';

@Injectable({ providedIn: 'root' })
export class JobResolve implements Resolve<IJob> {
  jobs: IJob[] = [];
  constructor(
    private service: JobService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IJob> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      this.accountService
        .getAuthenticationState()
        .pipe(
          flatMap(account =>
            this.garageAdminService.findGaragesByAccount(account!.login).pipe(
              map((res: HttpResponse<IGarage[]>) => of(res.body)),
              flatMap(g =>
                g.pipe(
                  map(garages => (garages ? garages[0] : null)),
                  flatMap(gs => this.service.queryGarageJobs(gs?.id).pipe(map(js => js.body)))
                )
              )
            )
          )
        )
        .subscribe(vs => (this.jobs = vs || []));
      return this.service.find(id).pipe(
        flatMap((job: HttpResponse<Job>) => {
          if (job.body) {
            if (this.jobs && this.jobs.length > 0 && !this.jobs.some(j => j.id + '' === id)) {
              this.router.navigate(['404']);
              return EMPTY;
            }
            return of(job.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Job());
  }
}

export const jobRoute: Routes = [
  {
    path: '',
    component: JobComponent,
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Jobs',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: JobDetailComponent,
    resolve: {
      job: JobResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Jobs',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: JobUpdateComponent,
    resolve: {
      job: JobResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Jobs',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: JobUpdateComponent,
    resolve: {
      job: JobResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Jobs',
    },
    canActivate: [UserRouteAccessService],
  },
];
