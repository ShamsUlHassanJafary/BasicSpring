import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGarageType } from 'app/shared/model/garage-type.model';

@Component({
  selector: 'jhi-garage-type-detail',
  templateUrl: './garage-type-detail.component.html',
})
export class GarageTypeDetailComponent implements OnInit {
  garageType: IGarageType | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ garageType }) => (this.garageType = garageType));
  }

  previousState(): void {
    window.history.back();
  }
}
