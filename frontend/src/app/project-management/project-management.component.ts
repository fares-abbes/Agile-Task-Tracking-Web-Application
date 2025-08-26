import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-project-management',
  templateUrl: './project-management.component.html',
  styleUrls: ['./project-management.component.css']
})
export class ProjectManagementComponent implements OnInit {
  projects: any[] = [];
  clients: any[] = []; // fetched clients for dropdown
  clientsMap: Record<number, string> = {};
  loading = false;
  error = false;

  showAddForm = false;
  addForm = new FormGroup({
    projectName: new FormControl('', Validators.required),
    description: new FormControl(''),
    clientId: new FormControl(null),
    status: new FormControl('PLANNED'),
    startDate: new FormControl(''),
    endDate: new FormControl('')
  });

  editProjectId: number | null = null;
  editForm = new FormGroup({
    projectName: new FormControl('', Validators.required),
    description: new FormControl(''),
    clientId: new FormControl(null),
    status: new FormControl('PLANNED'),
    startDate: new FormControl(''),
    endDate: new FormControl('')
  });

  pendingDeletes: { project: any; timerId: any; expiresAt: number }[] = [];
  private deleteDelayMs = 5000;

  private baseTeamLead = 'http://localhost:8090/api/projects/teamlead';
  private clientsUrl = 'http://localhost:8090/api/clients/GetAllClients';
  private addUrl = (userId: number, clientId: number | null) =>
    `http://localhost:8090/api/projects/add/${userId}/${clientId ?? 0}`;
  private updateUrl = (id: number) => `http://localhost:8090/api/projects/${id}`;
  private deleteUrl = (id: number) => `http://localhost:8090/api/projects/${id}`;

  teamLeadId?: number;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    // load clients for dropdown immediately
    this.fetchClientsList();

    // resolve connected user id or fallback to route param if desired
    const idFromStorage = this.getUserIdFromLocalStorage();
    if (idFromStorage) {
      this.teamLeadId = idFromStorage;
      this.fetchProjects();
      return;
    }

    // try common server endpoints
    this.http.get<any>('/api/auth/me').pipe(
      catchError(() => this.http.get<any>('/api/users/me').pipe(catchError(() => of(null))))
    ).subscribe({
      next: (me) => {
        const user = me?.user ?? me;
        if (user?.id) {
          this.teamLeadId = Number(user.id);
          this.fetchProjects();
          return;
        }
        this.error = true;
      },
      error: () => this.error = true
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
      } catch { /* ignore */ }
    }
    return null;
  }

  // fetch clients for dropdown
  fetchClientsList() {
    this.http.get<any[]>(this.clientsUrl).pipe(
      catchError(err => { console.error('clients load failed', err); return of([] as any[]); })
    ).subscribe(res => {
      this.clients = Array.isArray(res) ? res : [];
      // build lookup map for clientId -> clientName
      this.clientsMap = {};
      for (const c of this.clients) {
        if (c?.clientId != null) this.clientsMap[Number(c.clientId)] = c.clientName || '';
      }
    });
  }

  fetchProjects() {
    if (!this.teamLeadId) {
      this.error = true;
      return;
    }
    this.loading = true;
    this.error = false;
    const url = `${this.baseTeamLead}/${this.teamLeadId}`;
    this.http.get<any[]>(url).pipe(
      catchError(err => { console.error('projects load failed', err); this.error = true; return of([]); })
    ).subscribe(res => {
      this.projects = Array.isArray(res) ? res : [];
      // attach a display name for client to each project
      for (const p of this.projects) {
        // prefer nested client object if backend returns it
        if (p?.client?.clientName) {
          p.clientNameDisplay = p.client.clientName;
        } else if (p?.clientId != null && this.clientsMap[p.clientId]) {
          p.clientNameDisplay = this.clientsMap[p.clientId];
        } else {
          p.clientNameDisplay = '';
        }
      }
      this.loading = false;
    });
  }

  toggleAdd() {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) this.addForm.reset();
  }

  addProject() {
    if (!this.teamLeadId || this.addForm.invalid) return;
    const selectedClientId = this.addForm.value.clientId ?? 0;

    const payload: any = {
      projectName: this.addForm.value.projectName,
      description: this.addForm.value.description,
      status: this.addForm.value.status,
      // send dates as 'YYYY-MM-DD' (LocalDate)
      startDate: this.addForm.value.startDate || null,
      endDate: this.addForm.value.endDate || null
    };

    this.http.post<any>(this.addUrl(this.teamLeadId, selectedClientId), payload).subscribe({
      next: (p) => {
        this.projects.unshift(p);
        this.addForm.reset({ status: 'PLANNED', clientId: null });
        this.showAddForm = false;
      },
      error: (err) => {
        console.error('add project failed', err);
      }
    });
  }

  startEdit(p: any) {
    this.editProjectId = p.projectId;
    this.editForm.setValue({
      projectName: p.projectName || '',
      description: p.description || '',
      clientId: p.clientId ?? null,
      status: p.status ?? 'PLANNED',
      startDate: p.startDate ?? '',
      endDate: p.endDate ?? ''
    });
  }

  updateProject() {
    if (this.editProjectId === null || this.editForm.invalid) return;
    const payload: any = {
      projectName: this.editForm.value.projectName,
      description: this.editForm.value.description,
      status: this.editForm.value.status,
      startDate: this.editForm.value.startDate || null,
      endDate: this.editForm.value.endDate || null
    };
    this.http.put<any>(this.updateUrl(this.editProjectId), payload).subscribe({
      next: (updated) => {
        const idx = this.projects.findIndex(x => x.projectId === this.editProjectId);
        if (idx !== -1) this.projects[idx] = { ...this.projects[idx], ...updated };
        this.editProjectId = null;
      },
      error: (err) => console.error('update failed', err)
    });
  }

  cancelEdit() {
    this.editProjectId = null;
  }

  // optimistic delete + undo
  deleteProject(projectId: number) {
    const idx = this.projects.findIndex(x => x.projectId === projectId);
    if (idx === -1) return;
    const project = this.projects[idx];
    this.projects.splice(idx, 1);

    const expiresAt = Date.now() + this.deleteDelayMs;
    const timerId = setTimeout(() => this.performDelete(projectId), this.deleteDelayMs);
    this.pendingDeletes.push({ project, timerId, expiresAt });
  }

  private performDelete(projectId: number) {
    const pIdx = this.pendingDeletes.findIndex(p => p.project.projectId === projectId);
    if (pIdx !== -1) this.pendingDeletes.splice(pIdx, 1);

    this.http.delete(this.deleteUrl(projectId)).subscribe({
      next: () => { /* ok */ },
      error: (err) => {
        console.error('delete failed', err);
        this.fetchProjects();
      }
    });
  }

  undoDelete(projectId: number) {
    const pIdx = this.pendingDeletes.findIndex(p => p.project.projectId === projectId);
    if (pIdx === -1) return;
    const pending = this.pendingDeletes[pIdx];
    clearTimeout(pending.timerId);
    this.projects.unshift(pending.project);
    this.pendingDeletes.splice(pIdx, 1);
  }

  secondsLeft(p: { expiresAt: number }) {
    const ms = Math.max(0, p.expiresAt - Date.now());
    return Math.ceil(ms / 1000);
  }
}
