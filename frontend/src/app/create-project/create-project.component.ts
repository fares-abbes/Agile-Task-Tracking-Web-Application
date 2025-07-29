import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html'
})
export class CreateProjectComponent implements OnInit {
  createProjectForm = new FormGroup({
    projectName: new FormControl('', Validators.required),
    description: new FormControl('', Validators.required),
    status: new FormControl('TODO', Validators.required), // Default value set here
    startDate: new FormControl('', Validators.required),
    endDate: new FormControl('', Validators.required),
    clientId: new FormControl('', Validators.required)
  });

  clients: any[] = [];
  errorMessage = '';
  loading = false;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    // Fetch clients for dropdown
    this.http.get<any[]>('http://localhost:8090/api/clients/GetAllClients').subscribe({
      next: (data) => this.clients = data,
      error: () => this.errorMessage = 'Failed to load clients.'
    });
  }

  onSubmit() {
    if (this.createProjectForm.invalid) return;
    this.loading = true;
    this.errorMessage = '';

    // Get connected user (team lead) from localStorage
    const currentUserStr = localStorage.getItem('currentUser');
    if (!currentUserStr) {
      this.errorMessage = 'No connected user.';
      this.loading = false;
      return;
    }
    const currentUser = JSON.parse(currentUserStr);

    // Prepare payload
    const payload = { ...this.createProjectForm.value };

    this.http.post(
      `http://localhost:8090/api/projects/add/${currentUser.id}/${this.createProjectForm.value.clientId}`,
      payload
    ).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/projects']);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Failed to create project.';
      }
    });
  }
}
