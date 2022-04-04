import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { AccountService } from 'app/core/auth/account.service';
import { CustomerService } from 'app/entities/customer/customer.service';
import { GarageAdminService } from 'app/entities/garage-admin/garage-admin.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { IGarage } from 'app/shared/model/garage.model';
import { of } from 'rxjs';
import { concatMap, map, flatMap } from 'rxjs/operators';

@Component({
  selector: 'jhi-select-create-customer',
  templateUrl: './select-create-customer.component.html',
})
export class SelectCreateCustomerComponent implements OnInit {
  @Input()
  editBookingForm!: FormGroup;
  customers: ICustomer[] = [];

  newCustomer = false;

  selectMode = true;

  tempCustomer!: ICustomer;

  constructor(
    protected customerService: CustomerService,
    protected garageAdminService: GarageAdminService,
    protected accountService: AccountService
  ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(
        concatMap(account =>
          this.garageAdminService.findGaragesByAccount(account!.login).pipe(map((res: HttpResponse<IGarage[]>) => of(res.body)))
        ),
        concatMap(g => g.pipe(map(garages => garages)))
      )
      .subscribe(gs => {
        const garage = gs && gs.length > 0 ? gs[0] : undefined;

        this.customerService
          .queryGarageCustomers(garage?.id)
          .pipe(flatMap((customersResponse: HttpResponse<ICustomer[]>) => of(customersResponse.body)))
          .subscribe(gc => {
            gc?.forEach(c => {
              c.customerToString = this.customerDetailsToString(c);
              this.customers?.push(c);
            });
            this.customers?.sort((a, b) => {
              const left = a?.id;
              const right = b.id;
              return left && right ? left - right : 0;
            });
          });
      });
  }

  customerDetailsToString(customer: ICustomer): string {
    if (customer.firstName && customer.lastName) return customer.firstName + ' ' + customer.lastName;
    else return '';
  }
}
