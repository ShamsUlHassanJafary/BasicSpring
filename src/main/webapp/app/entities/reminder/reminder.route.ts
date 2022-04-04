import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IReminder, Reminder } from 'app/shared/model/reminder.model';
import { ReminderService } from './reminder.service';
import { ReminderComponent } from './reminder.component';
import { ReminderDetailComponent } from './reminder-detail.component';
import { ReminderUpdateComponent } from './reminder-update.component';

@Injectable({ providedIn: 'root' })
export class ReminderResolve implements Resolve<IReminder> {
  constructor(private service: ReminderService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IReminder> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((reminder: HttpResponse<Reminder>) => {
          if (reminder.body) {
            return of(reminder.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Reminder());
  }
}

export const reminderRoute: Routes = [
  {
    path: '',
    component: ReminderComponent,
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Reminders',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReminderDetailComponent,
    resolve: {
      reminder: ReminderResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Reminders',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReminderUpdateComponent,
    resolve: {
      reminder: ReminderResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Reminders',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReminderUpdateComponent,
    resolve: {
      reminder: ReminderResolve,
    },
    data: {
      authorities: [Authority.GARAGE_OWNER, Authority.GARAGE_ADMIN],
      pageTitle: 'Reminders',
    },
    canActivate: [UserRouteAccessService],
  },
];
