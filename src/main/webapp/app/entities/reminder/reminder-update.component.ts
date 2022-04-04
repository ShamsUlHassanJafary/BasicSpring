import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import * as moment from 'moment';
import { IReminder, Reminder } from 'app/shared/model/reminder.model';
import { ReminderService } from './reminder.service';
import { Customer, ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer/customer.service';
import { IVehicle, Vehicle } from 'app/shared/model/vehicle.model';
import { VehicleService } from 'app/entities/vehicle/vehicle.service';
import { IGarage } from 'app/shared/model/garage.model';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { AccountService } from 'app/core/auth/account.service';
import { concatMap, map } from 'rxjs/operators';
import { TIME_FORMAT } from 'app/shared/constants/input.constants';

type SelectableEntity = ICustomer | IVehicle | IGarage;

@Component({
  selector: 'jhi-reminder-update',
  templateUrl: './reminder-update.component.html',
})
export class ReminderUpdateComponent implements OnInit {
  isSaving = false;
  eventDateDp: any;
  alertDateDp: any;
  secondAlertDateDp: any;

  editForm = this.fb.group({
    id: [],
    description: [null, [Validators.required]],
    eventDate: [null, [Validators.required]],
    enabled: [null, [Validators.required]],
    emailEnabled: [null, [Validators.required]],
    smsEnabled: [null, [Validators.required]],
    alertDate: [null, [Validators.required]],
    alertTime: [],
    secondAlertDate: [],
    secondAlertTime: [],
    comment: [],
    customer: [],
    vehicle: [],
    garage: [null, Validators.required],
    reference: [],
  });

  constructor(
    protected reminderService: ReminderService,
    protected customerService: CustomerService,
    protected vehicleService: VehicleService,
    protected garageAdminService: GarageAdminService,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reminder }) => {
      this.updateForm(reminder);

      this.accountService
        .getAuthenticationState()
        .pipe(
          concatMap(account =>
            this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
          ),
          concatMap(g => g.pipe(map(garages => garages)))
        )
        .subscribe(gs => {
          const garage = gs![0];
          this.editForm.get(['garage'])!.setValue(garage);

          // this.customerService
          //   .queryGarageCustomers(garage.id)
          //   .pipe(flatMap((customersResponse: HttpResponse<ICustomer[]>) => of(customersResponse.body)))
          //   .subscribe(gj => {
          //     gj?.forEach(j => this.customers?.push(j));
          //     this.customers?.sort((a, b) => {
          //       const left = a?.id;
          //       const right = b.id;
          //       return left && right ? left - right : 0;
          //     });
          //   });

          // this.vehicleService
          //   .queryGarageVehicles(garage.id)
          //   .pipe(flatMap((vehiclesResponse: HttpResponse<IVehicle[]>) => of(vehiclesResponse.body)))
          //   .subscribe(gj => {
          //     gj?.forEach(j => this.vehicles?.push(j));
          //     this.vehicles?.sort((a, b) => {
          //       const left = a?.id;
          //       const right = b.id;
          //       return left && right ? left - right : 0;
          //     });
          //   });
        });
    });
  }

  vehicleDetailsToString(vehicle: IVehicle): String {
    return vehicle.registration?.toUpperCase() + ' (' + vehicle.make + ' ' + vehicle.model + ' ' + vehicle.colour + ')';
  }

  updateForm(reminder: IReminder): void {
    if (!reminder.vehicle) {
      reminder.vehicle = new Vehicle();
    }

    if (!reminder.customer) {
      reminder.customer = new Customer();
    }

    this.editForm.patchValue({
      id: reminder.id,
      description: reminder.description,
      eventDate: reminder.eventDate,
      alertTime: reminder.alertTime?.format(TIME_FORMAT),
      secondAlertTime: reminder.secondAlertTime?.format(TIME_FORMAT),
      enabled: reminder.enabled,
      emailEnabled: reminder.emailEnabled,
      smsEnabled: reminder.smsEnabled,
      alertDate: reminder.alertDate,
      secondAlertDate: reminder.secondAlertDate,
      comment: reminder.comment,
      customer: reminder.customer,
      vehicle: reminder.vehicle,
      garage: reminder.garage,
      selectedVehicle: reminder.vehicle,
      selectedCustomer: reminder.customer,
      reference: reminder.reference,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reminder = this.createFromForm();
    if (reminder.id !== undefined) {
      this.subscribeToSaveResponse(this.reminderService.update(reminder));
    } else {
      this.subscribeToSaveResponse(this.reminderService.create(reminder));
    }
  }

  private createFromForm(): IReminder {
    return {
      ...new Reminder(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      eventDate: this.editForm.get(['eventDate'])!.value,
      enabled: this.editForm.get(['enabled'])!.value,
      emailEnabled: this.editForm.get(['emailEnabled'])!.value,
      smsEnabled: this.editForm.get(['smsEnabled'])!.value,
      alertDate: this.editForm.get(['alertDate'])!.value,
      secondAlertDate: this.editForm.get(['secondAlertDate'])!.value,
      alertTime: this.editForm.get(['alertTime'])!.value != null ? moment(this.editForm.get(['alertTime'])!.value, TIME_FORMAT) : undefined,
      secondAlertTime:
        this.editForm.get(['secondAlertTime'])!.value != null
          ? moment(this.editForm.get(['secondAlertTime'])!.value, TIME_FORMAT)
          : undefined,
      comment: this.editForm.get(['comment'])!.value,
      customer: this.editForm.get(['customer'])!.value,
      vehicle: this.editForm.get(['vehicle'])!.value,
      garage: this.editForm.get(['garage'])!.value,
      reference: this.editForm.get(['reference'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReminder>>): void {
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

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
