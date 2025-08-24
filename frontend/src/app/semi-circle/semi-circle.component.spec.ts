import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SemiCircleComponent } from './semi-circle.component';

describe('SemiCircleComponent', () => {
  let component: SemiCircleComponent;
  let fixture: ComponentFixture<SemiCircleComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SemiCircleComponent]
    });
    fixture = TestBed.createComponent(SemiCircleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
