import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { Gms4UTestModule } from '../../../test.module';
import { CustomerComponent } from 'app/entities/customer/customer.component';
import { CustomerService } from 'app/entities/customer/customer.service';
import { Customer } from 'app/shared/model/customer.model';
import { MockAccountService } from '../../../helpers/mock-account.service';
import { GarageAdminService } from 'app/entities/garage-admin';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { Garage } from 'app/shared/model/garage.model';

describe('Component Tests', () => {
  describe('Customer Management Component', () => {
    let comp: CustomerComponent;
    let fixture: ComponentFixture<CustomerComponent>;
    let service: CustomerService;
    let accountService: MockAccountService;
    let garageAdminService: GarageAdminService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [CustomerComponent],
      })
        .overrideTemplate(CustomerComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CustomerComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(CustomerService);
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
      spyOn(service, 'queryGarageCustomers').and.returnValue(
        of(
          new HttpResponse({
            body: [new Customer(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.queryGarageCustomers).toHaveBeenCalled();
      expect(comp.customers && comp.customers[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
