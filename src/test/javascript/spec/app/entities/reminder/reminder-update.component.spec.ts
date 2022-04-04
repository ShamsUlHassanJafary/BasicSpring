import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { Gms4UTestModule } from '../../../test.module';
import { ReminderUpdateComponent } from 'app/entities/reminder/reminder-update.component';
import { ReminderService } from 'app/entities/reminder/reminder.service';
import { Reminder } from 'app/shared/model/reminder.model';

describe('Component Tests', () => {
  describe('Reminder Management Update Component', () => {
    let comp: ReminderUpdateComponent;
    let fixture: ComponentFixture<ReminderUpdateComponent>;
    let service: ReminderService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [ReminderUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(ReminderUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ReminderUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ReminderService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Reminder(123);
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
        const entity = new Reminder();
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
