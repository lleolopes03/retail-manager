import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LembreteComponent } from './lembrete.component';

describe('LembreteComponent', () => {
  let component: LembreteComponent;
  let fixture: ComponentFixture<LembreteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LembreteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LembreteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
