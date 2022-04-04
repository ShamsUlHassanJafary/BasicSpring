import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IGarage, Garage } from 'app/shared/model/garage.model';
import { GarageService } from './garage.service';
import { GarageComponent } from './garage.component';
import { GarageDetailComponent } from './garage-detail.component';
import { GarageUpdateComponent } from './garage-update.component';

@Injectable({ providedIn: 'root' })
export class GarageResolve implements Resolve<IGarage> {
  constructor(private service: GarageService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGarage> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((garage: HttpResponse<Garage>) => {
          if (garage.body) {
            return of(garage.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Garage());
  }
}

export const garageRoute: Routes = [
  {
    path: '',
    component: GarageComponent,
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'Garages',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GarageDetailComponent,
    resolve: {
      garage: GarageResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'Garages',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GarageUpdateComponent,
    resolve: {
      garage: GarageResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'Garages',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GarageUpdateComponent,
    resolve: {
      garage: GarageResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'Garages',
    },
    canActivate: [UserRouteAccessService],
  },
];
