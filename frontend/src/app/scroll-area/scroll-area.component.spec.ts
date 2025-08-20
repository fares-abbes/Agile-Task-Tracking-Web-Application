import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScrollAreaComponent } from './scroll-area.component';

describe('ScrollAreaComponent', () => {
  let component: ScrollAreaComponent;
  let fixture: ComponentFixture<ScrollAreaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ScrollAreaComponent]
    });
    fixture = TestBed.createComponent(ScrollAreaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
