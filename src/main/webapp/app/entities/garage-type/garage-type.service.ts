import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IGarageType } from 'app/shared/model/garage-type.model';

type EntityResponseType = HttpResponse<IGarageType>;
type EntityArrayResponseType = HttpResponse<IGarageType[]>;

@Injectable({ providedIn: 'root' })
export class GarageTypeService {
  public resourceUrl = SERVER_API_URL + 'api/garage-types';

  constructor(protected http: HttpClient) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGarageType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGarageType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }
}
