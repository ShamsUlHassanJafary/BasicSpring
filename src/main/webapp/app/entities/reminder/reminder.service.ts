import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment-timezone';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IReminder } from 'app/shared/model/reminder.model';

type EntityResponseType = HttpResponse<IReminder>;
type EntityArrayResponseType = HttpResponse<IReminder[]>;

@Injectable({ providedIn: 'root' })
export class ReminderService {
  public resourceUrl = SERVER_API_URL + 'api/reminders';

  public garageResourceUrl = SERVER_API_URL + 'api/garages';

  constructor(protected http: HttpClient) {}

  create(reminder: IReminder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reminder);
    return this.http
      .post<IReminder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(reminder: IReminder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reminder);
    return this.http
      .put<IReminder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IReminder>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IReminder[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  queryGarageReminders(garageId?: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IReminder[]>(`${this.garageResourceUrl}/${garageId}/reminders`, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(reminder: IReminder): IReminder {
    const copy: IReminder = Object.assign({}, reminder, {
      eventDate: reminder.eventDate && reminder.eventDate.isValid() ? reminder.eventDate.utc(true).toJSON() : undefined,
      alertDate: reminder.alertDate && reminder.alertDate.isValid() ? reminder.alertDate.utc(true).toJSON() : undefined,
      secondAlertDate:
        reminder.secondAlertDate && reminder.secondAlertDate.isValid() ? reminder.secondAlertDate.utc(true).toJSON() : undefined,
      alertTime: reminder.alertTime && reminder.alertTime.isValid() ? reminder.alertTime.toJSON() : undefined,
      secondAlertTime: reminder.secondAlertTime && reminder.secondAlertTime.isValid() ? reminder.secondAlertTime.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.eventDate = res.body.eventDate ? moment(res.body.eventDate) : undefined;
      res.body.alertDate = res.body.alertDate ? moment(res.body.alertDate) : undefined;
      res.body.secondAlertDate = res.body.secondAlertDate ? moment(res.body.secondAlertDate) : undefined;
      res.body.alertTime = res.body.alertTime ? moment(res.body.alertTime) : undefined;
      res.body.secondAlertTime = res.body.secondAlertTime ? moment(res.body.secondAlertTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((reminder: IReminder) => {
        reminder.eventDate = reminder.eventDate ? moment(reminder.eventDate) : undefined;
        reminder.alertDate = reminder.alertDate ? moment(reminder.alertDate) : undefined;
        reminder.secondAlertDate = reminder.secondAlertDate ? moment(reminder.secondAlertDate) : undefined;
        reminder.alertTime = reminder.alertTime ? moment(reminder.alertTime) : undefined;
        reminder.secondAlertTime = reminder.secondAlertTime ? moment(reminder.secondAlertTime) : undefined;
      });
    }
    return res;
  }
}
