import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PagamentoCarneComponent } from './pagamento-carne.component';

describe('PagamentoCarneComponent', () => {
  let component: PagamentoCarneComponent;
  let fixture: ComponentFixture<PagamentoCarneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PagamentoCarneComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PagamentoCarneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
