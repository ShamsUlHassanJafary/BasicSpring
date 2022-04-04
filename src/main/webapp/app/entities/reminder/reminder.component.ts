import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { of, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IReminder } from 'app/shared/model/reminder.model';
import { ReminderService } from './reminder.service';
import { ReminderDeleteDialogComponent } from './reminder-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { IGarage } from 'app/shared/model/garage.model';
import { concatMap, map, flatMap } from 'rxjs/operators';
import { GarageAdminService } from '../garage-admin/garage-admin.service';

@Component({
  selector: 'jhi-reminder',
  templateUrl: './reminder.component.html',
})
export class ReminderComponent implements OnInit, OnDestroy {
  reminders?: IReminder[];
  eventSubscriber?: Subscription;

  constructor(
    protected reminderService: ReminderService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
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
        if (gs)
          this.reminderService
            .queryGarageReminders(gs[0].id)
            .pipe(flatMap((remindersReponse: HttpResponse<IReminder[]>) => of(remindersReponse.body)))
            .subscribe(gj => {
              gj?.forEach(j => this.reminders?.push(j));
              this.reminders?.sort((a, b) => {
                const left = a?.id;
                const right = b.id;
                return left && right ? left - right : 0;
              });
            });
      });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInReminders();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IReminder): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInReminders(): void {
    this.eventSubscriber = this.eventManager.subscribe('reminderListModification', () => this.loadAll());
  }

  delete(reminder: IReminder): void {
    const modalRef = this.modalService.open(ReminderDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.reminder = reminder;
  }
}
