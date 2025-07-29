import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-view-tasks',
  templateUrl: './view-tasks.component.html'
})
export class ViewTasksComponent implements OnInit {
  tasks: any[] = [];
  loading = true;
  error = false;
  currentUser: any;
  isDeveloper = false;
  isTeamLead = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    const currentUserStr = localStorage.getItem('currentUser');
    if (currentUserStr) {
      this.currentUser = JSON.parse(currentUserStr);
      this.isDeveloper = this.currentUser.role === 'DEVELOPPER';
      this.isTeamLead = this.currentUser.role === 'TEAMLEAD';

      this.loadTasks();
    }
  }

  loadTasks() {
    this.loading = true;
    let apiUrl = '';

    if (this.isDeveloper) {
      // Developer sees tasks assigned to them
      apiUrl = `http://localhost:8090/api/tasks/developer/${this.currentUser.id}`;
    } else if (this.isTeamLead) {
      // Team Lead sees tasks they created (from their projects)
      apiUrl = `http://localhost:8090/api/tasks/teamlead/${this.currentUser.id}`;
    } else {
      // Admin or other roles might see all tasks
      apiUrl = `http://localhost:8090/api/tasks`;
    }

    this.http.get<any[]>(apiUrl).subscribe({
      next: (data) => {
        this.tasks = data;
        this.loading = false;
      },
      error: () => {
        this.error = true;
        this.loading = false;
      }
    });
  }

  // Optional: Filter by status for developers
  filterByStatus(status: string) {
    if (this.isDeveloper) {
      this.loading = true;
      this.http.get<any[]>(`http://localhost:8090/api/tasks/developer/${this.currentUser.id}/status/${status}`)
        .subscribe({
          next: (data) => {
            this.tasks = data;
            this.loading = false;
          },
          error: () => {
            this.error = true;
            this.loading = false;
          }
        });
    }
  }

  // Optional: Filter by importance for developers
  filterByImportance(importance: string) {
    if (this.isDeveloper) {
      this.loading = true;
      this.http.get<any[]>(`http://localhost:8090/api/tasks/developer/${this.currentUser.id}/importance/${importance}`)
        .subscribe({
          next: (data) => {
            this.tasks = data;
            this.loading = false;
          },
          error: () => {
            this.error = true;
            this.loading = false;
          }
        });
    }
  }
}
