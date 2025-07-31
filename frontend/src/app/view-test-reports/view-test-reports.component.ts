import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-view-test-reports',
  templateUrl: './view-test-reports.component.html',
  styleUrls: ['./view-test-reports.component.css']
})
export class ViewTestReportsComponent implements OnInit {
  Object = Object;

  reports: any[] = [];
  reportsLoading = false;
  reportsError = false;
  currentUser: any = null;
  expandedIndex: number = -1;

  constructor(private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      this.currentUser = JSON.parse(userStr);
      this.route.queryParams.subscribe(() => {
        this.loadReportsForCurrentUser();
      });
    }
  }

  loadReportsForCurrentUser() {
    if (!this.currentUser || !this.currentUser.id) {
      this.reportsError = true;
      return;
    }
    this.reportsLoading = true;
    this.reportsError = false;
    this.reports = [];

    let url = '';
    if (this.currentUser.role === 'TEAMLEAD') {
      url = `http://localhost:8090/api/test-reports/teamlead/${this.currentUser.id}/reports`;
    } else {
      url = `http://localhost:8090/api/test-reports/developer/${this.currentUser.id}/reports`;
    }

    this.http.get<any[]>(url).subscribe({
      next: (data) => {
        this.reports = data;
        this.reportsLoading = false;
      },
      error: () => {
        this.reportsError = true;
        this.reportsLoading = false;
      }
    });
  }

  toggleDetails(index: number) {
    this.expandedIndex = this.expandedIndex === index ? -1 : index;
  }
}
