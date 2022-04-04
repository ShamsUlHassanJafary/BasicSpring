import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IReminder } from 'app/shared/model/reminder.model';
import { ReminderService } from './reminder.service';

@Component({
  templateUrl: './reminder-delete-dialog.component.html',
})
export class ReminderDeleteDialogComponent {
  reminder?: IReminder;

  constructor(protected reminderService: ReminderService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.reminderService.delete(id).subscribe(() => {
      this.eventManager.broadcast('reminderListModification');
      this.activeModal.close();
    });
  }
}
