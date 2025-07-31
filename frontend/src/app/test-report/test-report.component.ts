import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-test-report',
  templateUrl: './test-report.component.html'
})
export class TestReportComponent implements OnInit {
  Object = Object; // Add this line to make Object available in the template

  taskId!: number; // Add the ! here
  report: any = null;
  loading = true;
  error = false;
  attributeForm: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private fb: FormBuilder
  ) {
    this.attributeForm = this.fb.group({
      attributeName: ['', Validators.required]
    });
  }

  ngOnInit() {
    const taskIdParam = this.route.snapshot.paramMap.get('taskId');
    if (taskIdParam) {
      this.taskId = Number(taskIdParam);
      this.loadTestReport();
    } else {
      // Handle missing task ID
      this.error = true;
      this.loading = false;
    }
  }

  loadTestReport(): void {
    this.loading = true;
    this.http.get<any>(`http://localhost:8090/api/test-reports/task/${this.taskId}`)
      .subscribe({
        next: (data) => {
          this.report = data;
          this.loading = false;
        },
        error: () => {
          this.error = true;
          this.loading = false;
        }
      });
  }

  toggleAttribute(attributeName: string, currentValue: boolean): void {
    this.http.put<any>(
      `http://localhost:8090/api/test-reports/${this.report.reportId}/attributes/${attributeName}/isMet?isMet=${!currentValue}`,
      {}
    ).subscribe({
      next: (updatedReport) => {
        this.report = updatedReport;
      },
      error: () => {
        // Handle error
      }
    });
  }

  addAttribute(): void {
    if (this.attributeForm.valid) {
      const attributeName = this.attributeForm.get('attributeName')?.value;

      this.http.post<any>(
        `http://localhost:8090/api/test-reports/${this.report.reportId}/attributes?attributeName=${attributeName}&isMet=false`,
        {}
      ).subscribe({
        next: (updatedReport) => {
          this.report = updatedReport;
          this.attributeForm.reset();
        },
        error: () => {
          // Handle error
        }
      });
    }
  }

  deleteAttribute(attributeName: string): void {
    this.http.delete<any>(
      `http://localhost:8090/api/test-reports/${this.report.reportId}/attributes/${attributeName}`
    ).subscribe({
      next: (updatedReport) => {
        this.report = updatedReport;
      },
      error: () => {
        // Handle error
      }
    });
  }
}
