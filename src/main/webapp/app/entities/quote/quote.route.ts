import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { concatMap, flatMap, map } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IQuote, Quote } from 'app/shared/model/quote.model';
import { QuoteService } from './quote.service';
import { QuoteComponent } from './quote.component';
import { QuoteDetailComponent } from './quote-detail.component';
import { QuoteUpdateComponent } from './quote-update.component';
import { AccountService } from 'app/core/auth/account.service';
import { IGarage } from 'app/shared/model/garage.model';
import { GarageAdminService } from '../garage-admin/garage-admin.service';

@Injectable({ providedIn: 'root' })
export class QuoteResolve implements Resolve<IQuote> {
  garage!: IGarage;

  constructor(
    private service: QuoteService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IQuote> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      this.getAccountsGarage().subscribe(gs => (this.garage = gs![0]));

      return this.service.find(id).pipe(
        flatMap((quote: HttpResponse<Quote>) => {
          if (quote.body) {
            if (quote.body.booking?.garage && quote.body.booking?.garage.id !== this.garage?.id) {
              this.router.navigate(['404']);
              return EMPTY;
            }

            // Force to get jobs by booking
            this.service
              .queryQuoteJobs(quote.body.booking?.id, this.garage?.id)
              .pipe(map(res => res.body))
              .subscribe(ijobs => {
                if (quote.body?.booking) quote.body.booking.jobs = ijobs || [];
              });

            return of(quote.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }

    return of(new Quote());
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

export const quoteRoute: Routes = [
  {
    path: '',
    component: QuoteComponent,
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Quotes',
      defaultSort: 'quoteDate,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: QuoteDetailComponent,
    resolve: {
      quote: QuoteResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Quotes',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: QuoteUpdateComponent,
    resolve: {
      quote: QuoteResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Quotes',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: QuoteUpdateComponent,
    resolve: {
      quote: QuoteResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Quotes',
    },
    canActivate: [UserRouteAccessService],
  },
];
