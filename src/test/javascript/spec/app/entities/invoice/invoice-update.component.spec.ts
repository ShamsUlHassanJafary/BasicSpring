import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { Gms4UTestModule } from '../../../test.module';
import { InvoiceUpdateComponent } from 'app/entities/invoice/invoice-update.component';
import { InvoiceService } from 'app/entities/invoice/invoice.service';
import { Invoice } from 'app/shared/model/invoice.model';
import { Booking } from 'app/shared/model/booking.model';
import { BookingService } from 'app/entities/booking/booking.service';
import { GarageAdminService } from 'app/entities/garage-admin';
import { MockAccountService } from '../../../helpers/mock-account.service';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { Garage } from 'app/shared/model/garage.model';

describe('Component Tests', () => {
  describe('Invoice Management Update Component', () => {
    let comp: InvoiceUpdateComponent;
    let fixture: ComponentFixture<InvoiceUpdateComponent>;
    let service: InvoiceService;
    let accountService: MockAccountService;
    let garageAdminService: GarageAdminService;
    let bookingService: BookingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [InvoiceUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(InvoiceUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InvoiceUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(InvoiceService);
      accountService = TestBed.get(AccountService);
      const login = 'test.user';
      accountService.setIdentityResponse(new Account(false, [], 'test.email@email.com', '', '', '', login, ''));
      garageAdminService = fixture.debugElement.injector.get(GarageAdminService);
      bookingService = fixture.debugElement.injector.get(BookingService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        spyOn(bookingService, 'find').and.returnValue(
          of(
            new HttpResponse({
              body: new Booking(),
            })
          )
        );

        spyOn(garageAdminService, 'findGaragesByAccount').and.returnValues(
          of(
            new HttpResponse({
              body: [new Garage(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA')],
            })
          )
        );
        const entity = new Invoice(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalled();
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        spyOn(garageAdminService, 'findGaragesByAccount').and.returnValues(
          of(
            new HttpResponse({
              body: [new Garage(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA')],
            })
          )
        );

        const entity = new Invoice();

        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalled();
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
