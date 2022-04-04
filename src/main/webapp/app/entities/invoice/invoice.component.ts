import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { combineLatest, of, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IInvoice, Invoice } from 'app/shared/model/invoice.model';
import { InvoiceService } from './invoice.service';
import { InvoiceDeleteDialogComponent } from './invoice-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { IGarage } from 'app/shared/model/garage.model';
import { concatMap, map, flatMap } from 'rxjs/operators';
import { ActivatedRoute, Router, Data, ParamMap } from '@angular/router';
import { IQuote } from 'app/shared/model/quote.model';
import { QuoteService } from '../quote/quote.service';
import { IBooking } from 'app/shared/model/booking.model';
import { BookingService } from '../booking/booking.service';
import { BookingStatus } from 'app/shared/model/enumerations/booking-status.model';

@Component({
  selector: 'jhi-invoice',
  templateUrl: './invoice.component.html',
})
export class InvoiceComponent implements OnInit, OnDestroy {
  invoices?: IInvoice[];
  eventSubscriber?: Subscription;
  quoteEventSubscriber?: Subscription;
  bookingEventSubscriber?: Subscription;

  existingQuotes: IQuote[] = [];
  existingBookings: IBooking[] = [];

  filter: any = { booking: { searchTag: '' } };

  predicate!: string;
  ascending!: boolean;

  constructor(
    protected invoiceService: InvoiceService,
    protected quoteService: QuoteService,
    protected bookingService: BookingService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    private activatedRoute: ActivatedRoute,
    protected router: Router
  ) {}

  loadAll(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(
        concatMap(account =>
          this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
        ),
        concatMap(g => g.pipe(map(garages => garages)))
      )
      .subscribe(gs => {
        if (gs && gs.length > 0) {
          this.loadAllGarageInvoices(gs);
          this.loadAllGarageQuotes(gs);
          this.loadAllGarageBookings(gs);
        }
      });
  }

  private loadAllGarageBookings(gs: IGarage[]): void {
    this.bookingService
      .queryGarageBookings(gs[0].id, { statuses: [BookingStatus.COMPLETED] })
      .pipe(
        flatMap(
          (bookingResponse: HttpResponse<IBooking[]>) => of(bookingResponse.body)
          // .pipe(
          //   flatMap(qs =>
          //     of(
          //       qs?.map(q => {
          //         // Force to get jobs by booking
          //         this.loadJobsByBooking(q, gs);
          //         return q;
          //       })
          //     )
          //   )
          // )
        )
      )
      .subscribe(gb => {
        if (gb && gb.length > 0) {
          this.existingBookings = gb;
          this.existingBookings?.sort((a, b) => {
            const left = a?.id;
            const right = b.id;
            return left && right ? left - right : 0;
          });
        }
      });
  }

  private loadAllGarageQuotes(gs: IGarage[]): void {
    this.quoteService
      .queryGarageQuotations(gs[0].id)
      .pipe(
        flatMap(
          (quotesResponse: HttpResponse<IQuote[]>) => of(quotesResponse.body)
          // .pipe(
          //   flatMap(qs =>
          //     of(
          //       qs?.map(q => {
          //         // Force to get jobs by booking
          //         this.loadJobsByBooking(q, gs);
          //         return q;
          //       })
          //     )
          //   )
          // )
        )
      )
      .subscribe(gq => {
        if (gq && gq.length > 0) {
          this.existingQuotes = gq;
          this.existingQuotes?.sort((a, b) => {
            const left = a?.id;
            const right = b.id;
            return left && right ? left - right : 0;
          });
        }
      });
  }

  private loadAllGarageInvoices(gs: IGarage[]): void {
    this.invoiceService
      .queryGarageInvoices(gs[0].id, { sort: this.sort() })
      .pipe(
        flatMap((invoicesResponse: HttpResponse<Invoice[]>) =>
          of(invoicesResponse.body).pipe(
            flatMap(invoices =>
              of(
                invoices?.map(i => {
                  // Force to get jobs by booking
                  this.loadJobsByBooking(i, gs);
                  return i;
                })
              )
            )
          )
        )
      )
      .subscribe(gi => {
        this.invoices = [];
        if (gi && gi.length > 0) {
          this.invoices = gi;
          this.invoices.forEach(b => {
            const booking = b.booking;
            booking!.searchTag = booking!.vehicle?.registration + ', ' + booking!.customer?.firstName + ' ' + booking!.customer?.lastName;
            return b;
          });
        }
      });
  }

  private loadJobsByBooking(i: Invoice, gs: IGarage[]): void {
    if (i.booking) {
      this.invoiceService
        .queryInvoiceJobs(i.booking.id, gs[0].id)
        .pipe(map(res => res.body))
        .subscribe(ijobs => {
          if (i.booking && ijobs) i.booking.jobs = ijobs;
        });
    }
  }

  onQuoteChange(selectedQuote: IQuote): void {
    this.router.navigate(['/invoice/quote', selectedQuote.id]);
  }

  onBookingChange(selectedBooking: IBooking): void {
    this.router.navigate(['/invoice/booking', selectedBooking.id]);
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInInvoices();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }

    if (this.quoteEventSubscriber) {
      this.eventManager.destroy(this.quoteEventSubscriber);
    }

    if (this.bookingEventSubscriber) {
      this.eventManager.destroy(this.bookingEventSubscriber);
    }
  }

  trackId(index: number, item: IInvoice): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInInvoices(): void {
    this.eventSubscriber = this.eventManager.subscribe('invoiceListModification', () => this.loadAll());
    this.bookingEventSubscriber = this.eventManager.subscribe('bookingListModification', () => this.loadAll());
    this.quoteEventSubscriber = this.eventManager.subscribe('quoteListModification', () => this.loadAll());
  }

  delete(invoice: IInvoice): void {
    const modalRef = this.modalService.open(InvoiceDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.invoice = invoice;
  }

  private handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      this.predicate = sort[0];
      this.ascending = sort[1] === 'asc';
      this.loadAll();
    }).subscribe();
  }

  transition(): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
      },
    });
  }

  private sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('reference');
    }
    return result;
  }
}
