import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';

import { ICustomer, Customer } from 'app/shared/model/customer.model';
import { CustomerService } from './customer.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { AccountService } from 'app/core/auth/account.service';
import { IGarage } from 'app/shared/model/garage.model';
import { concatMap, map } from 'rxjs/operators';
import { GarageAdminService } from '../garage-admin/garage-admin.service';

@Component({
  selector: 'jhi-customer-update',
  templateUrl: './customer-update.component.html',
})
export class CustomerUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  garage!: IGarage;

  editForm = this.fb.group({
    id: [],
    hasDataKeepConsent: [],
    hasMarketingContent: [],
    hasNotificationContent: [],
    phoneNumber: [null, [Validators.required, Validators.minLength(11), Validators.maxLength(13)]],
    firstName: [null, [Validators.required, Validators.maxLength(50)]],
    lastName: [null, [Validators.required, Validators.maxLength(50)]],
    email: [null, [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    user: [],
    garages: [],
  });

  constructor(
    protected customerService: CustomerService,
    protected userService: UserService,
    protected accountService: AccountService,
    protected garageAdminService: GarageAdminService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customer }) => {
      this.updateForm(customer);

      this.initGarage();

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(customer: ICustomer): void {
    this.editForm.patchValue({
      id: customer.id,
      hasDataKeepConsent: customer.hasDataKeepConsent,
      hasMarketingContent: customer.hasMarketingContent,
      hasNotificationContent: customer.hasNotificationContent,
      phoneNumber: customer.phoneNumber,
      firstName: customer.firstName,
      lastName: customer.lastName,
      email: customer.email,
      user: customer.user,
      garages: customer.garages,
    });
  }

  initGarage(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(
        concatMap(account =>
          this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
        ),
        concatMap(g => g.pipe(map(garages => garages)))
      )
      .subscribe(gs => {
        this.editForm.get(['garages'])!.setValue(gs);
      });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customer = this.createFromForm();
    if (customer.id !== undefined) {
      this.subscribeToSaveResponse(this.customerService.update(customer));
    } else {
      this.subscribeToSaveResponse(this.customerService.create(customer));
    }
  }

  private createFromForm(): ICustomer {
    return {
      ...new Customer(),
      id: this.editForm.get(['id'])!.value,
      hasDataKeepConsent: this.editForm.get(['hasDataKeepConsent'])!.value,
      hasMarketingContent: this.editForm.get(['hasMarketingContent'])!.value,
      hasNotificationContent: this.editForm.get(['hasNotificationContent'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
      user: this.editForm.get(['user'])!.value,
      garages: this.editForm.get(['garages'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomer>>): void {
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

  trackById(index: number, item: IUser): any {
    return item.id;
  }
}
