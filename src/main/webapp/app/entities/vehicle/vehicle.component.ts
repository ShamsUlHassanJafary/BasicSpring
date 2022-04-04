import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { of, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IVehicle } from 'app/shared/model/vehicle.model';
import { VehicleService } from './vehicle.service';
import { VehicleDeleteDialogComponent } from './vehicle-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { map, flatMap, concatMap } from 'rxjs/operators';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { IGarage } from 'app/shared/model/garage.model';

@Component({
  selector: 'jhi-vehicle',
  templateUrl: './vehicle.component.html',
})
export class VehicleComponent implements OnInit, OnDestroy {
  vehicles?: IVehicle[];
  eventSubscriber?: Subscription;

  constructor(
    protected vehicleService: VehicleService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    // this.vehicleService.query().subscribe((res: HttpResponse<IVehicle[]>) => (this.vehicles = res.body || []));
    this.accountService
      .getAuthenticationState()
      .pipe(
        concatMap(account =>
          this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
        ),
        concatMap(g => g.pipe(map(garages => garages)))
      )
      .subscribe(gs => {
        this.vehicles = [];
        if (gs)
          this.vehicleService
            .queryGarageVehicles(gs[0].id)
            .pipe(flatMap((vehiclesResponse: HttpResponse<IVehicle[]>) => of(vehiclesResponse.body)))
            .subscribe(gj => {
              gj?.forEach(j => this.vehicles?.push(j));
              this.vehicles?.sort((a, b) => {
                const left = a?.id;
                const right = b.id;
                return left && right ? left - right : 0;
              });
            });
      });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInVehicles();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IVehicle): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInVehicles(): void {
    this.eventSubscriber = this.eventManager.subscribe('vehicleListModification', () => this.loadAll());
  }

  delete(vehicle: IVehicle): void {
    const modalRef = this.modalService.open(VehicleDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.vehicle = vehicle;
  }
}
