import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { GarageTypeService } from 'app/entities/garage-type/garage-type.service';
import { IGarageType, GarageType } from 'app/shared/model/garage-type.model';

describe('Service Tests', () => {
  describe('GarageType Service', () => {
    let injector: TestBed;
    let service: GarageTypeService;
    let httpMock: HttpTestingController;
    let elemDefault: IGarageType;
    let expectedResult: IGarageType | IGarageType[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(GarageTypeService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new GarageType(0, 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should return a list of GarageType', () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
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
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
