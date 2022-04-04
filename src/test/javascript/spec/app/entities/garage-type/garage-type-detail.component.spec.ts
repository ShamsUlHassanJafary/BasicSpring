import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { Gms4UTestModule } from '../../../test.module';
import { GarageTypeDetailComponent } from 'app/entities/garage-type/garage-type-detail.component';
import { GarageType } from 'app/shared/model/garage-type.model';

describe('Component Tests', () => {
  describe('GarageType Management Detail Component', () => {
    let comp: GarageTypeDetailComponent;
    let fixture: ComponentFixture<GarageTypeDetailComponent>;
    const route = ({ data: of({ garageType: new GarageType(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [GarageTypeDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(GarageTypeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(GarageTypeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load garageType on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.garageType).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
