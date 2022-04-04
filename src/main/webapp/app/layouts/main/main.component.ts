import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, ActivatedRouteSnapshot, NavigationEnd, NavigationError } from '@angular/router';

import { AccountService } from 'app/core/auth/account.service';
import { SidebarService } from '../navbar/sidebar.service';

@Component({
  selector: 'jhi-main',
  templateUrl: './main.component.html',
  styleUrls: ['main.scss'],
})
export class MainComponent implements OnInit {
  constructor(
    private accountService: AccountService,
    private titleService: Title,
    private router: Router,
    private sidebarService: SidebarService
  ) {}

  ngOnInit(): void {
    // try to log in automatically
    this.accountService.identity().subscribe();

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateTitle();

        if (!this.isAuthenticated()) {
          this.hideSidebar();
        }
      }
      if (event instanceof NavigationError && event.error.status === 404) {
        this.router.navigate(['/404']);
      }
    });
  }

  private getPageTitle(routeSnapshot: ActivatedRouteSnapshot): string {
    let title: string = routeSnapshot.data && routeSnapshot.data['pageTitle'] ? routeSnapshot.data['pageTitle'] : '';
    if (routeSnapshot.firstChild) {
      title = this.getPageTitle(routeSnapshot.firstChild) || title;
    }
    return title;
  }

  private updateTitle(): void {
    let pageTitle = this.getPageTitle(this.router.routerState.snapshot.root);
    if (!pageTitle) {
      pageTitle = 'Gms4u';
    }
    this.titleService.setTitle(pageTitle);
  }

  toggleSidebar(): void {
    this.sidebarService.setSidebarState(!this.sidebarService.getSidebarState());
  }

  getSideBarState(): boolean {
    return this.sidebarService.getSidebarState();
  }

  hideSidebar(): void {
    this.sidebarService.setSidebarState(true);
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }
}
