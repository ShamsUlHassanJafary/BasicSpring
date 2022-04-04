import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IBooking } from 'app/shared/model/booking.model';
import { IJob } from 'app/shared/model/job.model';

type EntityResponseType = HttpResponse<IBooking>;
type EntityArrayResponseType = HttpResponse<IBooking[]>;

@Injectable({ providedIn: 'root' })
export class BookingService {
  public resourceUrl = SERVER_API_URL + 'api/bookings';

  public garageResourceUrl = SERVER_API_URL + 'api/garages';

  constructor(protected http: HttpClient) {}

  create(booking: IBooking): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(booking);
    return this.http
      .post<IBooking>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(booking: IBooking): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(booking);
    return this.http
      .put<IBooking>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBooking>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  generateAndDownloadInvoice(id: number): any {
    return this.http.get(`${this.resourceUrl}/${id}/invoices`, {
      headers: { 'Content-Type': 'application/pdf' },
      observe: 'response',
      responseType: 'blob',
    });
  }

  queryBookingJobs(bookingId?: number, garageId?: number, req?: any): Observable<HttpResponse<IJob[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<IJob[]>(`${this.garageResourceUrl}/${garageId}/bookings/${bookingId}/jobs`, { params: options, observe: 'response' })
      .pipe(map((res: HttpResponse<IJob[]>) => res));
  }

  generateAndDownloadServiceHistory(customerId: number, vehicleId: number, garageId?: number): any {
    const downloadServiceHistoryUrl =
      this.garageResourceUrl + `/${garageId}/customers/${customerId}/vehicles/${vehicleId}/service-history/download`;

    return this.http.get(downloadServiceHistoryUrl, {
      headers: { 'Content-Type': 'application/pdf' },
      observe: 'response',
      responseType: 'blob',
    });
  }

  sendInvoice(booking: IBooking): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(booking);
    return this.http
      .post<IBooking>(`${this.resourceUrl}/${booking?.id}/invoices`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  sendServiceHistoryReport(customerId: number, vehicleId: number, garageId?: number): Observable<HttpResponse<{}>> {
    const sendServiceHistoryMailUrl = this.garageResourceUrl + `/${garageId}/customers/${customerId}/vehicles/${vehicleId}/service-history`;
    return this.http.post(sendServiceHistoryMailUrl, null, { observe: 'response' });
  }

  queryGarageServiceHistory(garageId?: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBooking[]>(`${this.garageResourceUrl}/${garageId}/service-histories`, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  queryGarageBookings(garageId?: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBooking[]>(`${this.garageResourceUrl}/${garageId}/bookings`, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBooking[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(booking: IBooking): IBooking {
    const copy: IBooking = Object.assign({}, booking, {
      bookingDate: booking.bookingDate && booking.bookingDate.isValid() ? booking.bookingDate.utc(true).toJSON() : undefined,
      bookingTime: booking.bookingTime && booking.bookingTime.isValid() ? booking.bookingTime.utc(true).toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.bookingDate = res.body.bookingDate ? moment(res.body.bookingDate) : undefined;
      res.body.bookingTime = res.body.bookingTime ? moment(res.body.bookingTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((booking: IBooking) => {
        booking.bookingDate = booking.bookingDate ? moment(booking.bookingDate) : undefined;
        booking.bookingTime = booking.bookingTime ? moment(booking.bookingTime) : undefined;
      });
    }
    return res;
  }
}
