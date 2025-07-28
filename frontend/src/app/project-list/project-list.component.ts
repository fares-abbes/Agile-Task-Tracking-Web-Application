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

  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
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
}
