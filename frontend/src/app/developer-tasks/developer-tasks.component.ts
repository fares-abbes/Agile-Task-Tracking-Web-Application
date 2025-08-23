  import { Component, OnInit } from '@angular/core';
  import { HttpClient } from '@angular/common/http';
  import { Router } from '@angular/router';

  @Component({
    selector: 'app-developer-tasks',
    templateUrl: './developer-tasks.component.html',
    styleUrls: ['./developer-tasks.component.css']
  })
  export class DeveloperTasksComponent implements OnInit {
    developerId!: number;
    developerName = '';
    currentStatus = 'TODO';
    statuses = ['TODO', 'IN_PROGRESS', 'DONE', 'Approved', 'NotApproved'];
    tasks: any[] = [];
    tasksLoading = false;
    tasksError = false;
    expandedIndex: number | null = null;
    loading = true;
    error = false;

    constructor(
      private http: HttpClient,
      private router: Router
    ) {}

    ngOnInit() {
      // Get current user from localStorage
      const currentUserStr = localStorage.getItem('currentUser');

      if (!currentUserStr) {
        // Redirect to login if not authenticated
        this.router.navigate(['/auth']);
        return;
      }

      try {
        const currentUser = JSON.parse(currentUserStr);
        this.developerId = currentUser.id;
        this.developerName = currentUser.username;

        // Load initial tasks with default status
        this.loadTasks(this.currentStatus);
      } catch (error) {
        console.error('Error parsing user data:', error);
        localStorage.removeItem('currentUser');
        this.router.navigate(['/auth']);
      }
    }

    loadTasks(status: string) {
      this.currentStatus = status;
      this.loading = true;
      this.error = false;

      this.http.get<any[]>(`http://localhost:8090/api/tasks/developer/${this.developerId}/status/${status}`)
        .subscribe({
          next: (data) => {
            this.tasks = data;
            this.loading = false;
          },
          error: (err) => {
            console.error('Error loading tasks:', err);
            this.error = true;
            this.loading = false;
          }
        });
    }

    getStatusClass(status: string): string {
      switch (status) {
        case 'TODO': return 'bg-gray-900/50 text-gray-400';
        case 'IN_PROGRESS': return 'bg-blue-900/50 text-blue-400';
        case 'DONE': return 'bg-green-900/50 text-green-400';
        case 'Approved': return 'bg-indigo-900/50 text-indigo-400';
        case 'NotApproved': return 'bg-red-900/50 text-red-400';
        default: return 'bg-gray-900/50 text-gray-400';
      }
    }

    getPriorityClass(priority: string): string {
      switch (priority) {
        case 'HIGH': return 'bg-red-900/50 text-red-400';
        case 'MEDIUM': return 'bg-orange-900/50 text-orange-400';
        case 'LOW': return 'bg-green-900/50 text-green-400';
        default: return 'bg-gray-900/50 text-gray-400';
      }
    }

    // toggle used by the template to expand/collapse item details
    toggleDetails(i: number): void {
      this.expandedIndex = this.expandedIndex === i ? null : i;
    }
  }
