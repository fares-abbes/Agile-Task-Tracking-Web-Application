import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewTestReportsComponent } from './view-test-reports.component';

describe('ViewTestReportsComponent', () => {
  let component: ViewTestReportsComponent;
  let fixture: ComponentFixture<ViewTestReportsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewTestReportsComponent]
    });
    fixture = TestBed.createComponent(ViewTestReportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
