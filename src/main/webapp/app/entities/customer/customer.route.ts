import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { concatMap, flatMap, map } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ICustomer, Customer } from 'app/shared/model/customer.model';
import { CustomerService } from './customer.service';
import { CustomerComponent } from './customer.component';
import { CustomerDetailComponent } from './customer-detail.component';
import { CustomerUpdateComponent } from './customer-update.component';
import { AccountService } from 'app/core/auth/account.service';
import { IGarage } from 'app/shared/model/garage.model';
import { GarageAdminService } from '../garage-admin/garage-admin.service';

@Injectable({ providedIn: 'root' })
export class CustomerResolve implements Resolve<ICustomer> {
  customers: ICustomer[] = [];

  constructor(
    private service: CustomerService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICustomer> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      this.accountService
        .getAuthenticationState()
        .pipe(
          concatMap(account =>
            this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
          ),
          concatMap(g => g.pipe(map(garages => garages))),
          concatMap(gs => {
            const garage = gs ? gs[0] : null;
            return this.service.queryGarageCustomers(garage?.id).pipe(map(cs => cs.body));
          })
        )
        .subscribe(cs => (this.customers = cs || []));

      return this.service.find(id).pipe(
        flatMap((customer: HttpResponse<Customer>) => {
          if (customer.body) {
            if (this.customers && this.customers.length > 0 && !this.customers.some(c => c.id === +id)) {
              this.router.navigate(['404']);
              return EMPTY;
            }
            return of(customer.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Customer());
  }
}

export const customerRoute: Routes = [
  {
    path: '',
    component: CustomerComponent,
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Customers',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CustomerDetailComponent,
    resolve: {
      customer: CustomerResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Customers',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CustomerUpdateComponent,
    resolve: {
      customer: CustomerResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Customers',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CustomerUpdateComponent,
    resolve: {
      customer: CustomerResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Customers',
    },
    canActivate: [UserRouteAccessService],
  },
];
