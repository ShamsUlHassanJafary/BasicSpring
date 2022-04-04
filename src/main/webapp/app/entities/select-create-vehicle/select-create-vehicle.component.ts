import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { AccountService } from 'app/core/auth/account.service';
import { GarageAdminService } from 'app/entities/garage-admin/garage-admin.service';
import { VehicleService } from 'app/entities/vehicle/vehicle.service';
import { IVehicle } from 'app/shared/model/vehicle.model';
import { of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

@Component({
  selector: 'jhi-select-create-vehicle',
  templateUrl: './select-create-vehicle.component.html',
})
export class SelectCreateVehicleComponent implements OnInit {
  @Input()
  editBookingForm!: FormGroup;
  vehicles: IVehicle[] = [];

  newVehicle = false;

  selectMode = true;

  tempVehicle!: IVehicle;

  constructor(
    protected vehicleService: VehicleService,
    protected garageAdminService: GarageAdminService,
    protected accountService: AccountService
  ) {}

  ngOnInit(): void {
    this.editBookingForm?.get('customer')?.valueChanges.subscribe(val => {
      if (val.id) {
        this.getCustomerVehicles(val.id);
      }
    });
  }

  private getCustomerVehicles(customerId: number): void {
    const garage = this.editBookingForm?.get('garage')!.value;

    this.vehicleService
      .queryCustomerVehicles(customerId, garage?.id)
      .pipe(flatMap((vehiclesResponse: HttpResponse<IVehicle[]>) => of(vehiclesResponse.body)))
      .subscribe(veh => {
        this.vehicles = [];
        this.vehicles = veh!;
        this.vehicles?.forEach(v => {
          v.vehicleToString = this.vehicleDetailsToString(v);
        });
        this.vehicles?.sort((a, b) => {
          const left = a?.id;
          const right = b?.id;
          return left && right ? left - right : 0;
        });
      });
  }

  vehicleDetailsToString(vehicle: IVehicle): string {
    if (vehicle.registration)
      return vehicle?.registration?.toUpperCase() + ' (' + vehicle?.make + ' ' + vehicle?.model + ' ' + vehicle?.colour + ')';
    else return '';
  }
}
