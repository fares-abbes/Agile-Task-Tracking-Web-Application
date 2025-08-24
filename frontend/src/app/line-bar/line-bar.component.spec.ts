import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LineBarComponent } from './line-bar.component';

describe('LineBarComponent', () => {
  let component: LineBarComponent;
  let fixture: ComponentFixture<LineBarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LineBarComponent]
    });
    fixture = TestBed.createComponent(LineBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
