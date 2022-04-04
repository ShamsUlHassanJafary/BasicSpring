import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { Gms4UTestModule } from '../../../test.module';
import { ReminderComponent } from 'app/entities/reminder/reminder.component';
import { ReminderService } from 'app/entities/reminder/reminder.service';
import { Reminder } from 'app/shared/model/reminder.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { GarageAdminService } from 'app/entities/garage-admin';
import { Garage } from 'app/shared/model/garage.model';
import { MockAccountService } from '../../../helpers/mock-account.service';

describe('Component Tests', () => {
  describe('Reminder Management Component', () => {
    let comp: ReminderComponent;
    let fixture: ComponentFixture<ReminderComponent>;
    let service: ReminderService;
    let accountService: MockAccountService;
    let garageAdminService: GarageAdminService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [ReminderComponent],
      })
        .overrideTemplate(ReminderComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ReminderComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ReminderService);
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
      spyOn(service, 'queryGarageReminders').and.returnValue(
        of(
          new HttpResponse({
            body: [new Reminder(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.queryGarageReminders).toHaveBeenCalled();
      expect(comp.reminders && comp.reminders[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
