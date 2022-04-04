import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { GarageService } from 'app/entities/garage/garage.service';
import { IGarage, Garage } from 'app/shared/model/garage.model';
import { of } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

describe('Service Tests', () => {
  describe('Garage Service', () => {
    let injector: TestBed;
    let service: GarageService;
    let httpMock: HttpTestingController;
    let elemDefault: IGarage;
    let expectedResult: IGarage | IGarage[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(GarageService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new Garage(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Garage', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        spyOn(service, 'uploadLogo').and.returnValue(
          of(
            new HttpResponse({
              body: elemDefault,
            })
          )
        );
        service.create(new Garage()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Garage', () => {
        const returnedFromService = Object.assign(
          {
            businessName: 'BBBBBB',
            lineAddress1: 'BBBBBB',
            lineAddress2: 'BBBBBB',
            city: 'BBBBBB',
            county: 'BBBBBB',
            postcode: 'BBBBBB',
            country: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);
        spyOn(service, 'uploadLogo').and.returnValue(
          of(
            new HttpResponse({
              body: elemDefault,
            })
          )
        );
        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Garage', () => {
        const returnedFromService = Object.assign(
          {
            businessName: 'BBBBBB',
            lineAddress1: 'BBBBBB',
            lineAddress2: 'BBBBBB',
            city: 'BBBBBB',
            county: 'BBBBBB',
            postcode: 'BBBBBB',
            country: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Garage', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
