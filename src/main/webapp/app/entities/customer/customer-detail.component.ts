import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICustomer } from 'app/shared/model/customer.model';
import { from, of } from 'rxjs';
import { concatMap, flatMap, map } from 'rxjs/operators';
import { AccountService } from 'app/core/auth/account.service';
import { IGarage } from 'app/shared/model/garage.model';
import { IVehicle } from 'app/shared/model/vehicle.model';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { CustomerService } from './customer.service';
import { IBooking } from 'app/shared/model/booking.model';
import { BookingService } from '../booking/booking.service';
import { faPaperPlane, faFilePdf, faSpinner, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { JhiAlertService } from 'ng-jhipster';
import { Vehicle } from '../../shared/model/vehicle.model';

@Component({
  selector: 'jhi-customer-detail',
  templateUrl: './customer-detail.component.html',
})
export class CustomerDetailComponent implements OnInit {
  customer: ICustomer | null = null;

  vehicles: IVehicle[] = [];

  garage: IGarage | null = null;

  bookings: Map<Vehicle, IBooking[]> = new Map();

  isDownloading = false;
  isSending = false;

  currentVehicleId = 0;

  constructor(
    protected customerService: CustomerService,
    protected bookingService: BookingService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected activatedRoute: ActivatedRoute,
    protected alertService: JhiAlertService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customer }) => {
      this.customer = customer;

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
          const customerId = this.customer!.id;
          if (customerId && this.garage.id)
            this.customerService
              .queryCustomerVehicles(customerId, this.garage.id)
              .pipe(flatMap((res: HttpResponse<IVehicle[]>) => of(res.body)))
              .subscribe(vs => {
                if (vs) {
                  this.vehicles = vs;

                  from(this.vehicles).subscribe(v => {
                    if (v.id) this.getServiceHistory(customerId, v);
                  });
                }
              });
        });
    });
  }

  getServiceHistory(customerId: number, vehicle: IVehicle): void {
    const vehicleId = vehicle?.id;
    const garageId = this.garage!.id;
    if (garageId && vehicleId)
      this.customerService.queryServiceHistory(garageId, customerId, vehicleId).subscribe(data => {
        const histories: IBooking[] = [];
        if (data) {
          data.body!.forEach((d: IBooking) => {
            this.bookingService
              .queryBookingJobs(d.id, garageId)
              .pipe(map(res => res.body))
              .subscribe(ijobs => {
                if (d && ijobs) d.jobs = ijobs;
              });
            histories.push(d);
          });
        }

        if (vehicle && histories) {
          this.bookings.set(vehicle, histories);
        }
      });
  }

  generateAndDownloadServiceHistoryReport(vehicleId: number): void {
    const garageId = this.garage!.id;

    if (this.customer?.id && vehicleId) {
      this.currentVehicleId = vehicleId;
      this.isDownloading = true;
      this.bookingService.generateAndDownloadServiceHistory(this.customer?.id, vehicleId, garageId).subscribe((res: any) => {
        const headers = res.headers;
        const contentDisposition = headers.get('content-disposition');
        const result = contentDisposition.split(';')[2]?.trim().split('=')[1];
        const anchor = document.createElement('a');
        anchor.download = result?.replace(/"/g, '');
        anchor.href = (window.webkitURL || window.URL).createObjectURL(res.body);
        anchor.dataset.downloadurl = ['application/pdf', anchor.download, anchor.href].join(':');
        anchor.click();
        this.alertService.success('Service History Report downloaded.');
        (window.webkitURL || window.URL).revokeObjectURL(res.body);
        this.currentVehicleId = 0;
        this.isDownloading = false;
      });
    }
  }

  toggleSendSpinner(vehicleId: number): boolean {
    return this.currentVehicleId === vehicleId && this.isSending;
  }

  toggleDownloadSpinner(vehicleId: number): boolean {
    return this.currentVehicleId === vehicleId && this.isDownloading;
  }

  toggleDownloadIcon(vehicleId: number): IconDefinition {
    return this.currentVehicleId === vehicleId && this.isDownloading ? faSpinner : faFilePdf;
  }

  toggleDownloadMessage(vehicleId: number): string {
    return this.currentVehicleId === vehicleId && this.isDownloading
      ? 'Generating Service History Report'
      : 'Download Service History Report';
  }

  toggleSendIcon(vehicleId: number): IconDefinition {
    return this.currentVehicleId === vehicleId && this.isSending ? faSpinner : faPaperPlane;
  }

  toggleSendMessage(vehicleId: number): string {
    return this.currentVehicleId === vehicleId && this.isSending ? 'Sending Service History Report' : 'Send Service History Report';
  }

  sendServiceHistoryReport(vehicleId: number): void {
    const garageId = this.garage!.id;

    if (this.customer?.id && vehicleId) {
      this.currentVehicleId = vehicleId;
      this.isSending = true;
      this.bookingService.sendServiceHistoryReport(this.customer?.id, vehicleId, garageId).subscribe(
        () => {
          this.currentVehicleId = 0;
          this.isSending = false;
        },
        () => {
          this.currentVehicleId = 0;
          this.isSending = false;
        }
      );
    }
  }

  previousState(): void {
    window.history.back();
  }
}
