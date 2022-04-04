import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class SidebarService {
  toggled = false;

  constructor() {}

  toggle(): void {
    this.toggled = !this.toggled;
  }

  getSidebarState(): boolean {
    return this.toggled;
  }

  setSidebarState(state: boolean): void {
    this.toggled = state;
  }
}
