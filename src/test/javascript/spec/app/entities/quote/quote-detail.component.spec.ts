import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { Gms4UTestModule } from '../../../test.module';
import { QuoteDetailComponent } from 'app/entities/quote/quote-detail.component';
import { Quote } from 'app/shared/model/quote.model';

describe('Component Tests', () => {
  describe('Quote Management Detail Component', () => {
    let comp: QuoteDetailComponent;
    let fixture: ComponentFixture<QuoteDetailComponent>;
    const route = ({ data: of({ quote: new Quote(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Gms4UTestModule],
        declarations: [QuoteDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(QuoteDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(QuoteDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load quote on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.quote).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
