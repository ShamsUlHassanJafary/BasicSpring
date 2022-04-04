import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IBooking } from 'app/shared/model/booking.model';
import { BookingService } from './booking.service';

@Component({
  templateUrl: './booking-delete-dialog.component.html',
})
export class BookingDeleteDialogComponent {
  booking?: IBooking;

  constructor(protected bookingService: BookingService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bookingService.delete(id).subscribe(() => {
      this.eventManager.broadcast('bookingListModification');
      this.activeModal.close();
    });
  }
}
