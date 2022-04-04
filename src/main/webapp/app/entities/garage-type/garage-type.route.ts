import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IGarageType, GarageType } from 'app/shared/model/garage-type.model';
import { GarageTypeService } from './garage-type.service';
import { GarageTypeComponent } from './garage-type.component';
import { GarageTypeDetailComponent } from './garage-type-detail.component';

@Injectable({ providedIn: 'root' })
export class GarageTypeResolve implements Resolve<IGarageType> {
  constructor(private service: GarageTypeService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGarageType> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((garageType: HttpResponse<GarageType>) => {
          if (garageType.body) {
            return of(garageType.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new GarageType());
  }
}

export const garageTypeRoute: Routes = [
  {
    path: '',
    component: GarageTypeComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'GarageTypes',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GarageTypeDetailComponent,
    resolve: {
      garageType: GarageTypeResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'GarageTypes',
    },
    canActivate: [UserRouteAccessService],
  },
];
