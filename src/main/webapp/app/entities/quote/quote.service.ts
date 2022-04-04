import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IQuote } from 'app/shared/model/quote.model';
import { IJob } from 'app/shared/model/job.model';

type EntityResponseType = HttpResponse<IQuote>;
type EntityArrayResponseType = HttpResponse<IQuote[]>;

@Injectable({ providedIn: 'root' })
export class QuoteService {
  public resourceUrl = SERVER_API_URL + 'api/quotes';

  public garageResourceUrl = SERVER_API_URL + 'api/garages';

  constructor(protected http: HttpClient) {}

  create(quote: IQuote): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(quote);
    return this.http
      .post<IQuote>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(quote: IQuote): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(quote);
    return this.http
      .put<IQuote>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IQuote>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IQuote[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  queryGarageQuotations(garageId?: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IQuote[]>(`${this.garageResourceUrl}/${garageId}/quotes`, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  queryQuoteJobs(bookingId?: number, garageId?: number, req?: any): Observable<HttpResponse<IJob[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<IJob[]>(`${this.garageResourceUrl}/${garageId}/bookings/${bookingId}/jobs`, { params: options, observe: 'response' })
      .pipe(map((res: HttpResponse<IJob[]>) => res));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(quote: IQuote): IQuote {
    const copy: IQuote = Object.assign({}, quote, {
      quoteDate: quote.quoteDate && quote.quoteDate.isValid() ? quote.quoteDate.utc(true).toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.quoteDate = res.body.quoteDate ? moment(res.body.quoteDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((quote: IQuote) => {
        quote.quoteDate = quote.quoteDate ? moment(quote.quoteDate) : undefined;
      });
    }
    return res;
  }
}
