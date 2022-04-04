import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { ReminderService } from 'app/entities/reminder/reminder.service';
import { IReminder, Reminder } from 'app/shared/model/reminder.model';

describe('Service Tests', () => {
  describe('Reminder Service', () => {
    let injector: TestBed;
    let service: ReminderService;
    let httpMock: HttpTestingController;
    let elemDefault: IReminder;
    let expectedResult: IReminder | IReminder[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(ReminderService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Reminder(
        0,
        'AAAAAAA',
        currentDate,
        false,
        currentDate,
        currentDate,
        'AAAAAAA',
        false,
        false,
        currentDate,
        currentDate
      );
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            eventDate: currentDate,
            alertDate: currentDate,
            secondAlertDate: currentDate,
            alertTime: currentDate.format(DATE_TIME_FORMAT),
            secondAlertTime: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Reminder', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            eventDate: currentDate,
            alertDate: currentDate,
            secondAlertDate: currentDate,
            alertTime: currentDate.format(DATE_TIME_FORMAT),
            secondAlertTime: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            eventDate: currentDate,
            alertDate: currentDate,
            secondAlertDate: currentDate,
            alertTime: currentDate,
            secondAlertTime: currentDate,
          },
          returnedFromService
        );

        service.create(new Reminder()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Reminder', () => {
        const returnedFromService = Object.assign(
          {
            description: 'BBBBBB',
            eventDate: currentDate,
            enabled: true,
            alertDate: currentDate,
            secondAlertDate: currentDate,
            comment: 'BBBBBB',
            emailEnabled: true,
            smsEnabled: true,
            alertTime: currentDate.format(DATE_TIME_FORMAT),
            secondAlertTime: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            eventDate: currentDate,
            alertDate: currentDate,
            secondAlertDate: currentDate,
            alertTime: currentDate,
            secondAlertTime: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Reminder', () => {
        const returnedFromService = Object.assign(
          {
            description: 'BBBBBB',
            eventDate: currentDate,
            enabled: true,
            alertDate: currentDate,
            secondAlertDate: currentDate,
            comment: 'BBBBBB',
            emailEnabled: true,
            smsEnabled: true,
            alertTime: currentDate.format(DATE_TIME_FORMAT),
            secondAlertTime: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            eventDate: currentDate,
            alertDate: currentDate,
            secondAlertDate: currentDate,
            alertTime: currentDate,
            secondAlertTime: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Reminder', () => {
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
