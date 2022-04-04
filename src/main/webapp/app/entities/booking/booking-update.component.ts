import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import * as moment from 'moment';
import { TIME_FORMAT } from 'app/shared/constants/input.constants';
import { IBooking, Booking } from 'app/shared/model/booking.model';
import { BookingService } from './booking.service';
import { IVehicle, Vehicle } from 'app/shared/model/vehicle.model';
import { Customer, ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer/customer.service';
import { IGarage } from 'app/shared/model/garage.model';
import { JobService } from '../job/job.service';
import { IJob } from 'app/shared/model/job.model';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { AccountService } from 'app/core/auth/account.service';
import { concatMap, flatMap, map } from 'rxjs/operators';
import { BookingStatus } from 'app/shared/model/enumerations/booking-status.model';
import { IQuote, Quote } from 'app/shared/model/quote.model';

type SelectableEntity = ICustomer | IGarage | IVehicle | IJob;

@Component({
  selector: 'jhi-booking-update',
  templateUrl: './booking-update.component.html',
})
export class BookingUpdateComponent implements OnInit {
  isSaving = false;
  customers: ICustomer[] = [];
  garages: IGarage[] = [];
  jobs: IJob[] = [];
  bookingDateDp: any;

  editQuoteForm = this.fb.group({
    id: [],
    quoteDate: [],
    quoteTotal: [],
  });

  editForm = this.fb.group({
    id: [],
    bookingDate: [],
    bookingTime: [],
    jobs: this.fb.array([]),
    job: this.newJob(),
    furtherInstruction: [],
    customer: [],
    garage: { value: null, disabled: true },
    vehicle: [],
    quote: this.editQuoteForm,
    status: [],
    mileage: [],
    reference: [],
  });

  get jobsRequired(): FormArray {
    return this.editForm.get('jobs') as FormArray;
  }

  disabled = true;

  constructor(
    protected bookingService: BookingService,
    protected jobService: JobService,
    protected customerService: CustomerService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  newJob(): FormGroup {
    return this.fb.group({
      id: [],
      job: [],
      description: [],
      price: [],
    });
  }

  onChange($event: IJob): void {
    if ($event) this.editForm.get('job')?.patchValue($event);
    else this.editForm.get('job')?.reset();
  }

  isJobValid(): boolean {
    const newJob = this.editForm.get('job')!.value;

    return newJob.job?.label || newJob.description;
  }

  onAdd(): void {
    const newJob = this.editForm.get('job')!.value;
    if (newJob.job?.label) {
      newJob.id = undefined;
      newJob.description = newJob.job?.label;
      if (!newJob.price) {
        newJob.price = 0.0;
      }
      this.jobs.push(newJob);
    }

    this.jobsRequired.push(this.fb.group(newJob));
    this.editForm.get('job')?.reset();
    this.jobs.forEach(ji => {
      if (ji.id === newJob.id) {
        ji.disabled = true;
      }
    });
  }

  onRemove($event: any): void {
    this.jobsRequired.removeAt($event.index);
  }

  onDelete(index: number): void {
    this.jobs.forEach(ji => {
      if (ji.id === this.jobsRequired.at(index).value.id) {
        ji.disabled = false;
      }
    });
    this.jobsRequired.removeAt(index);
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ booking }) => {
      if (!booking.id) {
        const today = moment().startOf('day');
        booking.bookingTime = today;
      }

      this.accountService
        .getAuthenticationState()
        .pipe(
          concatMap(account =>
            this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
          ),
          concatMap(g => g.pipe(map(garages => garages)))
        )
        .subscribe(g => {
          this.garages = g || [];
          if (booking.garage === undefined || booking.garage === null) {
            booking.garage = this.garages[0];
          }

          let param = {};
          if (booking.id) param = { bookingId: booking.id };

          this.jobService
            .queryGarageJobs(this.garages[0].id, param)
            .pipe(flatMap((jobsResponse: HttpResponse<IJob[]>) => of(jobsResponse.body)))
            .subscribe(gj => {
              this.jobs = gj && gj.length > 0 ? gj : [];
              this.updateForm(booking);
            });
        });
    });
  }

  updateForm(booking: IBooking): void {
    if (!booking.vehicle) {
      booking.vehicle = new Vehicle();
    }

    if (!booking.customer) {
      booking.customer = new Customer();
    }

    if (!booking.jobs) {
      booking.jobs = [];
    }
    if (booking.jobs.length > 0) {
      booking.jobs.forEach(j => {
        this.jobs.forEach(ji => {
          if (ji.id === j.id) {
            ji.disabled = true;
          }
        });
        this.jobsRequired.push(this.fb.group(j));
      });
    }

    if (!booking.quote) {
      booking.quote = new Quote();
    }

    this.editForm.patchValue({
      id: booking.id,
      bookingDate: booking.bookingDate,
      bookingTime: booking.bookingTime?.format(TIME_FORMAT),
      furtherInstruction: booking.furtherInstruction,
      customer: booking.customer,
      garage: booking.garage,
      vehicle: booking.vehicle,
      jobs: booking.jobs,
      status: booking.status,
      quote: booking.quote,
      selectedCustomer: booking.customer,
      mileage: booking.mileage,
      reference: booking.reference,
    });

    this.editQuoteForm.patchValue({
      id: booking.quote?.id,
      quoteDate: booking.quote?.quoteDate,
      quoteTotal: booking.quote?.quoteTotal,
    });

    this.editForm.get('jobs')?.valueChanges.subscribe(val => {
      this.editForm.get(['quoteTotal'])?.patchValue(this.getTotal(val));
    });
  }

  getTotal(selectedVals: IJob[]): number {
    return selectedVals && selectedVals.length > 0 ? selectedVals.map(j => j.price).reduce((a = 0, b = 0) => a + b) || 0 : 0;
  }

  previousState(): void {
    window.history.back();
  }

  startJob(): void {
    if (this.editForm.get(['status'])!.value === BookingStatus.CONFIRMED) {
      this.editForm.get(['status'])!.setValue(BookingStatus.IN_PROGRESS);
    }

    this.save();
  }

  endJob(): void {
    if (this.editForm.get(['status'])!.value === BookingStatus.IN_PROGRESS) {
      this.editForm.get(['status'])!.setValue(BookingStatus.COMPLETED);
    }

    this.save();
  }

  initQuote(): void {
    let quote: IQuote = new Quote();

    if (this.editForm.get(['quote'])!.value) {
      quote = this.editForm.get(['quote'])!.value;
    }

    quote.quoteTotal = this.getTotal(this.editForm.get('jobs')!.value);
    quote.quoteDate = moment();
    this.editForm.get(['quote'])!.patchValue(quote);
  }

  cancelJob(): void {
    this.editForm.get(['status'])!.setValue(BookingStatus.CANCELLED);
    this.save();
  }

  save(): void {
    this.isSaving = true;

    this.initQuote();

    const booking = this.createFromForm();

    if (booking.id) {
      if (booking.status === BookingStatus.PENDING && this.isBookingValid()) {
        booking.status = BookingStatus.CONFIRMED;
      }

      this.subscribeToSaveResponse(this.bookingService.update(booking));
    } else {
      if (this.isBookingValid()) {
        booking.status = BookingStatus.CONFIRMED;
      }

      this.subscribeToSaveResponse(this.bookingService.create(booking));
    }
  }

  private createFromForm(): IBooking {
    return {
      ...new Booking(),
      id: this.editForm.get(['id'])!.value,
      bookingDate: this.editForm.get(['bookingDate'])!.value,
      bookingTime: this.editForm.get(['bookingTime'])!.value ? moment(this.editForm.get(['bookingTime'])!.value, TIME_FORMAT) : undefined,
      furtherInstruction: this.editForm.get(['furtherInstruction'])!.value,
      customer: this.editForm.get(['customer'])!.value,
      vehicle: this.editForm.get(['vehicle'])!.value,
      garage: this.editForm.get(['garage'])!.value,
      jobs: this.editForm.get(['jobs'])!.value,
      status: this.editForm.get(['status'])!.value,
      quote: this.editForm.get(['quote'])!.value,
      mileage: this.editForm.get(['mileage'])!.value,
      reference: this.editForm.get(['reference'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBooking>>): void {
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

  getSelected(selectedVals: IJob[], option: IJob): IJob {
    if (selectedVals && selectedVals.length > 0) {
      for (const value of selectedVals) {
        if (option.id === value.id) {
          return value;
        }
      }
    }
    return option;
  }

  isBookingValid(): boolean {
    return this.editForm.get(['bookingDate'])!.value && this.editForm.get(['bookingTime'])!.value;
  }

  isValid(): boolean {
    return this.isCustomerValid() && this.isVehicleValid() && this.jobsRequired.length > 0;
  }

  isCustomerValid(): boolean {
    return this.editForm.get('customer')!.value && this.editForm.get('customer')!.value.id;
  }

  isVehicleValid(): boolean {
    return this.editForm.get('vehicle')!.value && this.editForm.get('vehicle')!.value.id;
  }
}
