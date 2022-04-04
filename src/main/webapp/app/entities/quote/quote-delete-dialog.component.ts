import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IQuote } from 'app/shared/model/quote.model';
import { QuoteService } from './quote.service';

@Component({
  templateUrl: './quote-delete-dialog.component.html',
})
export class QuoteDeleteDialogComponent {
  quote?: IQuote;

  constructor(protected quoteService: QuoteService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.quoteService.delete(id).subscribe(() => {
      this.eventManager.broadcast('quoteListModification');
      this.activeModal.close();
    });
  }
}
