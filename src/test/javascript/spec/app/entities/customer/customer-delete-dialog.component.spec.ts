import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { Gms4UTestModule } from '../../../test.module';
import { MockEventManager } from '../../../helpers/mock-event-manager.service';
import { MockActiveModal } from '../../../helpers/mock-active-modal.service';
import { CustomerDeleteDialogComponent } from 'app/entities/customer/customer-delete-dialog.component';
import { CustomerService } from 'app/entities/customer/customer.service';
import { HttpResponse } from '@angular/common/http';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { GarageAdminService } from 'app/entities/garage-admin';
import { Garage } from 'app/shared/model/garage.model';
import { MockAccountService } from '../../../helpers/mock-account.service';

describe('Component Tests', () => {
  describe('Customer Management Delete Component', () => {
    let comp: CustomerDeleteDialogComponent;
    let fixture: ComponentFixture<CustomerDeleteDialogComponent>;
    let service: CustomerService;
    let accountService: MockAccountService;
    let garageAdminService: GarageAdminService;
    let mockEventManager: MockEventManager;
    let mockActiveModal: MockActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [CustomerDeleteDialogComponent],
      })
        .overrideTemplate(CustomerDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CustomerDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(CustomerService);
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
