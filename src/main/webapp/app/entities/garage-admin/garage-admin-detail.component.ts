import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGarageAdmin } from 'app/shared/model/garage-admin.model';

@Component({
  selector: 'jhi-garage-admin-detail',
  templateUrl: './garage-admin-detail.component.html',
})
export class GarageAdminDetailComponent implements OnInit {
  garageAdmin: IGarageAdmin | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ garageAdmin }) => (this.garageAdmin = garageAdmin));
  }

  previousState(): void {
    window.history.back();
  }
}
