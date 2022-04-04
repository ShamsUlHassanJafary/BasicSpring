import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { combineLatest, of, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IQuote } from 'app/shared/model/quote.model';
import { QuoteService } from './quote.service';
import { QuoteDeleteDialogComponent } from './quote-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { map, flatMap, concatMap } from 'rxjs/operators';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { IGarage } from 'app/shared/model/garage.model';
import { BookingService } from '../booking/booking.service';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';

@Component({
  selector: 'jhi-quote',
  templateUrl: './quote.component.html',
})
export class QuoteComponent implements OnInit, OnDestroy {
  quotes?: IQuote[];
  eventSubscriber?: Subscription;

  predicate!: string;
  ascending!: boolean;

  constructor(
    protected quoteService: QuoteService,
    protected accountService: AccountService,
    protected bookingService: BookingService,
    protected garageAdminService: GarageAdminService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    private activatedRoute: ActivatedRoute,
    protected router: Router
  ) {}

  loadAllGarageQuotations(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(
        concatMap(account =>
          this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
        ),
        concatMap(g => g.pipe(map(garages => garages)))
      )
      .subscribe(gs => {
        this.quotes = [];
        if (gs && gs.length > 0)
          this.quoteService
            .queryGarageQuotations(gs[0].id, { sort: this.sort() })
            .pipe(
              flatMap((quotesResponse: HttpResponse<IQuote[]>) =>
                of(quotesResponse.body).pipe(
                  flatMap(qs =>
                    of(
                      qs?.map(q => {
                        // Force to get jobs by booking
                        this.loadJobsByBooking(q, gs);
                        return q;
                      })
                    )
                  )
                )
              )
            )
            .subscribe(gq => {
              if (gq && gq.length > 0) {
                this.quotes = gq;
              }
            });
      });
  }

  private loadJobsByBooking(q: IQuote, gs: IGarage[]): void {
    if (q.booking) {
      this.quoteService
        .queryQuoteJobs(q.booking.id, gs[0].id)
        .pipe(map(res => res.body))
        .subscribe(ijobs => {
          if (q.booking && ijobs) q.booking.jobs = ijobs;
        });
    }
  }

  loadAll(): void {
    this.quoteService.query().subscribe((res: HttpResponse<IQuote[]>) => (this.quotes = res.body || []));
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInQuotes();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IQuote): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInQuotes(): void {
    this.eventSubscriber = this.eventManager.subscribe('quoteListModification', () => this.loadAllGarageQuotations());
  }

  delete(quote: IQuote): void {
    const modalRef = this.modalService.open(QuoteDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.quote = quote;
  }

  private handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      this.predicate = sort[0];
      this.ascending = sort[1] === 'asc';
      this.loadAllGarageQuotations();
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
