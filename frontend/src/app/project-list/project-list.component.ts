import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.component.html'
})
export class ProjectListComponent implements OnInit {
  projects: any[] = [];
  loading = true;
  error = false;
  isTeamLead = false;
  teamLeadId: number | null = null;

  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    // Detect if connected user is a TEAMLEAD
    const currentUserStr = localStorage.getItem('currentUser');
    if (currentUserStr) {
      const currentUser = JSON.parse(currentUserStr);
      this.isTeamLead = currentUser.role === 'TEAMLEAD';
      this.teamLeadId = currentUser.id;
    }

    this.route.queryParams.subscribe(params => {
      if (params['type'] === 'all') {
        // Admin: get all projects
        this.http.get<any[]>('http://localhost:8090/api/projects').subscribe({
          next: (data) => {
            this.projects = data;
            this.loading = false;
          },
          error: () => {
            this.error = true;
            this.loading = false;
          }
        });       
      } else if (params['teamLeadId']) {
        // TeamLead: get projects by teamLeadId
        this.http.get<any[]>(`http://localhost:8090/api/projects/teamlead/${params['teamLeadId']}`).subscribe({
          next: (data) => {
            this.projects = data;
            this.loading = false;
          },
          error: () => {
            this.error = true;
            this.loading = false;
          }
        });
      } else {
        this.projects = [];
        this.loading = false;
      }
    });
  }

  viewTasks(projectId: any) {
    if (projectId === undefined || projectId === null) {
      return;
    }
    this.router.navigate(['/projects', projectId, 'tasks']);
  }

  addTask(projectId: any) {
    if (projectId === undefined || projectId === null || !this.teamLeadId) {
      return;
    }
    // Navigate to your add-task route, passing projectId and teamLeadId as params or query params
    this.router.navigate(['/create-task'], { queryParams: { projectId, teamLeadId: this.teamLeadId } });
  }
}
