import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { Gms4UTestModule } from '../../../test.module';
import { QuoteComponent } from 'app/entities/quote/quote.component';
import { QuoteService } from 'app/entities/quote/quote.service';
import { Quote } from 'app/shared/model/quote.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { GarageAdminService } from 'app/entities/garage-admin';
import { Garage } from 'app/shared/model/garage.model';
import { MockAccountService } from '../../../helpers/mock-account.service';

describe('Component Tests', () => {
  describe('Quote Management Component', () => {
    let comp: QuoteComponent;
    let fixture: ComponentFixture<QuoteComponent>;
    let service: QuoteService;
    let accountService: MockAccountService;
    let garageAdminService: GarageAdminService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [QuoteComponent],
      })
        .overrideTemplate(QuoteComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(QuoteComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(QuoteService);

      accountService = TestBed.get(AccountService);
      const login = 'test.user';
      accountService.setIdentityResponse(new Account(false, [], 'test.email@email.com', '', '', '', login, ''));
      garageAdminService = fixture.debugElement.injector.get(GarageAdminService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      spyOn(garageAdminService, 'findGaragesByAccount').and.returnValues(
        of(
          new HttpResponse({
            body: [new Garage(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA')],
          })
        )
      );

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'queryGarageQuotations').and.returnValue(
        of(
          new HttpResponse({
            body: [new Quote(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.queryGarageQuotations).toHaveBeenCalled();
      expect(comp.quotes && comp.quotes[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
