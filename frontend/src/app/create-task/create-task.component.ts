import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-create-task',
  templateUrl: './create-task.component.html',
  styleUrls: ['./create-task.component.css']
})
export class CreateTaskComponent implements OnInit {
  private baseUrl = 'http://localhost:8090/api';

  task = {
    taksName: '',
    description: '',
    importance: '',
    startDate: '',  // Added startDate
    endDate: '',    // Added endDate
    projectId: null as number | null
  };

  projects: any[] = [];
  loading = false;
  error = '';
  currentUser: any;
  selectedProjectId: string = '';

  constructor(
    private http: HttpClient,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.getCurrentUser();
  }

  getCurrentUser(): void {
    const userData = localStorage.getItem('currentUser');

    if (userData) {
      this.currentUser = JSON.parse(userData);
      console.log('Current user:', this.currentUser);

      if (this.currentUser.role === 'TEAM_LEAD') {
        this.loadTeamLeadProjects();
      } else {
        this.loadAllProjects();
      }
    } else {
      this.error = 'User not authenticated';
      this.router.navigate(['/auth']);
    }
  }

  onProjectChange(event: any): void {
    const selectedValue = event.target.value;
    console.log('Selected project value:', selectedValue);
    console.log('Type of selected value:', typeof selectedValue);

    if (selectedValue && selectedValue !== '' && selectedValue !== 'undefined') {
      const numericValue = parseInt(selectedValue, 10);
      if (!isNaN(numericValue)) {
        this.task.projectId = numericValue;
        console.log('Successfully converted projectId:', this.task.projectId);
      } else {
        console.error('Failed to convert to number:', selectedValue);
        this.task.projectId = null;
      }
    } else {
      console.log('No valid project selected');
      this.task.projectId = null;
    }

    this.clearError();
  }

  loadTeamLeadProjects(): void {
    this.loading = true;
    console.log('Loading projects for team lead ID:', this.currentUser.id);

    this.http.get<any[]>(`${this.baseUrl}/projects/teamlead/${this.currentUser.id}`)
      .subscribe({
        next: (data: any) => {
          this.projects = data;
          this.loading = false;
          console.log('Team lead projects loaded:', data);
        },
        error: (error: any) => {
          console.error('Error loading team lead projects:', error);
          this.error = 'Failed to load projects. Please try again.';
          this.loading = false;
        }
      });
  }

  loadAllProjects(): void {
    this.loading = true;

    this.http.get<any[]>(`${this.baseUrl}/projects`)
      .subscribe({
        next: (data: any) => {
          this.projects = data;
          this.loading = false;
          console.log('All projects loaded:', data);
        },
        error: (error: any) => {
          console.error('Error loading projects:', error);
          this.error = 'Failed to load projects. Please try again.';
          this.loading = false;
        }
      });
  }

  onSubmit(): void {
    if (this.validateForm()) {
      this.loading = true;

      // Format dates to match backend expectations
      const taskData = {
        taksName: this.task.taksName,
        description: this.task.description,
        importance: this.task.importance,
        startDate: this.formatDate(this.task.startDate),  // Format date
        endDate: this.formatDate(this.task.endDate),      // Format date
        projectId: this.task.projectId,
        createdById: this.currentUser.id
      };

      console.log('Creating task with formatted data:', taskData);

      this.http.post<any>(`${this.baseUrl}/tasks/create`, taskData)
        .subscribe({
          next: (response: any) => {
            console.log('Task created successfully:', response);
            this.router.navigate(['/my-tasks']);
          },
          error: (error: any) => {
            console.error('Full error object:', error);
            console.error('Error status:', error.status);
            console.error('Error body:', error.error);

            if (error.status === 400) {
              if (error.error && error.error.message) {
                this.error = `Validation error: ${error.error.message}`;
              } else {
                this.error = 'Invalid data format. Please check your backend validation.';
              }
            } else {
              this.error = `Server error: ${error.status}`;
            }

            this.loading = false;
          }
        });
    }
  }

  // Add this helper method
  formatDate(dateString: string): string {
    if (!dateString) return '';

    // Convert YYYY-MM-DD to backend expected format
    const date = new Date(dateString);
    return date.toISOString().split('T')[0]; // Returns YYYY-MM-DD
  }

  validateForm(): boolean {
    if (!this.task.taksName) {
      this.error = 'Task name is required';
      return false;
    }

    if (!this.task.projectId) {
      this.error = 'Please select a project';
      return false;
    }

    if (!this.task.startDate) {
      this.error = 'Start date is required';
      return false;
    }

    if (!this.task.endDate) {
      this.error = 'End date is required';
      return false;
    }

    // Validate that end date is after start date
    if (new Date(this.task.endDate) <= new Date(this.task.startDate)) {
      this.error = 'End date must be after start date';
      return false;
    }

    this.error = '';
    return true;
  }

  clearError(): void {
    this.error = '';
  }

  navigateToMyTasks(): void {
    this.router.navigate(['/my-tasks']);
  }

  navigateToAuth(): void {
    this.router.navigate(['/auth']);
  }
}
