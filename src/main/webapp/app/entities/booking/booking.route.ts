import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { concatMap, flatMap, map } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IBooking, Booking } from 'app/shared/model/booking.model';
import { BookingService } from './booking.service';
import { BookingComponent } from './booking.component';
import { BookingDetailComponent } from './booking-detail.component';
import { BookingUpdateComponent } from './booking-update.component';
import { AccountService } from 'app/core/auth/account.service';
import { IGarage } from 'app/shared/model/garage.model';
import { GarageAdminService } from '../garage-admin/garage-admin.service';

@Injectable({ providedIn: 'root' })
export class BookingResolve implements Resolve<IBooking> {
  garage!: IGarage;

  constructor(
    private service: BookingService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBooking> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      this.getAccountsGarage().subscribe(gs => (this.garage = gs![0]));

      return this.service.find(id).pipe(
        flatMap((booking: HttpResponse<Booking>) => {
          if (booking.body) {
            if (booking.body?.garage && booking.body?.garage.id !== this.garage?.id) {
              this.router.navigate(['404']);
              return EMPTY;
            }

            return of(booking.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Booking());
  }

  private getAccountsGarage(): Observable<IGarage[] | null> {
    return this.accountService.getAuthenticationState().pipe(
      concatMap(account =>
        this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
      ),
      concatMap(g => g.pipe(map(garages => garages)))
    );
  }
}

export const bookingRoute: Routes = [
  {
    path: '',
    component: BookingComponent,
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Bookings',
      defaultSort: 'bookingDate,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BookingDetailComponent,
    resolve: {
      booking: BookingResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Bookings',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BookingUpdateComponent,
    resolve: {
      booking: BookingResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Bookings',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BookingUpdateComponent,
    resolve: {
      booking: BookingResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Bookings',
    },
    canActivate: [UserRouteAccessService],
  },
];
