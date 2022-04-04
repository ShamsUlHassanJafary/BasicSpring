import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGarageAdmin } from 'app/shared/model/garage-admin.model';
import { GarageAdminService } from './garage-admin.service';
import { GarageAdminDeleteDialogComponent } from './garage-admin-delete-dialog.component';

@Component({
  selector: 'jhi-garage-admin',
  templateUrl: './garage-admin.component.html',
})
export class GarageAdminComponent implements OnInit, OnDestroy {
  garageAdmins?: IGarageAdmin[];
  eventSubscriber?: Subscription;

  constructor(
    protected garageAdminService: GarageAdminService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.garageAdminService.query().subscribe((res: HttpResponse<IGarageAdmin[]>) => (this.garageAdmins = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInGarageAdmins();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IGarageAdmin): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInGarageAdmins(): void {
    this.eventSubscriber = this.eventManager.subscribe('garageAdminListModification', () => this.loadAll());
  }

  delete(garageAdmin: IGarageAdmin): void {
    const modalRef = this.modalService.open(GarageAdminDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.garageAdmin = garageAdmin;
  }
}
