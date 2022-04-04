import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IVehicle } from 'app/shared/model/vehicle.model';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

type EntityResponseType = HttpResponse<IVehicle>;
type EntityArrayResponseType = HttpResponse<IVehicle[]>;

@Injectable({ providedIn: 'root' })
export class VehicleService {
  public resourceUrl = SERVER_API_URL + 'api/vehicles';

  public customerResourceUrl = SERVER_API_URL + 'api/customers';

  public garageResourceUrl = SERVER_API_URL + 'api/garages';

  constructor(protected http: HttpClient) {}

  create(vehicle: IVehicle): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vehicle);
    return this.http
      .post<IVehicle>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(vehicle: IVehicle): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(vehicle);
    return this.http
      .put<IVehicle>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IVehicle>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  verifyVehicle(registrationNumber: string): Observable<EntityResponseType> {
    return this.http
      .get<IVehicle>(`${this.resourceUrl}/dvla/${registrationNumber}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVehicle[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  queryGarageVehicles(garageId?: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVehicle[]>(`${this.garageResourceUrl}/${garageId}/vehicles`, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  queryCustomerVehicles(id: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVehicle[]>(`${this.customerResourceUrl}/${id}/vehicles`, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  deleteFromGarage(id: number, garageId?: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.garageResourceUrl}/${garageId}/vehicles/${id}`, { observe: 'response' });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.motExpiryDate = res.body.motExpiryDate ? moment(res.body.motExpiryDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((vehicle: IVehicle) => {
        vehicle.motExpiryDate = vehicle.motExpiryDate ? moment(vehicle.motExpiryDate) : undefined;
      });
    }
    return res;
  }

  protected convertDateFromClient(vehicle: IVehicle): IVehicle {
    const copy: IVehicle = Object.assign({}, vehicle, {
      motExpiryDate: vehicle.motExpiryDate && vehicle.motExpiryDate.isValid() ? vehicle.motExpiryDate.utc(true).toJSON() : undefined,
    });
    return copy;
  }
}
