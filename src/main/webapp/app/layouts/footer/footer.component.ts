import { Component } from '@angular/core';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
})
export class FooterComponent {
  constructor(private accountService: AccountService) {}

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }
}
