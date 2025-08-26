import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingpageGreenComponent } from './landingpage-green.component';

describe('LandingpageGreenComponent', () => {
  let component: LandingpageGreenComponent;
  let fixture: ComponentFixture<LandingpageGreenComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LandingpageGreenComponent]
    });
    fixture = TestBed.createComponent(LandingpageGreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
