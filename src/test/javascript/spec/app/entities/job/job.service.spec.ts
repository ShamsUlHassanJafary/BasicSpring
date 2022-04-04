import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { JobService } from 'app/entities/job/job.service';
import { IJob, Job } from 'app/shared/model/job.model';

describe('Service Tests', () => {
  describe('Job Service', () => {
    let injector: TestBed;
    let service: JobService;
    let httpMock: HttpTestingController;
    let elemDefault: IJob;
    let expectedResult: IJob | IJob[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(JobService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Job(0, 'AAAAAAA', currentDate, 0);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            dateCreated: currentDate,
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Job', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            dateCreated: currentDate,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            dateCreated: currentDate,
          },
          returnedFromService
        );

        service.create(new Job()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Job', () => {
        const returnedFromService = Object.assign(
          {
            description: 'BBBBBB',
            dateCreated: currentDate,
            price: 1,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            dateCreated: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Job', () => {
        const returnedFromService = Object.assign(
          {
            description: 'BBBBBB',
            dateCreated: currentDate,
            price: 1,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            dateCreated: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Job', () => {
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
