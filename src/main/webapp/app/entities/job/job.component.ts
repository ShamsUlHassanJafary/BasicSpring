import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { of, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IJob } from 'app/shared/model/job.model';
import { JobService } from './job.service';
import { JobDeleteDialogComponent } from './job-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { map, flatMap, concatMap } from 'rxjs/operators';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { IGarage } from 'app/shared/model/garage.model';

@Component({
  selector: 'jhi-job',
  templateUrl: './job.component.html',
})
export class JobComponent implements OnInit, OnDestroy {
  jobs?: IJob[];
  eventSubscriber?: Subscription;

  constructor(
    protected jobService: JobService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(
        concatMap(account =>
          this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
        ),
        concatMap(g => g.pipe(map(garages => garages)))
      )
      .subscribe(gs => {
        this.jobs = [];
        if (gs)
          this.jobService
            .queryGarageJobs(gs[0].id)
            .pipe(flatMap((jobsResponse: HttpResponse<IJob[]>) => of(jobsResponse.body)))
            .subscribe(gj => {
              if (gj && gj.length > 0) {
                gj?.forEach(j => this.jobs?.push(j));
                this.jobs?.sort((a, b) => {
                  const left = a?.id;
                  const right = b.id;
                  return left && right ? left - right : 0;
                });
              }
            });
      });

    // this.jobService.query().subscribe((res: HttpResponse<IJob[]>) => (this.jobs = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInJobs();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IJob): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInJobs(): void {
    this.eventSubscriber = this.eventManager.subscribe('jobListModification', () => this.loadAll());
  }

  delete(job: IJob): void {
    const modalRef = this.modalService.open(JobDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.job = job;
  }
}
