import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';

import { IVehicle, Vehicle } from 'app/shared/model/vehicle.model';
import { VehicleService } from './vehicle.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer/customer.service';
import { AccountService } from 'app/core/auth/account.service';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { IGarage } from 'app/shared/model/garage.model';
import { concatMap, flatMap, map } from 'rxjs/operators';

@Component({
  selector: 'jhi-vehicle-update',
  templateUrl: './vehicle-update.component.html',
})
export class VehicleUpdateComponent implements OnInit {
  isSaving = false;
  customers: ICustomer[] = [];
  garage!: IGarage;

  editForm = this.fb.group({
    id: [],
    registration: [null, [Validators.required]],
    // registration: [
    //   null,
    //   [
    //     Validators.pattern(
    //       '(?<Current>^[a-zA-Z]{2}[0-9]{2}[a-zA-Z]{3}$)|(?<Prefix>^[a-zA-Z][0-9]{1,3}[A-Z]{3}$)|(?<Suffix>^[a-zA-Z]{3}[0-9]{1,3}[a-zA-Z]$)|(?<DatelessLongNumberPrefix>^[0-9]{1,4}[a-zA-Z]{1,2}$)|(?<DatelessShortNumberPrefix>^[0-9]{1,3}[a-zA-Z]{1,3}$)|(?<DatelessLongNumberSuffix>^[a-zA-Z]{1,2}[0-9]{1,4}$)|(?<DatelessShortNumberSufix>^[a-zA-Z]{1,3}[0-9]{1,3}$)|(?<DatelessNorthernIreland>^[a-zA-Z]{1,3}[0-9]{1,4}$)|(?<DiplomaticPlate>^[0-9]{3}[DX]{1}[0-9]{3}$)'
    //     ),
    //   ],
    // ],
    make: [null, [Validators.required]],
    model: [],
    colour: [],
    owners: [],
    garages: [],
    motExpiryDate: [],
  });

  colours: string[] = ['Black', 'White', 'Silver', 'Gray', 'Red', 'Blue', 'Green', 'Yellow', 'Other'];

  constructor(
    protected vehicleService: VehicleService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vehicle }) => {
      this.updateForm(vehicle);

      this.addOwnerToVehicle(vehicle);

      this.accountService
        .getAuthenticationState()
        .pipe(
          concatMap(account =>
            this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
          ),
          concatMap(g => g.pipe(map(garages => garages)))
        )
        .subscribe(gs => {
          this.garage = gs![0];
          this.editForm.get(['garages'])!.setValue(gs);

          this.customerService
            .queryGarageCustomers(this.garage.id)
            .pipe(flatMap((customersResponse: HttpResponse<ICustomer[]>) => of(customersResponse.body)))
            .subscribe(gj => {
              gj?.forEach(j => this.customers?.push(j));
              this.customers?.sort((a, b) => {
                const left = a?.id;
                const right = b.id;
                return left && right ? left - right : 0;
              });
            });
        });
    });
  }

  updateForm(vehicle: IVehicle): void {
    this.editForm.patchValue({
      id: vehicle.id,
      registration: vehicle.registration,
      make: vehicle.make,
      model: vehicle.model,
      colour: vehicle.colour,
      garages: vehicle.garages,
      motExpiryDate: vehicle.motExpiryDate,
      owners: vehicle.owners,
    });
  }

  verifyVehicle(): void {
    const registrationNumber = this.editForm.get('registration')!.value;
    this.vehicleService
      .verifyVehicle(registrationNumber)
      .pipe(flatMap(res => of(res.body)))
      .subscribe(data => {
        this.editForm.get('make')?.patchValue(data?.make);
        this.editForm.get('colour')?.patchValue(data?.colour);
        this.editForm.get('motExpiryDate')?.patchValue(data?.motExpiryDate);
      });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;

    const vehicle = this.createFromForm();

    if (vehicle.id) {
      this.subscribeToSaveResponse(this.vehicleService.update(vehicle));
    } else {
      this.subscribeToSaveResponse(this.vehicleService.create(vehicle));
    }
  }

  private addOwnerToVehicle(vehicle: IVehicle): void {
    const customerId = history.state?.customerId;
    if (customerId)
      this.customerService
        .find(customerId)
        .pipe(flatMap((res: HttpResponse<ICustomer>) => of(res.body)))
        .subscribe(customer => {
          if (customer) {
            if (vehicle.owners && !vehicle.owners?.some(c => c.id === customer.id)) {
              vehicle.owners?.push(customer);
            } else {
              vehicle.owners = [];
              vehicle.owners?.push(customer);
            }
            this.editForm.get(['owners'])!.patchValue(vehicle.owners);
          }
        });
  }

  private createFromForm(): IVehicle {
    return {
      ...new Vehicle(),
      id: this.editForm.get(['id'])!.value,
      registration: this.editForm.get(['registration'])!.value,
      make: this.editForm.get(['make'])!.value,
      model: this.editForm.get(['model'])!.value,
      colour: this.editForm.get(['colour'])!.value,
      garages: this.editForm.get(['garages'])!.value,
      motExpiryDate: this.editForm.get(['motExpiryDate'])!.value,
      owners: this.editForm.get(['owners'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVehicle>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: ICustomer): any {
    return item.id;
  }
}
