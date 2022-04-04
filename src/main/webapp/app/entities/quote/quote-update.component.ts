import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { concatMap, flatMap, map } from 'rxjs/operators';

import { IQuote, Quote } from 'app/shared/model/quote.model';
import { QuoteService } from './quote.service';
import { Booking } from 'app/shared/model/booking.model';
import { BookingService } from 'app/entities/booking/booking.service';
import { IJob } from 'app/shared/model/job.model';
import { JobService } from '../job/job.service';
import { IVehicle, Vehicle } from 'app/shared/model/vehicle.model';
import { Customer, ICustomer } from 'app/shared/model/customer.model';
import { AccountService } from 'app/core/auth/account.service';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { IGarage } from 'app/shared/model/garage.model';
import * as moment from 'moment';
import { BookingStatus } from 'app/shared/model/enumerations/booking-status.model';
type SelectableEntity = IJob | ICustomer | IVehicle;

@Component({
  selector: 'jhi-quote-update',
  templateUrl: './quote-update.component.html',
})
export class QuoteUpdateComponent implements OnInit {
  isSaving = false;
  jobs: IJob[] = [];
  garage: IGarage | undefined;
  quoteDateDp: any;

  editBookingForm = this.fb.group({
    id: [],
    bookingDate: [],
    bookingTime: [],
    jobs: this.fb.array([]),
    job: this.newJob(),
    furtherInstruction: [],
    serviceHistory: [],
    customer: [],
    garage: [],
    vehicle: [],
    status: [],
    mileage: [],
    reference: [],
  });

  editForm = this.fb.group({
    id: [],
    quoteDate: [],
    quoteTotal: [],
    discount: [null, [Validators.min(0), Validators.max(100)]],
    booking: this.editBookingForm,
    reference: [],
  });

  get discount(): FormControl {
    return this.editForm.get('discount') as FormControl;
  }

  get jobsRequired(): FormArray {
    return this.editBookingForm.get('jobs') as FormArray;
  }

  disabled = true;

  bookingStatus: BookingStatus | undefined;

  constructor(
    protected quoteService: QuoteService,
    protected bookingService: BookingService,
    protected jobService: JobService,
    protected garageAdminService: GarageAdminService,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ quote }) => {
      if (!quote.id) {
        const today = moment().startOf('day');
        quote.quoteDate = today;
      }

      this.accountService
        .getAuthenticationState()
        .pipe(
          concatMap(account =>
            this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
          ),
          concatMap(g => g.pipe(map(garages => garages)))
        )
        .subscribe(gs => {
          this.garage = gs && gs.length > 0 ? gs[0] : undefined;
          let param = {};
          if (quote.booking && quote.booking.id) {
            param = { bookingId: quote.booking.id };
          }

          this.jobService
            .queryGarageJobs(this.garage?.id, param)
            .pipe(flatMap((jobsResponse: HttpResponse<IJob[]>) => of(jobsResponse.body)))
            .subscribe(gj => {
              this.jobs = (gj && gj.length > 0 && gj) || [];
            });

          this.updateForm(quote);
        });
    });
  }

  newJob(): FormGroup {
    return this.fb.group({
      id: [],
      job: [],
      description: [],
      price: [],
    });
  }

  onChange($event: IJob): void {
    this.editBookingForm.get('job')?.patchValue($event);
  }

  isJobValid(): boolean {
    const newJob = this.editBookingForm.get('job')!.value;

    return newJob.job?.label || newJob.description;
  }

  onAdd(): void {
    const newJob = this.editBookingForm.get('job')!.value;
    if (newJob.job?.label) {
      newJob.id = undefined;
      newJob.description = newJob.job?.label;
      if (!newJob.price) {
        newJob.price = 0.0;
      }
      this.jobs.push(newJob);
    }

    this.jobsRequired.push(this.fb.group(newJob));
    this.editBookingForm.get('job')?.reset();
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

  updateForm(quote: IQuote): void {
    let quoteBooking = quote.booking;

    if (!quoteBooking) {
      quoteBooking = new Booking();
      quoteBooking.status = BookingStatus.PENDING;
    }

    if (!quoteBooking.vehicle) {
      quoteBooking.vehicle = new Vehicle();
    }

    if (!quoteBooking.garage) quoteBooking.garage = this.garage;

    if (!quoteBooking.customer) {
      quoteBooking.customer = new Customer();
    }

    if (!quoteBooking.jobs) {
      quoteBooking.jobs = [];
    }
    if (quoteBooking.jobs.length > 0) {
      quoteBooking.jobs.forEach(j => {
        this.jobs.forEach(ji => {
          if (ji.id === j.id) {
            ji.disabled = true;
          }
        });
        this.jobsRequired.push(this.fb.group(j));
      });
    }

    this.editBookingForm.patchValue({
      id: quoteBooking.id,
      bookingDate: quoteBooking.bookingDate,
      bookingTime: quoteBooking.bookingTime,
      furtherInstruction: quoteBooking.furtherInstruction,
      customer: quoteBooking.customer,
      garage: quoteBooking.garage,
      vehicle: quoteBooking.vehicle,
      jobs: quoteBooking.jobs,
      mileage: quoteBooking.mileage,
      reference: quoteBooking.reference,
    });

    this.editForm.patchValue({
      id: quote.id,
      quoteDate: quote.quoteDate,
      quoteTotal: quote.quoteTotal,
      discount: quote.discount,
      booking: quoteBooking,
      reference: quote.reference,
    });

    this.editBookingForm.get('jobs')?.valueChanges.subscribe(val => {
      this.editForm.get(['quoteTotal'])?.patchValue(this.applyDiscount(this.getTotal(val)));
    });

    this.editForm.get('discount')?.valueChanges.subscribe(() => {
      this.editForm.get(['quoteTotal'])?.patchValue(this.applyDiscount(this.getTotal(this.editBookingForm.get('jobs')!.value)));
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;

    const quote = this.createFromForm();
    if (quote.id) {
      this.subscribeToSaveResponse(this.quoteService.update(quote));
    } else {
      this.subscribeToSaveResponse(this.quoteService.create(quote));
    }
  }

  private createFromForm(): IQuote {
    return {
      ...new Quote(),
      id: this.editForm.get(['id'])!.value,
      quoteDate: this.editForm.get(['quoteDate'])!.value,
      quoteTotal: this.editForm.get(['quoteTotal'])!.value,
      discount: this.editForm.get(['discount'])!.value,
      booking: this.editForm.get(['booking'])!.value,
      reference: this.editForm.get(['reference'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IQuote>>): void {
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

  applyDiscount(total: number): number {
    if (this.editForm.get(['discount'])!.value) {
      return total - (this.editForm.get(['discount'])!.value / 100) * total;
    }

    return total;
  }

  getTotal(selectedVals: IJob[]): number {
    if (selectedVals && selectedVals.length > 0) return selectedVals.map(j => j.price).reduce((a = 0, b = 0) => a + b) || 0;

    return 0;
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

  isValid(): boolean {
    return this.isCustomerValid() && this.isVehicleValid() && this.jobsRequired.length > 0;
  }

  isCustomerValid(): boolean {
    return this.editBookingForm.get('customer')!.value && this.editBookingForm.get('customer')!.value.id;
  }

  isVehicleValid(): boolean {
    return this.editBookingForm.get('vehicle')!.value && this.editBookingForm.get('vehicle')!.value.id;
  }
}
