import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { Gms4UTestModule } from '../../../test.module';
import { GarageDetailComponent } from 'app/entities/garage/garage-detail.component';
import { Garage } from 'app/shared/model/garage.model';

describe('Component Tests', () => {
  describe('Garage Management Detail Component', () => {
    let comp: GarageDetailComponent;
    let fixture: ComponentFixture<GarageDetailComponent>;
    const route = ({ data: of({ garage: new Garage(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [GarageDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(GarageDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(GarageDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load garage on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.garage).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
