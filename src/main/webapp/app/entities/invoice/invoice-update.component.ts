import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { concatMap, flatMap, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IInvoice, Invoice } from 'app/shared/model/invoice.model';
import { InvoiceService } from './invoice.service';
import { IQuote } from 'app/shared/model/quote.model';
import { IJob } from 'app/shared/model/job.model';
import { AccountService } from 'app/core/auth/account.service';
import { IGarage } from 'app/shared/model/garage.model';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { JobService } from '../job/job.service';
import { Booking } from 'app/shared/model/booking.model';
import { Customer } from 'app/shared/model/customer.model';
import { Vehicle } from 'app/shared/model/vehicle.model';
import { BookingService } from '../booking/booking.service';

type SelectableEntity = IJob | IQuote;

@Component({
  selector: 'jhi-invoice-update',
  templateUrl: './invoice-update.component.html',
})
export class InvoiceUpdateComponent implements OnInit {
  isSaving = false;
  jobs: IJob[] = [];
  garage: IGarage | undefined;

  editBookingForm = this.fb.group({
    id: [],
    bookingDate: [],
    bookingTime: [],
    jobs: this.fb.array([], Validators.required),
    job: this.newJob(),
    furtherInstruction: [],
    customer: [],
    garage: [],
    vehicle: [],
    status: [],
    mileage: [],
    reference: [],
  });

  editForm = this.fb.group({
    id: [],
    invoiceDate: [],
    issueDate: [],
    invoiceTotal: [],
    paid: [{ value: null, disabled: true }],
    balance: [],
    discount: [{ value: null, disabled: true }, [Validators.min(0), Validators.max(100)]],
    booking: this.editBookingForm,
    reference: [],
  });

  get discount(): FormControl {
    return this.editForm.get('discount') as FormControl;
  }

  get balance(): FormControl {
    return this.editForm.get('balance') as FormControl;
  }

  get paid(): FormControl {
    return this.editForm.get('paid') as FormControl;
  }

  get jobsRequired(): FormArray {
    return this.editBookingForm.get('jobs') as FormArray;
  }

  disabled = true;

  constructor(
    protected invoiceService: InvoiceService,
    protected bookingService: BookingService,
    protected jobService: JobService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ invoice }) => {
      if (!invoice.id) {
        const today = moment().startOf('day');
        invoice.invoiceDate = today;
        invoice.issueDate = today;
        invoice.paid = 0;
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
          const invoiceBookingId = history.state?.bookingId;
          if (invoiceBookingId) {
            param = { bookingId: invoiceBookingId };
            this.bookingService
              .find(invoiceBookingId)
              .pipe(flatMap((res: HttpResponse<Booking>) => of(res.body)))
              .subscribe(booking => {
                if (booking) {
                  invoice.booking = booking;

                  this.initGarageJobs(param).subscribe(gj => {
                    this.jobs = gj && gj.length > 0 ? gj : [];
                    if (booking.jobs) invoice.invoiceTotal = this.getTotal(booking.jobs);
                    this.updateForm(invoice);
                  });
                }
              });
          } else {
            this.initGarageJobs().subscribe(gj => {
              this.jobs = gj && gj.length > 0 ? gj : [];
              this.updateForm(invoice);
            });
          }
        });
    });
  }

  private initGarageJobs(param?: {}): Observable<IJob[]> {
    return this.jobService
      .queryGarageJobs(this.garage?.id, param)
      .pipe(flatMap((jobsResponse: HttpResponse<IJob[]>) => of(jobsResponse.body || [])));
  }

  updateForm(invoice: IInvoice): void {
    let invoiceBooking = invoice.booking;

    if (!invoiceBooking) {
      invoiceBooking = new Booking();
    }

    if (!invoiceBooking.vehicle) {
      invoiceBooking.vehicle = new Vehicle();
    }

    if (!invoiceBooking?.garage) invoiceBooking.garage = this.garage;

    if (!invoiceBooking.customer) {
      invoiceBooking.customer = new Customer();
    }

    if (!invoiceBooking.jobs) {
      invoiceBooking.jobs = [];
    }
    if (invoiceBooking.jobs.length > 0) {
      invoiceBooking.jobs.forEach(j => {
        this.jobs.forEach(ji => {
          if (ji.id === j.id) {
            ji.disabled = true;
          }
        });
        this.jobsRequired.push(this.fb.group(j));
      });
    }

    this.editBookingForm.patchValue({
      id: invoiceBooking.id,
      bookingDate: invoiceBooking.bookingDate,
      bookingTime: invoiceBooking.bookingTime,
      furtherInstruction: invoiceBooking.furtherInstruction,
      customer: invoiceBooking.customer,
      garage: invoiceBooking.garage,
      vehicle: invoiceBooking.vehicle,
      jobs: invoiceBooking.jobs,
      mileage: invoiceBooking.mileage,
      reference: invoiceBooking.reference,
    });

    this.editForm.patchValue({
      id: invoice.id,
      invoiceDate: invoice.invoiceDate ? invoice.invoiceDate.format(DATE_TIME_FORMAT) : null,
      issueDate: invoice.issueDate,
      invoiceTotal: invoice.invoiceTotal,
      paid: invoice.paid,
      balance: invoice.balance,
      discount: invoice.discount,
      booking: invoiceBooking,
      reference: invoice.reference,
    });

    this.editBookingForm.get('jobs')?.valueChanges.subscribe(val => {
      this.editForm.get(['invoiceTotal'])?.patchValue(this.getTotal(val).toFixed(2));
    });

    this.balance?.patchValue((this.editForm.get('invoiceTotal')!.value - this.paid.value).toFixed(2));

    this.discount?.valueChanges.subscribe(() => {
      this.editForm
        .get(['invoiceTotal'])
        ?.patchValue(this.applyDiscount(this.getTotal(this.editBookingForm.get('jobs')!.value)).toFixed(2));
    });

    this.paid.valueChanges.subscribe(() => {
      this.balance?.patchValue((this.editForm.get('invoiceTotal')!.value - this.paid.value).toFixed(2));
    });

    this.updateCalculation();
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;

    const invoice = this.createFromForm();

    if (invoice.id) {
      this.subscribeToSaveResponse(this.invoiceService.update(invoice));
    } else {
      this.subscribeToSaveResponse(this.invoiceService.create(invoice));
    }
  }

  private createFromForm(): IInvoice {
    return {
      ...new Invoice(),
      id: this.editForm.get(['id'])!.value,
      issueDate: this.editForm.get(['issueDate'])!.value,
      invoiceDate: this.editForm.get(['invoiceDate'])!.value
        ? moment(this.editForm.get(['invoiceDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      invoiceTotal: this.editForm.get(['invoiceTotal'])!.value,
      paid: this.editForm.get(['paid'])!.value,
      balance: this.editForm.get(['balance'])!.value,
      discount: this.editForm.get(['discount'])!.value,
      booking: this.editForm.get(['booking'])!.value,
      reference: this.editForm.get(['reference'])!.value,
    };
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

    this.updateCalculation();
  }

  updateCalculation(): void {
    if (this.jobsRequired.length > 0) {
      this.paid.enable();
      this.discount.enable();
    } else {
      this.paid.disable();
      this.discount.disable();
    }
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
    this.updateCalculation();
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInvoice>>): void {
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
