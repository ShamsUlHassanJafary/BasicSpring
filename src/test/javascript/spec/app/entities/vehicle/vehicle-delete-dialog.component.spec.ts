import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { Gms4UTestModule } from '../../../test.module';
import { MockEventManager } from '../../../helpers/mock-event-manager.service';
import { MockActiveModal } from '../../../helpers/mock-active-modal.service';
import { VehicleDeleteDialogComponent } from 'app/entities/vehicle/vehicle-delete-dialog.component';
import { VehicleService } from 'app/entities/vehicle/vehicle.service';
import { HttpResponse } from '@angular/common/http';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { GarageAdminService } from 'app/entities/garage-admin';
import { Garage } from 'app/shared/model/garage.model';
import { MockAccountService } from '../../../helpers/mock-account.service';

describe('Component Tests', () => {
  describe('Vehicle Management Delete Component', () => {
    let comp: VehicleDeleteDialogComponent;
    let fixture: ComponentFixture<VehicleDeleteDialogComponent>;
    let service: VehicleService;
    let accountService: MockAccountService;
    let garageAdminService: GarageAdminService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [VehicleDeleteDialogComponent],
      })
        .overrideTemplate(VehicleDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(VehicleDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(VehicleService);
      mockEventManager = TestBed.get(JhiEventManager);
      mockActiveModal = TestBed.get(NgbActiveModal);
      accountService = TestBed.get(AccountService);
      const login = 'test.user';
      accountService.setIdentityResponse(new Account(false, [], 'test.email@email.com', '', '', '', login, ''));
      garageAdminService = fixture.debugElement.injector.get(GarageAdminService);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          const garage = new Garage(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA');
          // GIVEN
          spyOn(garageAdminService, 'findGaragesByAccount').and.returnValues(
            of(
              new HttpResponse({
                body: [garage],
              })
            )
          );

          spyOn(service, 'deleteFromGarage').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.deleteFromGarage).toHaveBeenCalledWith(123, garage.id);
          expect(mockActiveModal.closeSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));

      it('Should not call delete service on clear', () => {
        // GIVEN
        spyOn(service, 'delete');

        // WHEN
        comp.cancel();

        // THEN
        expect(service.delete).not.toHaveBeenCalled();
        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
      });
    });
  });
});
