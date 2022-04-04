import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { Gms4UTestModule } from '../../../test.module';
import { InvoiceComponent } from 'app/entities/invoice/invoice.component';
import { InvoiceService } from 'app/entities/invoice/invoice.service';
import { Invoice } from 'app/shared/model/invoice.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { GarageAdminService } from 'app/entities/garage-admin';
import { Garage } from 'app/shared/model/garage.model';
import { MockAccountService } from '../../../helpers/mock-account.service';

describe('Component Tests', () => {
  describe('Invoice Management Component', () => {
    let comp: InvoiceComponent;
    let fixture: ComponentFixture<InvoiceComponent>;
    let service: InvoiceService;
    let accountService: MockAccountService;
    let garageAdminService: GarageAdminService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [InvoiceComponent],
      })
        .overrideTemplate(InvoiceComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InvoiceComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(InvoiceService);
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
      spyOn(service, 'queryGarageInvoices').and.returnValue(
        of(
          new HttpResponse({
            body: [new Invoice(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.queryGarageInvoices).toHaveBeenCalled();
      expect(comp.invoices && comp.invoices[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
