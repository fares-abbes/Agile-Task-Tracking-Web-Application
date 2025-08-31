import { Component, OnInit, Input, OnChanges, AfterViewInit, OnDestroy, SimpleChanges, ViewChild, ElementRef } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

Chart.register(...registerables);

export interface UserTaskRankDTO {
  userId: number;
  username: string;
  teamId: number;
  tasksDone: number;
  teamLeadId?: number;
}

@Component({
  selector: 'app-line-bar',
  templateUrl: './line-bar.component.html',
  styleUrls: ['./line-bar.component.css']
})
export class LineBarComponent implements OnInit, OnChanges, AfterViewInit, OnDestroy {
  @Input() teamId!: number | null;
  @ViewChild('chartCanvas', { static: false }) chartCanvas!: ElementRef<HTMLCanvasElement>;

  chart!: Chart | undefined;
  loading = false;
  error = false;
  hasData = false;
  pendingRows: UserTaskRankDTO[] = [];
  viewInitialized = false;
  currentUserId: number | null = null;

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    console.log('[LineBar] ngOnInit teamId (input) =', this.teamId);

    // Get current user ID first
    this.getCurrentUserId();

    // If no teamId input provided, get it from current user
    if (this.teamId == null && this.currentUserId) {
      this.getUserTeamId();
    } else if (this.teamId == null) {
      // fallback to route param if input not provided
      const param = this.route.snapshot.paramMap.get('teamId');
      console.log('[LineBar] route param teamId =', param);
      if (param) {
        const id = Number(param);
        if (!isNaN(id)) this.teamId = id;
      }
    }

    // Load data if we have teamId
    if (this.teamId != null) {
      this.loadData();
    }
  }

  private getCurrentUserId(): void {
    const currentUser = localStorage.getItem('currentUser');
    if (currentUser) {
      try {
        const user = JSON.parse(currentUser);
        this.currentUserId = user.id || user.userId;
        console.log('[LineBar] Current user ID:', this.currentUserId);
      } catch (error) {
        console.error('[LineBar] Error parsing current user:', error);
      }
    }
  }

  private getUserTeamId(): void {
    if (!this.currentUserId) return;

    const url = `http://localhost:8090/api/users/${this.currentUserId}/team`;
    console.log('[LineBar] Getting team for user:', this.currentUserId);

    this.http.get<any>(url).subscribe({
      next: (response) => {
        console.log('[LineBar] User team response:', response);
        if (response && response.teamId) {
          this.teamId = response.teamId;
          this.loadData();
        } else {
          console.warn('[LineBar] User is not assigned to any team');
          this.error = true;
        }
      },
      error: (err) => {
        console.error('[LineBar] Failed to get user team:', err);
        this.error = true;
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ('teamId' in changes) {
      console.log('[LineBar] ngOnChanges teamId =', this.teamId);
      if (this.teamId != null) this.loadData();
    }
  }

  ngAfterViewInit(): void {
    this.viewInitialized = true;
    if (this.pendingRows.length > 0) {
      this.renderChart(this.pendingRows);
      this.pendingRows = [];
    }
  }

  private loadData(): void {
    if (this.teamId == null) {
      console.warn('[LineBar] loadData called but teamId is null');
      return;
    }
    this.loading = true;
    this.error = false;
    const url = `http://localhost:8090/api/tasks/team/${this.teamId}/rank-done`;
    console.log('[LineBar] loading team stats for teamId=', this.teamId, ' url=', url);
    this.http.get<UserTaskRankDTO[]>(url).subscribe({
      next: (rows) => {
        console.log('[LineBar] backend response:', rows);
        this.loading = false;
        this.hasData = Array.isArray(rows) && rows.length > 0;
        this.pendingRows = rows || [];
        if (this.viewInitialized) this.renderChart(rows || []);
      },
      error: (err) => {
        console.error('[LineBar] Failed to load team stats', err);
        this.loading = false;
        this.error = true;
        this.hasData = false;
      }
    });
  }

  private renderChart(rows: UserTaskRankDTO[]): void {
    const labels = rows.map(m => m.username);
    const values = rows.map(m => m.tasksDone);

    // destroy previous chart
    if (this.chart) {
      this.chart.destroy();
      this.chart = undefined;
    }

    if (!rows || rows.length === 0) {
      return;
    }

    const ctx = this.chartCanvas?.nativeElement.getContext('2d') as CanvasRenderingContext2D;
    this.chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Tasks Done',
            data: values,
            borderColor: '#7c5cff',
            backgroundColor: 'rgba(59,230,255,0.12)',
            fill: true,
            tension: 0.3,
            pointRadius: 6,
            pointBackgroundColor: '#00ff99',
            pointBorderColor: '#ffffff',
            borderWidth: 2
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: true }
        },
        scales: {
          y: {
            beginAtZero: true,
            title: { display: true, text: 'Tasks Completed' }
          },
          x: {
            title: { display: true, text: 'Team Members' }
          }
        }
      }
    });
  }

  ngOnDestroy(): void {
    if (this.chart) {
      this.chart.destroy();
      this.chart = undefined;
    }
  }
}
