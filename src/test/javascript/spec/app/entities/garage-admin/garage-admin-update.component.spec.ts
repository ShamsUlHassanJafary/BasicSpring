import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { Gms4UTestModule } from '../../../test.module';
import { GarageAdminUpdateComponent } from 'app/entities/garage-admin/garage-admin-update.component';
import { GarageAdminService } from 'app/entities/garage-admin/garage-admin.service';
import { GarageAdmin } from 'app/shared/model/garage-admin.model';

describe('Component Tests', () => {
  describe('GarageAdmin Management Update Component', () => {
    let comp: GarageAdminUpdateComponent;
    let fixture: ComponentFixture<GarageAdminUpdateComponent>;
    let service: GarageAdminService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [GarageAdminUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(GarageAdminUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GarageAdminUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(GarageAdminService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new GarageAdmin(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new GarageAdmin();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
