import { Component, Input, OnInit, AfterViewInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Chart, registerables } from 'chart.js';
import { ActivatedRoute } from '@angular/router';

Chart.register(...registerables);

export interface ProjectProgressDTO {
  projectId: number;
  projectName: string;
  totalTasks: number;
  doneApprovedCount: number;
  percentage: number; // 0..100
}

@Component({
  selector: 'app-semi-circle',
  templateUrl: './semi-circle.component.html',
  styleUrls: ['./semi-circle.component.css']
})
export class SemiCircleComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() teamLeadId?: number | null;

  projects: ProjectProgressDTO[] = [];
  currentIndex = 0;
  loading = false;
  error = false;

  @ViewChild('chartCanvas', { static: false }) chartCanvas!: ElementRef<HTMLCanvasElement>;
  chart?: Chart<'doughnut', number[], string>;

  constructor(private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit(): void {
    // fallback to route param if no @Input provided
    if (this.teamLeadId == null) {
      const param = this.route.snapshot.paramMap.get('teamLeadId');
      if (param) {
        const id = Number(param);
        if (!isNaN(id)) this.teamLeadId = id;
      }
    }
    if (this.teamLeadId != null) {
      this.loadProjects(this.teamLeadId);
    } else {
      console.warn('SemiCircle: missing teamLeadId (provide via [teamLeadId] or /route param)');
    }
  }

  ngAfterViewInit(): void {
    // if data already loaded, render first chart
    if (this.projects.length > 0) {
      this.renderChartForIndex(this.currentIndex);
    }
  }

  ngOnDestroy(): void {
    if (this.chart) this.chart.destroy();
  }

  loadProjects(teamLeadId: number) {
    this.loading = true;
    this.error = false;
    const url = `http://localhost:8090/api/tasks/teamlead/${teamLeadId}/project-progress`;
    this.http.get<ProjectProgressDTO[]>(url).subscribe({
      next: (rows) => {
        this.loading = false;
        this.projects = Array.isArray(rows) ? rows : [];
        this.currentIndex = 0;
        if (this.projects.length > 0 && this.chartCanvas) {
          this.renderChartForIndex(this.currentIndex);
        }
      },
      error: (err) => {
        console.error('SemiCircle: failed to load projects', err);
        this.loading = false;
        this.error = true;
      }
    });
  }

  prev() {
    if (this.projects.length === 0) return;
    this.currentIndex = (this.currentIndex - 1 + this.projects.length) % this.projects.length;
    this.renderChartForIndex(this.currentIndex);
  }

  next() {
    if (this.projects.length === 0) return;
    this.currentIndex = (this.currentIndex + 1) % this.projects.length;
    this.renderChartForIndex(this.currentIndex);
  }

  private renderChartForIndex(index: number) {
    const project = this.projects[index];
    if (!project) return;

    const pct = Math.max(0, Math.min(100, Math.round(project.percentage * 10) / 10));

    // Create segments for the semi-circle gauge
    const segmentData: number[] = [];
    const segmentColors: string[] = [];

    // Colors matching the expense tracker design
    const completedColor = '#22c55e';      // Green
    const inProgressColor = '#84cc16';      // Lime
    const pendingColor = '#64748b';         // Gray
    const emptyColor = 'rgba(45, 45, 45, 0.8)'; // Dark gray

    // Calculate segments based on completion percentage
    if (pct >= 80) {
      // Mostly completed - show green
      segmentData.push(pct * 0.8);
      segmentColors.push(completedColor);
      if (pct < 100) {
        segmentData.push(pct * 0.2);
        segmentColors.push(inProgressColor);
      }
    } else if (pct >= 40) {
      // In progress - show green and lime
      segmentData.push(pct * 0.6);
      segmentColors.push(completedColor);
      segmentData.push(pct * 0.4);
      segmentColors.push(inProgressColor);
    } else if (pct > 0) {
      // Early stage - show mostly pending
      segmentData.push(pct * 0.5);
      segmentColors.push(inProgressColor);
      segmentData.push(pct * 0.5);
      segmentColors.push(pendingColor);
    }

    // Add empty segment if not 100%
    if (pct < 100) {
      segmentData.push(100 - pct);
      segmentColors.push(emptyColor);
    }

    // Destroy previous chart
    if (this.chart) {
      this.chart.destroy();
      this.chart = undefined;
    }

    // Create the chart
      const ctx = this.chartCanvas?.nativeElement.getContext('2d') as CanvasRenderingContext2D;
      this.chart = new Chart(ctx, {
        type: 'doughnut',
        data: {
          datasets: [{
            data: segmentData,
            backgroundColor: segmentColors,
            // smaller gap and slightly tighter corners
            borderWidth: 5,                // reduced gap thickness
            borderColor: '#0b1220',       // background color for gap
            borderRadius: 100                // slightly smaller rounding
          }]
        },
        options: {
          // make the ring thinner so segments look smaller
          cutout: '82%',                 // increased inner radius (thinner ring)
          rotation: 270,
          circumference: 180 ,
          responsive: true,
          maintainAspectRatio: true,
          plugins: {
            legend: { display: true },
            tooltip: { enabled: false }
          },
          elements: {
            arc: { borderWidth: 0}
          }
        }
      }) as Chart<'doughnut', number[], string>;
    }

    // helpers used in template
  get currentProjectName(): string {
    return this.projects[this.currentIndex]?.projectName ?? '';
  }

  get currentPercentage(): number {
    return Math.round((this.projects[this.currentIndex]?.percentage ?? 0) * 10) / 10;
  }

  get hasProjects(): boolean {
    return this.projects && this.projects.length > 0;
  }
}
