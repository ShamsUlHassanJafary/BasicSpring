import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { Gms4UTestModule } from '../../../test.module';
import { BookingComponent } from 'app/entities/booking/booking.component';
import { BookingService } from 'app/entities/booking/booking.service';
import { Booking } from 'app/shared/model/booking.model';
import { GarageAdminService } from 'app/entities/garage-admin';
import { MockAccountService } from '../../../helpers/mock-account.service';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { Garage } from 'app/shared/model/garage.model';

describe('Component Tests', () => {
  describe('Booking Management Component', () => {
    let comp: BookingComponent;
    let fixture: ComponentFixture<BookingComponent>;
    let service: BookingService;
    let accountService: MockAccountService;
    let garageAdminService: GarageAdminService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [BookingComponent],
      })
        .overrideTemplate(BookingComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BookingComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BookingService);
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
      spyOn(service, 'queryGarageBookings').and.returnValue(
        of(
          new HttpResponse({
            body: [new Booking(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.queryGarageBookings).toHaveBeenCalled();
      expect(comp.bookings && comp.bookings[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
