import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import './vendor';
import { Gms4USharedModule } from 'app/shared/shared.module';
import { Gms4UCoreModule } from 'app/core/core.module';
import { Gms4UAppRoutingModule } from './app-routing.module';
import { Gms4UHomeModule } from './home/home.module';
import { Gms4UEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';
@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    Gms4USharedModule,
    Gms4UCoreModule,
    Gms4UHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    Gms4UEntityModule,
    Gms4UAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class Gms4UAppModule {}
