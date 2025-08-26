import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

interface Task {
  taskId: number;
  taksName?: string;
  project?: { projectName?: string };
  assignedTo?: { username?: string };
  status?: string;
  importance?: string;
  date_debut?: string;
  date_fin?: string;
}

@Component({
  selector: 'app-teamlead-board',
  templateUrl: './teamlead-board.component.html',
  styleUrls: ['./teamlead-board.component.css']
})
export class TeamleadBoardComponent implements OnInit {
  teamLeadId?: number;
  loading = true;
  counts = {
    pending: 0,
    completed: 0,
    highPriority: 0,
    members: 0
  };
  tasks: Task[] = [];
  error = false;

  constructor(private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit(): void {
    // Prefer connected user id (from localStorage or server). Fallback to route param.
    const idFromStorage = this.getUserIdFromLocalStorage();
    if (idFromStorage) {
      this.teamLeadId = idFromStorage;
      this.loadDataForTeamLead();
      return;
    }

    // try server endpoints to get current user
    this.http.get<any>('/api/auth/me').pipe(
      catchError(() => this.http.get<any>('/api/users/me').pipe(catchError(() => of(null))))
    ).subscribe({
      next: (me) => {
        if (me) {
          const user = me?.user ?? me;
          if (user?.id) {
            this.teamLeadId = Number(user.id);
            this.loadDataForTeamLead();
            return;
          }
        }
        // fallback to route param if no connected user found
        const param = this.route.snapshot.paramMap.get('teamLeadId');
        if (param) {
          const id = Number(param);
          if (!isNaN(id)) {
            this.teamLeadId = id;
            this.loadDataForTeamLead();
            return;
          }
        }
        this.error = true;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to resolve current user', err);
        // fallback to route param
        const param = this.route.snapshot.paramMap.get('teamLeadId');
        if (param) {
          const id = Number(param);
          if (!isNaN(id)) {
            this.teamLeadId = id;
            this.loadDataForTeamLead();
            return;
          }
        }
        this.error = true;
        this.loading = false;
      }
    });
  }

  private getUserIdFromLocalStorage(): number | null {
    const keys = ['currentUser', 'user', 'authUser'];
    for (const k of keys) {
      const raw = localStorage.getItem(k);
      if (!raw) continue;
      try {
        const obj = JSON.parse(raw);
        const user = obj?.user ?? obj;
        if (user?.id) return Number(user.id);
      } catch {
        // ignore parse errors
      }
    }
    return null;
  }

  private loadDataForTeamLead() {
    if (!this.teamLeadId) {
      this.error = true;
      this.loading = false;
      return;
    }

    const base = `http://localhost:8090/api/tasks/teamlead/${this.teamLeadId}`;

    const pending$ = this.http.get<number>(`${base}/counts/pending`).pipe(catchError(() => of(0)));
    const completed$ = this.http.get<number>(`${base}/counts/completed`).pipe(catchError(() => of(0)));
    const high$ = this.http.get<number>(`${base}/counts/high-priority`).pipe(catchError(() => of(0)));
    const members$ = this.http.get<number>(`${base}/members/count`).pipe(catchError(() => of(0)));
    const tasks$ = this.http.get<Task[]>(`${base}/tasks/not-approved`).pipe(catchError(() => of([] as Task[])));

    forkJoin({ pending: pending$, completed: completed$, high: high$, members: members$, tasks: tasks$ })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (res) => {
          this.counts.pending = res.pending ?? 0;
          this.counts.completed = res.completed ?? 0;
          this.counts.highPriority = res.high ?? 0;
          this.counts.members = res.members ?? 0;
          this.tasks = Array.isArray(res.tasks) ? res.tasks : [];
          this.error = false;
        },
        error: (err) => {
          console.error('TeamleadBoard load error', err);
          this.error = true;
        }
      });
  }
}
