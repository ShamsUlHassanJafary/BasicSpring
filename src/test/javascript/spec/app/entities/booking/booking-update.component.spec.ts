import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { Gms4UTestModule } from '../../../test.module';
import { BookingUpdateComponent } from 'app/entities/booking/booking-update.component';
import { BookingService } from 'app/entities/booking/booking.service';
import { Booking } from 'app/shared/model/booking.model';
import { Customer } from 'app/shared/model/customer.model';
import { Quote } from 'app/shared/model/quote.model';
import * as moment from 'moment';
import { Vehicle } from 'app/shared/model/vehicle.model';

describe('Component Tests', () => {
  describe('Booking Management Update Component', () => {
    let comp: BookingUpdateComponent;
    let fixture: ComponentFixture<BookingUpdateComponent>;
    let service: BookingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [BookingUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(BookingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BookingUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BookingService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Booking(123);
        entity.customer = new Customer();
        entity.vehicle = new Vehicle();
        entity.jobs = [];
        const copy = Object.assign({}, entity);
        const quote = new Quote();
        quote.booking = undefined;
        quote.discount = undefined;
        quote.quoteDate = moment();
        quote.quoteTotal = 0;
        copy.quote = Object.assign({}, quote);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: copy })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(copy);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Booking();
        entity.customer = new Customer();
        entity.vehicle = new Vehicle();
        entity.jobs = [];
        const copy = Object.assign({}, entity);
        const quote = new Quote();
        quote.booking = undefined;
        quote.discount = undefined;
        quote.quoteDate = moment();
        quote.quoteTotal = 0;
        copy.quote = Object.assign({}, quote);

        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: copy })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(copy);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
