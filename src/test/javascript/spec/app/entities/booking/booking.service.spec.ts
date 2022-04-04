import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { BookingService } from 'app/entities/booking/booking.service';
import { IBooking, Booking } from 'app/shared/model/booking.model';
import { BookingStatus } from 'app/shared/model/enumerations/booking-status.model';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

describe('Service Tests', () => {
  describe('Booking Service', () => {
    let injector: TestBed;
    let service: BookingService;
    let httpMock: HttpTestingController;
    let elemDefault: IBooking;
    let expectedResult: IBooking | IBooking[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(BookingService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Booking(0, currentDate, currentDate, 'AAAAAAA', BookingStatus.IN_PROGRESS);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            bookingDate: currentDate,
            bookingTime: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Booking', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            bookingDate: currentDate,
            bookingTime: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            bookingDate: currentDate,
            bookingTime: currentDate,
          },
          returnedFromService
        );

        service.create(new Booking()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Booking', () => {
        const returnedFromService = Object.assign(
          {
            bookingDate: currentDate,
            bookingTime: currentDate.format(DATE_TIME_FORMAT),
            furtherInstruction: 'BBBBBB',
            status: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            bookingDate: currentDate,
            bookingTime: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Booking', () => {
        const returnedFromService = Object.assign(
          {
            bookingDate: currentDate,
            bookingTime: currentDate.format(DATE_TIME_FORMAT),
            furtherInstruction: 'BBBBBB',
            status: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            bookingDate: currentDate,
            bookingTime: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Booking', () => {
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
