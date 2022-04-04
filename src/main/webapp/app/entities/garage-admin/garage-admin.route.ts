import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IGarageAdmin, GarageAdmin } from 'app/shared/model/garage-admin.model';
import { GarageAdminService } from './garage-admin.service';
import { GarageAdminComponent } from './garage-admin.component';
import { GarageAdminDetailComponent } from './garage-admin-detail.component';
import { GarageAdminUpdateComponent } from './garage-admin-update.component';

@Injectable({ providedIn: 'root' })
export class GarageAdminResolve implements Resolve<IGarageAdmin> {
  constructor(private service: GarageAdminService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGarageAdmin> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((garageAdmin: HttpResponse<GarageAdmin>) => {
          if (garageAdmin.body) {
            return of(garageAdmin.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new GarageAdmin());
  }
}

export const garageAdminRoute: Routes = [
  {
    path: '',
    component: GarageAdminComponent,
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'GarageAdmins',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GarageAdminDetailComponent,
    resolve: {
      garageAdmin: GarageAdminResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'GarageAdmins',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GarageAdminUpdateComponent,
    resolve: {
      garageAdmin: GarageAdminResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'GarageAdmins',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GarageAdminUpdateComponent,
    resolve: {
      garageAdmin: GarageAdminResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'GarageAdmins',
    },
    canActivate: [UserRouteAccessService],
  },
];
