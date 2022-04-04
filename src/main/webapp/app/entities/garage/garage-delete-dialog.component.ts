import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IGarage } from 'app/shared/model/garage.model';
import { GarageService } from './garage.service';

@Component({
  templateUrl: './garage-delete-dialog.component.html',
})
export class GarageDeleteDialogComponent {
  garage?: IGarage;

  constructor(protected garageService: GarageService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.garageService.delete(id).subscribe(() => {
      this.eventManager.broadcast('garageListModification');
      this.activeModal.close();
    });
  }
}
