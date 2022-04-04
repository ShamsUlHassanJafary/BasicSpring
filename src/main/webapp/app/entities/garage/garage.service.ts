import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IGarage } from 'app/shared/model/garage.model';
import { switchMap } from 'rxjs/operators';

type EntityResponseType = HttpResponse<IGarage>;
type EntityArrayResponseType = HttpResponse<IGarage[]>;

@Injectable({ providedIn: 'root' })
export class GarageService {
  public resourceUrl = SERVER_API_URL + 'api/garages';

  constructor(protected http: HttpClient) {}

  create(garage: IGarage): Observable<EntityResponseType> {
    return this.http
      .post<IGarage>(this.resourceUrl, garage, { observe: 'response' })
      .pipe(
        switchMap((g: EntityResponseType) => {
          return this.uploadLogo(g.body?.id!, garage.file!);
        })
      );
  }

  update(garage: IGarage): Observable<EntityResponseType> {
    return this.http
      .put<IGarage>(this.resourceUrl, garage, { observe: 'response' })
      .pipe(
        switchMap((g: EntityResponseType) => {
          return this.uploadLogo(g.body?.id!, garage.file!);
        })
      );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGarage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGarage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  uploadLogo(garageId: number, logo: File): Observable<EntityResponseType> {
    const formData: FormData = new FormData();
    formData.append('file', logo);

    return this.http.post<IGarage>(`${this.resourceUrl}/${garageId}/upload-logo`, formData, { observe: 'response', reportProgress: true });
  }

  retrieveLogo(garageId: number): Observable<HttpResponse<any>> {
    return this.http.get<any>(`${this.resourceUrl}/${garageId}/retrieve-logo`, { observe: 'response' });
  }

  dataURItoBlob(dataURI: string, type: string): Blob {
    const byteString = window.atob(dataURI);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const int8Array = new Uint8Array(arrayBuffer);
    for (let i = 0; i < byteString.length; i++) {
      int8Array[i] = byteString.charCodeAt(i);
    }
    const blob = new Blob([int8Array], { type: 'image/' + type });
    return blob;
  }
}
