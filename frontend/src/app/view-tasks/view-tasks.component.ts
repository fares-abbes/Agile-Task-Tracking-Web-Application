import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms'; // Import FormsModule

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
  isTester = false; // Add this
  selectedStatus?: string;
  selectedImportance?: string;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    const currentUserStr = localStorage.getItem('currentUser');
    if (currentUserStr) {
      this.currentUser = JSON.parse(currentUserStr);
      this.isDeveloper = this.currentUser.role === 'DEVELOPPER';
      this.isTeamLead = this.currentUser.role === 'TEAMLEAD';
      this.isTester = this.currentUser.role === 'TESTER'; // Add this

      this.loadTasks();
    }
  }

  loadTasks() {
    this.loading = true;
    let apiUrl = '';

    if (this.isDeveloper) {
      apiUrl = `http://localhost:8090/api/tasks/developer/${this.currentUser.id}`;
    } else if (this.isTeamLead) {
      apiUrl = `http://localhost:8090/api/tasks/teamlead/${this.currentUser.id}`;
    } else if (this.isTester) {
      // Tester sees tasks assigned to them for testing
      apiUrl = `http://localhost:8090/api/tasks/getTasksByTester/${this.currentUser.id}`;
    } else {
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


  filterTasks(status?: string, importance?: string) {
    if (this.isTester || this.isDeveloper) {
      this.loading = true;
      let params = [];
      if (status) params.push(`status=${status}`);
      if (importance) params.push(`importance=${importance}`);
      const query = params.length ? '?' + params.join('&') : '';
      this.http.get<any[]>(`http://localhost:8090/api/tasks/user/${this.currentUser.id}/filter${query}`)
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

onApprovalToggle(task: any, event: Event) {
  const inputElement = event.target as HTMLInputElement;
  if (!inputElement) return;

  const checked = inputElement.checked;
  const newStatus = checked ? 'Approved' : 'NotApproved';

  this.http.put<any>(`http://localhost:8090/api/tasks/task/${task.taskId}/status/${newStatus}`, {})
    .subscribe({
      next: (updatedTask) => {
        task.status = updatedTask.status; // update status in UI
      },
      error: () => {
        // Optionally show an error message
      }
    });
}

}
