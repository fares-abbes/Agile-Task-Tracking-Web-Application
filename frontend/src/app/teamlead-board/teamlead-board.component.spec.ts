import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamleadBoardComponent } from './teamlead-board.component';

describe('TeamleadBoardComponent', () => {
  let component: TeamleadBoardComponent;
  let fixture: ComponentFixture<TeamleadBoardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TeamleadBoardComponent]
    });
    fixture = TestBed.createComponent(TeamleadBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
