import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

export interface GeneratedTask {
  task: string;
  description?: string;
  complexity?: number;
}

export interface ProjectHistoryItem {
  id: number;
  title?: string;
  description?: string;
  tasksJson?: string;
  createdAt?: string;
  updatedAt?: string;
}

@Component({
  selector: 'app-generate-tasks',
  templateUrl: './generate-tasks.component.html',
  styleUrls: ['./generate-tasks.component.css']
})
export class GenerateTasksComponent implements OnInit {
  form: FormGroup;
  loading = false;
  saving = false;
  errorMessage: string | null = null;
  tasks: GeneratedTask[] = [];
  showRaw = false;
  history: ProjectHistoryItem[] = [];
  selectedHistoryId?: number;

  searchTerm: string = '';

  private base = 'http://localhost:8090/api/ai';

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.form = this.fb.group({
      title: [''],
      description: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngOnInit(): void {
    this.loadHistory();
  }

  get descriptionControl() {
    return this.form.controls['description'];
  }

  get filteredHistory(): ProjectHistoryItem[] {
    const q = (this.searchTerm || '').trim().toLowerCase();
    if (!q) return this.history;
    return this.history.filter(h =>
      ((h.title ?? '') + ' ' + (h.description ?? '')).toLowerCase().includes(q)
    );
  }

  loadHistory() {
    this.http.get<ProjectHistoryItem[]>(`${this.base}/history?userId=1`)
      .subscribe({
        next: h => this.history = h || [],
        error: err => console.error('history load failed', err)
      });
  }

  loadFromHistory(item: ProjectHistoryItem) {
    this.selectedHistoryId = item.id;
    this.form.patchValue({ title: item.title ?? '', description: item.description ?? '' });
    if (item.tasksJson) {
      try { this.tasks = JSON.parse(item.tasksJson); } catch { this.tasks = []; }
    } else {
      this.tasks = [];
    }
  }

  generate() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.errorMessage = null;
    this.tasks = [];
    const body = { title: this.form.value.title, description: this.form.value.description };
    this.http.post<any>(`${this.base}/generate-tasks?userId=1`, body)
      .subscribe({
        next: res => {
          // server may return { projectId, tasks }
          this.tasks = Array.isArray(res.tasks) ? res.tasks : (Array.isArray(res) ? res : []);
          if (res.projectId) this.selectedHistoryId = res.projectId;
          this.loadHistory();
          this.loading = false;
        },
        error: err => {
          console.error(err);
          this.errorMessage = err?.error || err?.message || 'Failed to generate tasks.';
          this.loading = false;
        }
      });
  }

  // Save current description + tasks to history without regenerating
  saveCurrent() {
    if (!this.form.value.description || this.tasks.length === 0) return;
    this.saving = true;
    const payload = {
      title: this.form.value.title,
      description: this.form.value.description,
      tasks: this.tasks
    };
    // Backend must implement POST /api/ai/history to accept this payload and return saved project
    this.http.post<any>(`${this.base}/history`, payload)
      .subscribe({
        next: res => {
          // expect saved item { id, createdAt, ... }
          if (res && res.id) this.selectedHistoryId = res.id;
          this.loadHistory();
          this.saving = false;
        },
        error: err => {
          console.error('save failed', err);
          this.errorMessage = 'Save failed';
          this.saving = false;
        }
      });
  }

  clear() {
    this.form.reset();
    this.tasks = [];
    this.errorMessage = null;
    this.showRaw = false;
    this.selectedHistoryId = undefined;
  }

  copyJSON() {
    const json = JSON.stringify(this.tasks, null, 2);
    navigator.clipboard.writeText(json).catch(() => alert('Failed to copy'));
  }

  downloadJSON() {
    const json = JSON.stringify(this.tasks, null, 2);
    const blob = new Blob([json], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'tasks.json';
    a.click();
    URL.revokeObjectURL(url);
  }
}
