import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingPageOComponent } from './landing-page-o.component';

describe('LandingPageOComponent', () => {
  let component: LandingPageOComponent;
  let fixture: ComponentFixture<LandingPageOComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LandingPageOComponent]
    });
    fixture = TestBed.createComponent(LandingPageOComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
