import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { Gms4UTestModule } from '../../../test.module';
import { GarageAdminDetailComponent } from 'app/entities/garage-admin/garage-admin-detail.component';
import { GarageAdmin } from 'app/shared/model/garage-admin.model';

describe('Component Tests', () => {
  describe('GarageAdmin Management Detail Component', () => {
    let comp: GarageAdminDetailComponent;
    let fixture: ComponentFixture<GarageAdminDetailComponent>;
    const route = ({ data: of({ garageAdmin: new GarageAdmin(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [GarageAdminDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(GarageAdminDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(GarageAdminDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load garageAdmin on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.garageAdmin).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
