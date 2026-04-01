import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PagamentoDinheiroComponent } from './pagamento-dinheiro.component';

describe('PagamentoDinheiroComponent', () => {
  let component: PagamentoDinheiroComponent;
  let fixture: ComponentFixture<PagamentoDinheiroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PagamentoDinheiroComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PagamentoDinheiroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
