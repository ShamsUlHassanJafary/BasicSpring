import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { IGarageType } from 'app/shared/model/garage-type.model';
import { GarageTypeService } from './garage-type.service';

@Component({
  selector: 'jhi-garage-type',
  templateUrl: './garage-type.component.html',
})
export class GarageTypeComponent implements OnInit, OnDestroy {
  garageTypes?: IGarageType[];
  eventSubscriber?: Subscription;

  constructor(protected garageTypeService: GarageTypeService, protected eventManager: JhiEventManager) {}

  loadAll(): void {
    this.garageTypeService.query().subscribe((res: HttpResponse<IGarageType[]>) => (this.garageTypes = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInGarageTypes();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IGarageType): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInGarageTypes(): void {
    this.eventSubscriber = this.eventManager.subscribe('garageTypeListModification', () => this.loadAll());
  }
}
