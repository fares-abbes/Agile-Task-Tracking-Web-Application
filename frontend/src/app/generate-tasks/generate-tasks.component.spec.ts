import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateTasksComponent } from './generate-tasks.component';

describe('GenerateTasksComponent', () => {
  let component: GenerateTasksComponent;
  let fixture: ComponentFixture<GenerateTasksComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GenerateTasksComponent]
    });
    fixture = TestBed.createComponent(GenerateTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
