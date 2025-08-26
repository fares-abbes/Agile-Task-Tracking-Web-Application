import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  userEmail?: string;
  teamLeadId?: number;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    // Try local storage first (common patterns)
    if (!this.loadFromLocalStorage()) {
      // Fallback: request current user from backend (adjust endpoint if needed)
      this.loadFromServer();
    }
  }

  private loadFromLocalStorage(): boolean {
    const keys = ['currentUser', 'user', 'authUser'];
    for (const k of keys) {
      const raw = localStorage.getItem(k);
      if (!raw) continue;
      try {
        const obj = JSON.parse(raw);
        // handle common shapes: { id, email } or { user: { id, email } }
        const user = obj?.user ?? obj;
        if (user?.email) this.userEmail = user.email;
        if (user?.id) this.teamLeadId = Number(user.id);
        // found something useful
        if (this.userEmail || this.teamLeadId) return true;
      } catch {
        // ignore parse errors
      }
    }
    return false;
  }

  private loadFromServer(): void {
    // Try common endpoints. Adjust to your backend.
    this.http.get<any>('/api/auth/me').subscribe({
      next: (u) => this.applyUserObject(u),
      error: () => {
        this.http.get<any>('/api/users/me').subscribe({
          next: (u2) => this.applyUserObject(u2),
          error: () => {
            // silent fallback, template shows default
            console.warn('Sidebar: failed to load current user from server');
          }
        });
      }
    });
  }

  private applyUserObject(u: any) {
    if (!u) return;
    // Accept several possible shapes
    const user = u?.user ?? u;
    if (user?.email) this.userEmail = user.email;
    if (user?.id) this.teamLeadId = Number(user.id);
    // if your auth returns nested fields, adjust accordingly
  }
}
