import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ICustomer } from 'app/shared/model/customer.model';
import { IVehicle } from 'app/shared/model/vehicle.model';
import { IBooking } from 'app/shared/model/booking.model';

type EntityResponseType = HttpResponse<ICustomer>;
type EntityArrayResponseType = HttpResponse<ICustomer[] | IBooking[] | IVehicle[]>;

@Injectable({ providedIn: 'root' })
export class CustomerService {
  public resourceUrl = SERVER_API_URL + 'api/customers';

  public garageResourceUrl = SERVER_API_URL + 'api/garages';

  constructor(protected http: HttpClient) {}

  create(customer: ICustomer): Observable<EntityResponseType> {
    return this.http.post<ICustomer>(this.resourceUrl, customer, { observe: 'response' });
  }

  update(customer: ICustomer): Observable<EntityResponseType> {
    return this.http.put<ICustomer>(this.resourceUrl, customer, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICustomer>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICustomer[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  queryGarageCustomers(garageId?: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICustomer[]>(`${this.garageResourceUrl}/${garageId}/customers`, { params: options, observe: 'response' });
  }

  queryCustomerVehicles(id: number, garageId: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVehicle[]>(`${this.garageResourceUrl}/${garageId}/customers/${id}/vehicles`, {
      params: options,
      observe: 'response',
    });
  }

  queryServiceHistory(garageId: number, customerId: number, vehicleId: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBooking[]>(`${this.garageResourceUrl}/${garageId}/customers/${customerId}/vehicles/${vehicleId}/history`, {
      params: options,
      observe: 'response',
    });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  deleteFromGarage(id: number, garageId?: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.garageResourceUrl}/${garageId}/customers/${id}`, { observe: 'response' });
  }
}
