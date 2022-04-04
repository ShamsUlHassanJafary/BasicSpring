import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IGarageAdmin } from 'app/shared/model/garage-admin.model';
import { IGarage } from 'app/shared/model/garage.model';

type EntityResponseType = HttpResponse<IGarageAdmin>;
type EntityArrayResponseType = HttpResponse<IGarageAdmin[] | IGarage[]>;

@Injectable({ providedIn: 'root' })
export class GarageAdminService {
  public resourceUrl = SERVER_API_URL + 'api/garage-admins';

  constructor(protected http: HttpClient) {}

  create(garageAdmin: IGarageAdmin): Observable<EntityResponseType> {
    return this.http.post<IGarageAdmin>(this.resourceUrl, garageAdmin, { observe: 'response' });
  }

  update(garageAdmin: IGarageAdmin): Observable<EntityResponseType> {
    return this.http.put<IGarageAdmin>(this.resourceUrl, garageAdmin, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGarageAdmin>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  findGaragesByAccount(userLogin: string): Observable<EntityArrayResponseType> {
    return this.http.get<IGarage[]>(`${this.resourceUrl}/user/${userLogin}/garages`, { observe: 'response' });
  }

  findByAccount(userLogin: string): Observable<EntityResponseType> {
    return this.http.get<IGarageAdmin>(`${this.resourceUrl}/user/${userLogin}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGarageAdmin[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
