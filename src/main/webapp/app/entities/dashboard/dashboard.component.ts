import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { AccountService } from 'app/core/auth/account.service';
import { IBooking } from 'app/shared/model/booking.model';
import { BookingStatus } from 'app/shared/model/enumerations/booking-status.model';
import { IGarage } from 'app/shared/model/garage.model';
import { IReminder } from 'app/shared/model/reminder.model';
import { JhiEventManager } from 'ng-jhipster';
import { of, Subscription } from 'rxjs';
import { concatMap, map, flatMap } from 'rxjs/operators';
import { BookingService } from '../booking/booking.service';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { ReminderService } from '../reminder/reminder.service';
import * as moment from 'moment';

@Component({
  selector: 'jhi-dashboard',
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  bookings?: IBooking[];
  reminders?: IReminder[];
  eventSubscriber?: Subscription;
  predicate!: string;
  ascending!: boolean;

  constructor(
    protected reminderService: ReminderService,
    protected bookingService: BookingService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected eventManager: JhiEventManager
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
        this.reminders = [];
        if (gs) {
          this.reminderService
            .queryGarageReminders(gs[0].id, { date: moment(new Date()).format('DD/MM/YYYY') })
            .pipe(flatMap((remindersReponse: HttpResponse<IReminder[]>) => of(remindersReponse.body)))
            .subscribe(gj => {
              gj?.forEach(j => this.reminders?.push(j));
              this.reminders?.sort((a, b) => {
                const left = a?.id;
                const right = b.id;
                return left && right ? left - right : 0;
              });
            });
          this.loadAllGarageBookings(gs);
        }
      });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInReminders();
  }

  private loadAllGarageBookings(gs: IGarage[]): void {
    this.bookings = [];
    this.bookingService
      .queryGarageBookings(gs[0].id, {
        statuses: [BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS, BookingStatus.COMPLETED],
        date: moment(new Date()).format('DD/MM/YYYY'),
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
          if (ijobs) booking.jobs = ijobs;
        });
    }
  }

  registerChangeInReminders(): void {
    this.eventSubscriber = this.eventManager.subscribe('reminderListModification', () => this.loadAll());
  }

  registerChangeInBookings(): void {
    this.eventSubscriber = this.eventManager.subscribe('bookingListModification', () => this.loadAll());
  }

  private sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }
}
