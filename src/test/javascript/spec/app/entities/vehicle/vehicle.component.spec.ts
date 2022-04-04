import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { Gms4UTestModule } from '../../../test.module';
import { VehicleComponent } from 'app/entities/vehicle/vehicle.component';
import { VehicleService } from 'app/entities/vehicle/vehicle.service';
import { Vehicle } from 'app/shared/model/vehicle.model';
import { GarageAdminService } from 'app/entities/garage-admin';
import { MockAccountService } from '../../../helpers/mock-account.service';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { Garage } from 'app/shared/model/garage.model';

describe('Component Tests', () => {
  describe('Vehicle Management Component', () => {
    let comp: VehicleComponent;
    let fixture: ComponentFixture<VehicleComponent>;
    let service: VehicleService;
    let accountService: MockAccountService;
    let garageAdminService: GarageAdminService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [VehicleComponent],
      })
        .overrideTemplate(VehicleComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(VehicleComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(VehicleService);
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
      spyOn(service, 'queryGarageVehicles').and.returnValue(
        of(
          new HttpResponse({
            body: [new Vehicle(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.queryGarageVehicles).toHaveBeenCalled();
      expect(comp.vehicles && comp.vehicles[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
