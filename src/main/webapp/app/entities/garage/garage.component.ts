import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGarage } from 'app/shared/model/garage.model';
import { GarageService } from './garage.service';
import { GarageDeleteDialogComponent } from './garage-delete-dialog.component';

@Component({
  selector: 'jhi-garage',
  templateUrl: './garage.component.html',
})
export class GarageComponent implements OnInit, OnDestroy {
  garages?: IGarage[];
  eventSubscriber?: Subscription;

  constructor(protected garageService: GarageService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.garageService.query().subscribe((res: HttpResponse<IGarage[]>) => (this.garages = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInGarages();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IGarage): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInGarages(): void {
    this.eventSubscriber = this.eventManager.subscribe('garageListModification', () => this.loadAll());
  }

  delete(garage: IGarage): void {
    const modalRef = this.modalService.open(GarageDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.garage = garage;
  }
}
