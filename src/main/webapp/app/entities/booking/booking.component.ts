import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { combineLatest, of, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { IBooking } from 'app/shared/model/booking.model';
import { BookingService } from './booking.service';
import { BookingDeleteDialogComponent } from './booking-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { concatMap, flatMap, map } from 'rxjs/operators';
import { IGarage } from 'app/shared/model/garage.model';
import { BookingStatus } from 'app/shared/model/enumerations/booking-status.model';
import { IQuote } from 'app/shared/model/quote.model';
import { QuoteService } from '../quote/quote.service';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
@Component({
  selector: 'jhi-booking',
  templateUrl: './booking.component.html',
})
export class BookingComponent implements OnInit, OnDestroy {
  bookings?: IBooking[];
  eventSubscriber?: Subscription;

  existingQuotes: IQuote[] = [];

  predicate!: string;
  ascending!: boolean;

  constructor(
    protected bookingService: BookingService,
    protected quoteService: QuoteService,
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
          this.loadAllGarageBookings(gs);
          this.loadAllGarageQuotes(gs);
        }
      });
  }

  private loadAllGarageQuotes(gs: IGarage[]): void {
    this.existingQuotes = [];
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

  private loadAllGarageBookings(gs: IGarage[]): void {
    this.bookings = [];
    this.bookingService
      .queryGarageBookings(gs[0].id, {
        statuses: [BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS, BookingStatus.COMPLETED],
        sort: this.sort(),
      })
      .pipe(flatMap((bookingsResponse: HttpResponse<IBooking[]>) => of(bookingsResponse.body)))
      .subscribe(gb => {
        if (gb && gb.length > 0) {
          gb.forEach(booking => {
            this.loadJobsByBooking(booking, gs);
          });
          this.bookings = gb;
        }
      });
  }

  private loadJobsByBooking(booking: IBooking, gs: IGarage[]): void {
    if (booking) {
      this.bookingService
        .queryBookingJobs(booking.id, gs[0].id)
        .pipe(map(res => res.body))
        .subscribe(ijobs => {
          if (booking && ijobs) booking.jobs = ijobs;
        });
    }
  }

  onQuoteChange(selectedQuote: IQuote): void {
    this.router.navigate(['/booking', selectedQuote.booking?.id, 'edit']);
  }

  ngOnInit(): void {
    this.handleNavigation();
    // this.loadAll();
    this.registerChangeInBookings();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
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

  trackId(index: number, item: IBooking): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInBookings(): void {
    this.eventSubscriber = this.eventManager.subscribe('bookingListModification', () => this.loadAll());
  }

  delete(booking: IBooking): void {
    const modalRef = this.modalService.open(BookingDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.booking = booking;
  }

  isComplete(booking: IBooking): boolean {
    return booking?.status === BookingStatus.COMPLETED || booking?.status === BookingStatus.INVOICED;
  }
}
