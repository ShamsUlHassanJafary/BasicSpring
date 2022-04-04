import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { Gms4UTestModule } from '../../../test.module';
import { GarageAdminComponent } from 'app/entities/garage-admin/garage-admin.component';
import { GarageAdminService } from 'app/entities/garage-admin/garage-admin.service';
import { GarageAdmin } from 'app/shared/model/garage-admin.model';

describe('Component Tests', () => {
  describe('GarageAdmin Management Component', () => {
    let comp: GarageAdminComponent;
    let fixture: ComponentFixture<GarageAdminComponent>;
    let service: GarageAdminService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [GarageAdminComponent],
      })
        .overrideTemplate(GarageAdminComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GarageAdminComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(GarageAdminService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new GarageAdmin(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.garageAdmins && comp.garageAdmins[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
