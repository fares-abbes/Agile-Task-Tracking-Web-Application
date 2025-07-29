import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-create-task',
  templateUrl: './create-task.component.html',
  styleUrls: ['./create-task.component.css']
})
export class CreateTaskComponent implements OnInit {
  addTaskForm = new FormGroup({
    taskName: new FormControl('', Validators.required),
    description: new FormControl('', Validators.required),
    startDate: new FormControl('', Validators.required),
    endDate: new FormControl('', Validators.required),
    importance: new FormControl('LOW', Validators.required)
  });

  loading = false;
  errorMessage = '';
  projectId: number | null = null;
  teamLeadId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.projectId = params['projectId'];
      this.teamLeadId = params['teamLeadId'];
    });
  }

  onSubmit() {
    if (this.addTaskForm.invalid || !this.projectId || !this.teamLeadId) return;
    this.loading = true;
    this.errorMessage = '';

    this.http.post(
      `http://localhost:8090/api/tasks/create?teamLeadId=${this.teamLeadId}&projectId=${this.projectId}`,
      this.addTaskForm.value
    ).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/projects', this.projectId, 'tasks']);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Failed to create task.';
      }
    });
  }
}
