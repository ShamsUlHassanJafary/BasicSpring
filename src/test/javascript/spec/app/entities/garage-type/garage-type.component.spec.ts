import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { Gms4UTestModule } from '../../../test.module';
import { GarageTypeComponent } from 'app/entities/garage-type/garage-type.component';
import { GarageTypeService } from 'app/entities/garage-type/garage-type.service';
import { GarageType } from 'app/shared/model/garage-type.model';

describe('Component Tests', () => {
  describe('GarageType Management Component', () => {
    let comp: GarageTypeComponent;
    let fixture: ComponentFixture<GarageTypeComponent>;
    let service: GarageTypeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [GarageTypeComponent],
      })
        .overrideTemplate(GarageTypeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GarageTypeComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(GarageTypeService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new GarageType(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.garageTypes && comp.garageTypes[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
