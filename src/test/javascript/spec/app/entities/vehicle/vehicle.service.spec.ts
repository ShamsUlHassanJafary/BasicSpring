import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { VehicleService } from 'app/entities/vehicle/vehicle.service';
import { IVehicle, Vehicle } from 'app/shared/model/vehicle.model';

describe('Service Tests', () => {
  describe('Vehicle Service', () => {
    let injector: TestBed;
    let service: VehicleService;
    let httpMock: HttpTestingController;
    let elemDefault: IVehicle;
    let expectedResult: IVehicle | IVehicle[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(VehicleService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Vehicle(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            motDue: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Vehicle', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            motDue: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            motDue: currentDate,
          },
          returnedFromService
        );

        service.create(new Vehicle()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Vehicle', () => {
        const returnedFromService = Object.assign(
          {
            registration: 'BBBBBB',
            make: 'BBBBBB',
            model: 'BBBBBB',
            colour: 'BBBBBB',
            motDue: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            motDue: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Vehicle', () => {
        const returnedFromService = Object.assign(
          {
            registration: 'BBBBBB',
            make: 'BBBBBB',
            model: 'BBBBBB',
            colour: 'BBBBBB',
            motDue: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            motDue: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Vehicle', () => {
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
