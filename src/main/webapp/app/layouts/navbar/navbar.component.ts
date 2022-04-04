import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { VERSION } from 'app/app.constants';
import { AccountService } from 'app/core/auth/account.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { LoginService } from 'app/core/login/login.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { SidebarService } from './sidebar.service';

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['sidebar.scss'],
  animations: [
    trigger('slide', [state('up', style({ height: 0 })), state('down', style({ height: '*' })), transition('up <=> down', animate(200))]),
  ],
})
export class NavbarComponent implements OnInit {
  inProduction?: boolean;
  isNavbarCollapsed = true;
  swaggerEnabled?: boolean;
  version: string;

  menus = [
    { dropdown: 'admin-menu', active: false },
    { dropdown: 'account-menu', active: false },
    { dropdown: 'entity-menu', active: false },
  ];

  constructor(
    private loginService: LoginService,
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private profileService: ProfileService,
    private router: Router,
    private sidebarService: SidebarService
  ) {
    this.version = VERSION ? (VERSION.toLowerCase().startsWith('v') ? VERSION : 'v' + VERSION) : '';
  }

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction = profileInfo.inProduction;
      this.swaggerEnabled = profileInfo.swaggerEnabled;
    });
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed = true;
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['']);
    this.sidebarService.setSidebarState(true);
  }

  toggleNavbar(menu: any): void {
    menu.active = !menu.active;
    // this.isNavbarCollapsed = !this.isNavbarCollapsed;
  }

  getImageUrl(): string {
    return this.isAuthenticated() ? this.accountService.getImageUrl() : '';
  }

  getSideBarState(): boolean {
    return this.sidebarService.getSidebarState();
  }

  // toggle(currentMenu) {
  //   if (currentMenu.type === 'dropdown') {
  //     this.menus.forEach(element => {
  //       if (element === currentMenu) {
  //         currentMenu.active = !currentMenu.active;
  //       } else {
  //         element.active = false;
  //       }
  //     });
  //   }
  // }

  getState(menu: any): string {
    if (menu.active) {
      return 'down';
    } else {
      return 'up';
    }
  }
}
