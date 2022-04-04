import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IGarageAdmin, GarageAdmin } from 'app/shared/model/garage-admin.model';
import { GarageAdminService } from './garage-admin.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { GarageService } from '../garage/garage.service';
import { IGarage } from 'app/shared/model/garage.model';
import { map } from 'rxjs/operators';

type SelectableEntity = IGarage | IUser;

@Component({
  selector: 'jhi-garage-admin-update',
  templateUrl: './garage-admin-update.component.html',
})
export class GarageAdminUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  garages: IGarage[] = [];

  editForm = this.fb.group({
    id: [],
    user: [],
    garage: [],
  });

  constructor(
    protected garageAdminService: GarageAdminService,
    private garageService: GarageService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ garageAdmin }) => {
      this.updateForm(garageAdmin);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));

      this.garageService
        .query()
        .pipe(map((res: HttpResponse<IGarage[]>) => res.body || []))
        .subscribe((resBody: IGarage[]) => {
          this.garages = resBody;
        });
    });
  }

  updateForm(garageAdmin: IGarageAdmin): void {
    this.editForm.patchValue({
      id: garageAdmin.id,
      user: garageAdmin.user,
      garage: garageAdmin.garage,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const garageAdmin = this.createFromForm();
    if (garageAdmin.id !== undefined) {
      this.subscribeToSaveResponse(this.garageAdminService.update(garageAdmin));
    } else {
      this.subscribeToSaveResponse(this.garageAdminService.create(garageAdmin));
    }
  }

  private createFromForm(): IGarageAdmin {
    return {
      ...new GarageAdmin(),
      id: this.editForm.get(['id'])!.value,
      user: this.editForm.get(['user'])!.value,
      garage: this.editForm.get(['garage'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGarageAdmin>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
