import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { concatMap, flatMap, map } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IInvoice, Invoice } from 'app/shared/model/invoice.model';
import { InvoiceService } from './invoice.service';
import { InvoiceComponent } from './invoice.component';
import { InvoiceDetailComponent } from './invoice-detail.component';
import { InvoiceUpdateComponent } from './invoice-update.component';
import { AccountService } from 'app/core/auth/account.service';
import { IGarage } from 'app/shared/model/garage.model';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { QuoteService } from '../quote/quote.service';
import { Quote } from 'app/shared/model/quote.model';
import { BookingService } from '../booking/booking.service';
import { Booking } from 'app/shared/model/booking.model';

@Injectable({ providedIn: 'root' })
export class InvoiceResolve implements Resolve<IInvoice> {
  garage!: IGarage;

  constructor(
    private service: InvoiceService,
    protected accountService: AccountService,
    protected bookingService: BookingService,
    protected quoteService: QuoteService,
    protected garageAdminService: GarageAdminService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IInvoice> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      this.getAccountsGarage().subscribe(gs => {
        this.garage = gs![0];
      });

      return this.service.find(id).pipe(
        flatMap((invoice: HttpResponse<Invoice>) => {
          if (invoice.body) {
            if (invoice.body?.booking?.garage && invoice.body?.booking?.garage.id !== this.garage?.id) {
              this.router.navigate(['404']);
              return EMPTY;
            }
            this.service
              .queryInvoiceJobs(invoice.body.booking?.id, this.garage?.id)
              .pipe(map(res => res.body))
              .subscribe(ijobs => {
                if (invoice.body?.booking) invoice.body.booking.jobs = ijobs || [];
              });

            return of(invoice.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }

    // Creating invoice from quote.
    const quoteId = route.params['quoteId'];
    if (quoteId) {
      return this.quoteService.find(quoteId).pipe(
        flatMap((quote: HttpResponse<Quote>) => {
          if (quote.body) {
            const quoteBooking = quote.body.booking;
            // Force to get jobs by booking
            this.service
              .queryInvoiceJobs(quoteBooking?.id, quoteBooking?.garage?.id)
              .pipe(map(res => res.body))
              .subscribe(ijobs => {
                if (quoteBooking) quoteBooking.jobs = ijobs || [];
              });
            const newInvoice: IInvoice = new Invoice();
            newInvoice.booking = quoteBooking;

            return of(newInvoice);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }

    const bookingId = route.params['bookingId'];
    if (bookingId) {
      return this.bookingService.find(bookingId).pipe(
        flatMap((booking: HttpResponse<Booking>) => {
          if (booking.body) {
            const bookingBody = booking.body;

            // Force to get jobs by booking
            this.service
              .queryInvoiceJobs(bookingBody?.id, bookingBody.garage?.id)
              .pipe(map(res => res.body))
              .subscribe(ijobs => {
                if (bookingBody) bookingBody.jobs = ijobs || [];
              });
            const newInvoice: IInvoice = new Invoice();
            newInvoice.booking = bookingBody;

            return of(newInvoice);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }

    return of(new Invoice());
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

export const invoiceRoute: Routes = [
  {
    path: '',
    component: InvoiceComponent,
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Invoices',
      defaultSort: 'issueDate,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InvoiceDetailComponent,
    resolve: {
      invoice: InvoiceResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Invoices',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InvoiceUpdateComponent,
    resolve: {
      invoice: InvoiceResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Invoices',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InvoiceUpdateComponent,
    resolve: {
      invoice: InvoiceResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Invoices',
    },
    canActivate: [UserRouteAccessService],
  },

  {
    path: 'quote/:quoteId',
    component: InvoiceUpdateComponent,
    resolve: {
      invoice: InvoiceResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Invoices',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'booking/:bookingId',
    component: InvoiceUpdateComponent,
    resolve: {
      invoice: InvoiceResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Invoices',
    },
    canActivate: [UserRouteAccessService],
  },
];
