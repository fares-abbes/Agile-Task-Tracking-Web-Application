import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Location } from '@angular/common';

@Component({
  selector: 'app-project-tasks',
  templateUrl: './project-tasks.component.html'
})
export class ProjectTasksComponent implements OnInit {
  projectId!: number;
  projectName = '';
  tasks: any[] = [];
  loading = true;
  error = false;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private location: Location
  ) {}

  ngOnInit() {
    this.projectId = +this.route.snapshot.paramMap.get('projectId')!;

    // Get project details
    this.http.get<any>(`http://localhost:8090/api/projects/${this.projectId}`).subscribe({
      next: (project) => {
        this.projectName = project.name;
      },
      error: () => {
        this.error = true;
      }
    });

    // Get tasks for this project
    this.http.get<any[]>(`http://localhost:8090/api/tasks/project/${this.projectId}`).subscribe({
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

  goBack() {
    this.location.back();
  }

  getStatusClass(status: string) {
    switch (status) {
      case 'COMPLETED': return 'bg-green-900/50 text-green-400';
      case 'IN_PROGRESS': return 'bg-blue-900/50 text-blue-400';
      case 'TO_DO': return 'bg-yellow-900/50 text-yellow-400';
      default: return 'bg-gray-900/50 text-gray-400';
    }
  }

  getImportanceClass(importance: string) {
    switch (importance) {
      case 'HIGH': return 'bg-red-900/50 text-red-400';
      case 'MEDIUM': return 'bg-orange-900/50 text-orange-400';
      case 'LOW': return 'bg-green-900/50 text-green-400';
      default: return 'bg-gray-900/50 text-gray-400';
    }
  }
}
