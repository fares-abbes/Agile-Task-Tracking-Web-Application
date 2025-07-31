import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestReportComponent } from './test-report.component';

describe('TestReportComponent', () => {
  let component: TestReportComponent;
  let fixture: ComponentFixture<TestReportComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestReportComponent]
    });
    fixture = TestBed.createComponent(TestReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
