import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap, map } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IVehicle, Vehicle } from 'app/shared/model/vehicle.model';
import { VehicleService } from './vehicle.service';
import { VehicleComponent } from './vehicle.component';
import { VehicleDetailComponent } from './vehicle-detail.component';
import { VehicleUpdateComponent } from './vehicle-update.component';
import { AccountService } from 'app/core/auth/account.service';
import { IGarage } from 'app/shared/model/garage.model';
import { GarageAdminService } from '../garage-admin/garage-admin.service';

@Injectable({ providedIn: 'root' })
export class VehicleResolve implements Resolve<IVehicle> {
  vehicles: IVehicle[] = [];

  constructor(
    private service: VehicleService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVehicle> | Observable<never> {
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
                  flatMap(gs => this.service.queryGarageVehicles(gs?.id).pipe(map(vs => vs.body)))
                )
              )
            )
          )
        )
        .subscribe(vs => (this.vehicles = vs || []));

      return this.service.find(id).pipe(
        flatMap((vehicle: HttpResponse<Vehicle>) => {
          if (vehicle.body) {
            if (this.vehicles && this.vehicles.length > 0 && !this.vehicles.some(v => v.id === +id)) {
              this.router.navigate(['404']);
              return EMPTY;
            }
            return of(vehicle.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Vehicle());
  }
}

export const vehicleRoute: Routes = [
  {
    path: '',
    component: VehicleComponent,
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Vehicles',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VehicleDetailComponent,
    resolve: {
      vehicle: VehicleResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Vehicles',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VehicleUpdateComponent,
    resolve: {
      vehicle: VehicleResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Vehicles',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VehicleUpdateComponent,
    resolve: {
      vehicle: VehicleResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Vehicles',
    },
    canActivate: [UserRouteAccessService],
  },
];
