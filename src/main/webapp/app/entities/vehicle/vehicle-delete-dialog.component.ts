import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IVehicle } from 'app/shared/model/vehicle.model';
import { VehicleService } from './vehicle.service';
import { AccountService } from 'app/core/auth/account.service';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { HttpResponse } from '@angular/common/http';
import { IGarage } from 'app/shared/model/garage.model';
import { of } from 'rxjs';
import { concatMap, map } from 'rxjs/operators';

@Component({
  templateUrl: './vehicle-delete-dialog.component.html',
})
export class VehicleDeleteDialogComponent {
  vehicle?: IVehicle;

  constructor(
    protected vehicleService: VehicleService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.accountService
      .getAuthenticationState()
      .pipe(
        concatMap(account =>
          this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
        ),
        concatMap(g => g.pipe(map(garages => garages)))
      )
      .subscribe(gs => {
        this.vehicleService.deleteFromGarage(id, gs![0].id).subscribe(() => {
          this.eventManager.broadcast('vehicleListModification');
          this.activeModal.close();
        });
      });
  }
}
