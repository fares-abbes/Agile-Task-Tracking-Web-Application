import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ChartData, ChartOptions } from 'chart.js';

@Component({
  selector: 'app-stats',
  templateUrl: './stats.component.html',
  styleUrls: ['./stats.component.css']
})
export class StatsComponent implements OnInit {
  // Totals
  totals: any = {};
  loadingTotals = true;
  errorTotals = false;

  // Existing “ranking cards” section dependencies in your HTML
  teamMemberRanks: any[] = [];
  loadingRanks = true;
  errorRanks = false;
  // Bar chart: one bar per team user (including 0)
  loadingUsersChart = true;
  errorUsersChart = false;

  barChartType: 'bar' = 'bar';
  barChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      { data: [], label: 'Tasks Worked', backgroundColor: '#6366f1' }
    ]
  };
  barChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      title: { display: true, text: 'Tasks per User (Team)' }
    },
    scales: {
      x: { ticks: { autoSkip: false, maxRotation: 45, minRotation: 0 } },
      y: { beginAtZero: true, ticks: { precision: 0 } }
    }
  };

  currentUser: any = null;
  currentTeamId?: number;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      this.currentUser = JSON.parse(userStr);
      this.currentTeamId = this.currentUser?.team?.teamId;
    }

    this.fetchTotals();
    this.fetchTeamMemberRanks();

    if (this.currentTeamId) {
      this.fetchTasksPerUserInTeam(this.currentTeamId);
    } else if (this.currentUser?.id) {
      // Fallback: get user from backend to resolve teamId
      this.http.get<any>(`http://localhost:8090/api/users/getuser/${this.currentUser.id}`).subscribe({
        next: u => {
          this.currentTeamId = u?.team?.teamId;
          if (this.currentTeamId) {
            this.fetchTasksPerUserInTeam(this.currentTeamId);
          } else {
            this.errorUsersChart = true;
            this.loadingUsersChart = false;
            console.warn('No team found for current user.');
          }
        },
        error: () => {
          this.errorUsersChart = true;
          this.loadingUsersChart = false;
        }
      });
    } else {
      this.errorUsersChart = true;
      this.loadingUsersChart = false;
    }
  }

  // Totals box
  fetchTotals() {
    this.loadingTotals = true;
    this.http.get<any>('http://localhost:8090/api/stats/totals').subscribe({
      next: (data) => { this.totals = data; this.loadingTotals = false; },
      error: () => { this.errorTotals = true; this.loadingTotals = false; }
    });
  }

  // For your existing ranking cards (completed this month)
  fetchTeamMemberRanks() {
    this.loadingRanks = true;
    this.http.get<any[]>('http://localhost:8090/api/tasks/stats/rank-team-members-this-month').subscribe({
      next: (data) => {
        this.teamMemberRanks = data || [];
        this.loadingRanks = false;
      },
      error: () => {
        this.errorRanks = true;
        this.loadingRanks = false;
      }
    });
  }

  // New: returns all team users with their task counts; users with 0 included
  // Backend endpoint expected: GET /api/stats/tasks-per-user?teamId={teamId}
  fetchTasksPerUserInTeam(teamId: number) {
    this.loadingUsersChart = true;

    // If you want “completed this month” only, add: &status=DONE&month=MM&year=YYYY
    const url = `http://localhost:8090/api/stats/tasks-per-user?teamId=${teamId}`;

    this.http.get<any[]>(url).subscribe({
      next: (rows) => {
        const labels = rows.map(r => r.email);
        const values = rows.map(r => Number(r.taskCount || 0));
        this.barChartData = {
          labels,
          datasets: [{ data: values, label: 'Tasks Worked', backgroundColor: '#6366f1' }]
        };
        this.loadingUsersChart = false;
      },
      error: () => {
        this.errorUsersChart = true;
        this.loadingUsersChart = false;
      }
    });
  }
}
