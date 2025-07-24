import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  createdAt: string;
}

@Component({
  selector: 'app-list-users',
  templateUrl: './list-users.component.html',
  styleUrls: ['./list-users.component.css']
})
export class ListUsersComponent implements OnInit {
  users: User[] = [];
  loading: boolean = false;
  errorMessage: string = '';
  editUserId: number | null = null;
  editUsername: string = '';
  editEmail: string = '';
  editRole: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.fetchUsers();
  }

  fetchUsers() {
    this.loading = true;
    this.http.get<User[]>('http://localhost:8090/api/users/getusersList').subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load users.';
        this.loading = false;
      }
    });
  }

  startEdit(user: User) {
    this.editUserId = user.id;
    this.editUsername = user.username;
    this.editEmail = user.email;
    this.editRole = user.role;
  }

  cancelEdit() {
    this.editUserId = null;
    this.editUsername = '';
    this.editEmail = '';
    this.editRole = '';
  }

  updateUser() {
    if (this.editUserId == null) return;
    const updatedUser = {
      username: this.editUsername,
      email: this.editEmail,
      role: this.editRole
    };
    this.http.put<User>(`http://localhost:8090/api/users/updateuser/${this.editUserId}`, updatedUser).subscribe({
      next: () => {
        this.fetchUsers();
        this.cancelEdit();
      },
      error: () => {
        this.errorMessage = 'Failed to update user.';
      }
    });
  }

  deleteUser(id: number) {
    if (!confirm('Are you sure you want to delete this user?')) return;
    this.http.delete(`http://localhost:8090/api/users/deleteuser/${id}`).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== id);
      },
      error: () => {
        this.errorMessage = 'Failed to delete user.';
      }
    });
  }
}
