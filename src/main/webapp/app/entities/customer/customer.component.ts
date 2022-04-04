import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { of, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from './customer.service';
import { CustomerDeleteDialogComponent } from './customer-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { map, flatMap, concatMap } from 'rxjs/operators';
import { GarageAdminService } from '../garage-admin/garage-admin.service';
import { IGarage } from 'app/shared/model/garage.model';

@Component({
  selector: 'jhi-customer',
  templateUrl: './customer.component.html',
})
export class CustomerComponent implements OnInit, OnDestroy {
  customers?: ICustomer[];
  eventSubscriber?: Subscription;

  filter: any = { searchTag: '' };

  constructor(
    protected customerService: CustomerService,
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
        this.customers = [];
        if (gs)
          this.customerService
            .queryGarageCustomers(gs[0].id)
            .pipe(flatMap((customersResponse: HttpResponse<ICustomer[]>) => of(customersResponse.body)))
            .subscribe(gc => {
              gc?.forEach(c => {
                c.searchTag = [c.email, c.phoneNumber, c.firstName, c.lastName].join(' ');
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

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInCustomers();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ICustomer): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInCustomers(): void {
    this.eventSubscriber = this.eventManager.subscribe('customerListModification', () => this.loadAll());
  }

  delete(customer: ICustomer): void {
    const modalRef = this.modalService.open(CustomerDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.customer = customer;
  }
}
