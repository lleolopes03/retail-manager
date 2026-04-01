import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComprovanteComponent } from './comprovante.component';

describe('ComprovanteComponent', () => {
  let component: ComprovanteComponent;
  let fixture: ComponentFixture<ComprovanteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComprovanteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComprovanteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
