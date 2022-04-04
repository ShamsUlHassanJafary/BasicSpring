import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IJob, Job } from 'app/shared/model/job.model';
import { JobService } from './job.service';
import { IGarage } from 'app/shared/model/garage.model';
import { AccountService } from 'app/core/auth/account.service';
import { concatMap, map } from 'rxjs/operators';
import { GarageAdminService } from '../garage-admin/garage-admin.service';

@Component({
  selector: 'jhi-job-update',
  templateUrl: './job-update.component.html',
})
export class JobUpdateComponent implements OnInit {
  isSaving = false;

  garage: IGarage | null | undefined;

  editForm = this.fb.group({
    id: [],
    description: [null, [Validators.required]],
    dateCreated: [],
    price: [null, [Validators.required]],
    garage: [],
    reference: [],
  });

  constructor(
    protected jobService: JobService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ job }) => {
      if (!job.id) {
        const today = moment().startOf('day');
        job.dateCreated = today;
      }

      this.accountService
        .getAuthenticationState()
        .pipe(
          concatMap(account =>
            this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
          ),
          concatMap(g => g.pipe(map(garages => garages)))
        )
        .subscribe(g => {
          this.garage = g ? g[0] : null;

          job.garage = this.garage;
          this.updateForm(job);
        });
    });
  }

  updateForm(job: IJob): void {
    this.editForm.patchValue({
      id: job.id,
      description: job.description,
      dateCreated: job.dateCreated ? job.dateCreated.format(DATE_TIME_FORMAT) : null,
      price: job.price,
      garage: job.garage,
      reference: job.reference,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const job = this.createFromForm();
    if (job.id !== undefined) {
      this.subscribeToSaveResponse(this.jobService.update(job));
    } else {
      job.dateCreated = moment();
      this.subscribeToSaveResponse(this.jobService.create(job));
    }
  }

  private createFromForm(): IJob {
    return {
      ...new Job(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      dateCreated: this.editForm.get(['dateCreated'])!.value
        ? moment(this.editForm.get(['dateCreated'])!.value, DATE_TIME_FORMAT)
        : undefined,
      price: this.editForm.get(['price'])!.value,
      garage: this.editForm.get(['garage'])!.value,
      reference: this.editForm.get(['reference'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJob>>): void {
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
}
