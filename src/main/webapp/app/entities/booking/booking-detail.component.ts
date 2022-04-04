import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { IBooking } from 'app/shared/model/booking.model';
import { BookingStatus } from 'app/shared/model/enumerations/booking-status.model';
import { BookingService } from './booking.service';

@Component({
  selector: 'jhi-booking-detail',
  templateUrl: './booking-detail.component.html',
})
export class BookingDetailComponent implements OnInit {
  booking: IBooking | null = null;

  isDownloading = false;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected bookingService: BookingService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ booking }) => (this.booking = booking));
  }

  previousState(): void {
    window.history.back();
  }

  isComplete(booking: IBooking): boolean {
    return booking?.status === BookingStatus.COMPLETED || booking?.status === BookingStatus.INVOICED;
  }

  createInvoice(): void {
    if (this.booking) this.router.navigate(['/invoice', 'new'], { state: { bookingId: this.booking?.id } });
  }
}
