import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { QuoteService } from 'app/entities/quote/quote.service';
import { IQuote, Quote } from 'app/shared/model/quote.model';
import { MockStateStorageService } from '../../../helpers/mock-state-storage.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';
import { Router } from '@angular/router';
import { MockRouter } from '../../../helpers/mock-route.service';

fdescribe('Service Tests', () => {
  describe('Quote Service', () => {
    let injector: TestBed;
    let service: QuoteService;
    let httpMock: HttpTestingController;
    let elemDefault: IQuote;
    let expectedResult: IQuote | IQuote[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [
          {
            provide: StateStorageService,
            useClass: MockStateStorageService,
          },
          {
            provide: Router,
            useClass: MockRouter,
          },
        ],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(QuoteService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();
      elemDefault = new Quote(0, currentDate, 0);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            quoteDate: currentDate,
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Quote', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            quoteDate: currentDate,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            quoteDate: currentDate,
          },
          returnedFromService
        );

        service.create(new Quote()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Quote', () => {
        const returnedFromService = Object.assign(
          {
            quoteDate: currentDate,
            quoteTotal: 1,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            quoteDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Quote', () => {
        const returnedFromService = Object.assign(
          {
            quoteDate: currentDate,
            quoteTotal: 1,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            quoteDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Quote', () => {
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
