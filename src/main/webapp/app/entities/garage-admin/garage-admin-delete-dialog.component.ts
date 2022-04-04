import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IGarageAdmin } from 'app/shared/model/garage-admin.model';
import { GarageAdminService } from './garage-admin.service';

@Component({
  templateUrl: './garage-admin-delete-dialog.component.html',
})
export class GarageAdminDeleteDialogComponent {
  garageAdmin?: IGarageAdmin;

  constructor(
    protected garageAdminService: GarageAdminService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.garageAdminService.delete(id).subscribe(() => {
      this.eventManager.broadcast('garageAdminListModification');
      this.activeModal.close();
    });
  }
}
