import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { GarageService } from 'app/entities/garage/garage.service';
import { Authority } from 'app/shared/constants/authority.constants';
import { Garage, IGarage } from 'app/shared/model/garage.model';
import { map } from 'rxjs/operators';

@Component({
  selector: 'jhi-user-mgmt-update',
  templateUrl: './user-management-update.component.html',
})
export class UserManagementUpdateComponent implements OnInit {
  user!: User;
  authorities: string[] = [];
  garages: IGarage[] = [];
  isSaving = false;

  editGarageForm = this.fb.group({
    id: [],
    businessName: [],
    lineAddress1: [],
    lineAddress2: [],
    city: [],
    county: [],
    postcode: [],
    country: [],
    bookings: [],
    jobs: [],
    garageAdmins: [],
  });

  editForm = this.fb.group({
    id: [],
    login: [
      '',
      [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    ],
    firstName: ['', [Validators.maxLength(50)]],
    lastName: ['', [Validators.maxLength(50)]],
    email: ['', [Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    activated: [],
    langKey: [],
    authorities: [],
    garage: [],
  });

  constructor(
    private userService: UserService,
    private garageService: GarageService,
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(({ user }) => {
      if (user) {
        this.user = user;
        if (this.user.id === undefined) {
          this.user.activated = true;
        }
        this.updateForm(user);

        this.garageService
          .query()
          .pipe(map((res: HttpResponse<IGarage[]>) => res.body || []))
          .subscribe((resBody: IGarage[]) => {
            this.garages = resBody;
          });
      }
    });

    this.userService.authorities().subscribe(authorities => {
      this.authorities = authorities;
    });
  }

  createNewGarage(): void {
    this.router.navigate(['/garage/new']);
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    this.updateUser(this.user);
    if (this.user.id !== undefined) {
      this.userService.update(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    } else {
      this.user.langKey = 'en';
      this.userService.create(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    }
  }

  private updateForm(formUser: User): void {
    if (!formUser.garage) {
      formUser.garage = new Garage();
    }

    this.editGarageForm.patchValue({
      businessName: formUser.garage?.businessName,
      lineAddress1: formUser.garage?.lineAddress1,
      lineAddress2: formUser.garage?.lineAddress2,
      city: formUser.garage?.city,
      county: formUser.garage?.county,
      postcode: formUser.garage?.postcode,
      country: formUser.garage?.country,
    });

    this.editForm.patchValue({
      id: formUser.id,
      login: formUser.login,
      firstName: formUser.firstName,
      lastName: formUser.lastName,
      email: formUser.email,
      activated: formUser.activated,
      langKey: formUser.langKey,
      authorities: formUser.authorities,
      garage: formUser.garage,
    });
  }

  private updateUser(user: User): void {
    user.login = this.editForm.get(['login'])!.value;
    user.firstName = this.editForm.get(['firstName'])!.value;
    user.lastName = this.editForm.get(['lastName'])!.value;
    user.email = this.editForm.get(['email'])!.value;
    user.activated = this.editForm.get(['activated'])!.value;
    user.langKey = this.editForm.get(['langKey'])!.value;
    user.authorities = this.editForm.get(['authorities'])!.value;
    user.garage = this.editForm.get(['garage'])!.value;
  }

  private onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  private onSaveError(): void {
    this.isSaving = false;
  }

  public hasGarageAdminRole(): boolean {
    const formAuthorities: string[] = this.editForm.get(['authorities'])!.value;
    return formAuthorities && (formAuthorities.includes(Authority.GARAGE_ADMIN) || formAuthorities.includes(Authority.GARAGE_OWNER));
  }

  trackById(index: number, item: IGarage): any {
    return item.id;
  }
}
